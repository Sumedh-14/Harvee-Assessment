package utils;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes target/saucedemo-report.html: one self-contained file (inline CSS, failure screenshots
 * embedded as base64) that renders the same in any browser or IDE preview.
 */
public class HtmlReportListener implements IReporter {

    public static final String SCREENSHOT_ATTRIBUTE = "screenshot";
    private static final Path REPORT = Paths.get("target", "saucedemo-report.html");

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        List<ITestResult> results = new ArrayList<>();
        for (ISuite suite : suites) {
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                ITestContext ctx = suiteResult.getTestContext();
                results.addAll(ctx.getPassedTests().getAllResults());
                results.addAll(ctx.getFailedTests().getAllResults());
                results.addAll(ctx.getSkippedTests().getAllResults());
            }
        }
        results.sort(Comparator.comparing((ITestResult r) -> r.getMethod().getMethodName())
                .thenComparing(ITestResult::getStartMillis));

        long passed = results.stream().filter(r -> r.getStatus() == ITestResult.SUCCESS).count();
        long failed = results.stream().filter(r -> r.getStatus() == ITestResult.FAILURE).count();
        long skipped = results.size() - passed - failed;
        long start = results.stream().mapToLong(ITestResult::getStartMillis).min().orElse(0);
        long end = results.stream().mapToLong(ITestResult::getEndMillis).max().orElse(0);
        String suiteName = "SauceDemo Test Report";

        StringBuilder rows = new StringBuilder();
        for (ITestResult r : results) rows.append(row(r));

        String html = TEMPLATE
                .replace("{{suite}}", esc(suiteName))
                .replace("{{date}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
                .replace("{{browser}}", esc(ConfigReader.get("browser")) + (ConfigReader.getBoolean("headless") ? " (headless)" : ""))
                .replace("{{baseUrl}}", esc(ConfigReader.get("baseUrl")))
                .replace("{{total}}", String.valueOf(results.size()))
                .replace("{{passed}}", String.valueOf(passed))
                .replace("{{failed}}", String.valueOf(failed))
                .replace("{{skipped}}", String.valueOf(skipped))
                .replace("{{duration}}", seconds(end - start))
                .replace("{{overall}}", failed > 0 ? "fail" : "pass")
                .replace("{{overallText}}", failed > 0 ? failed + " FAILED" : "ALL PASSED")
                .replace("{{rows}}", rows);
        try {
            Files.createDirectories(REPORT.getParent());
            Files.writeString(REPORT, html);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.out.println("HTML report: " + REPORT.toAbsolutePath());
    }

    private static String row(ITestResult r) {
        String status = switch (r.getStatus()) {
            case ITestResult.SUCCESS -> "pass";
            case ITestResult.FAILURE -> "fail";
            default -> "skip";
        };
        String name = r.getMethod().getMethodName();
        String id = name.contains("_") ? name.substring(0, name.indexOf('_')) : "";
        String description = r.getMethod().getDescription() == null ? "" : r.getMethod().getDescription();
        String params = r.getParameters().length == 0 ? "" : Arrays.stream(r.getParameters())
                .map(p -> p == null || p.toString().isEmpty() ? "(empty)" : p.toString())
                .collect(Collectors.joining(" · "));

        StringBuilder details = new StringBuilder();
        if (r.getThrowable() != null) {
            StringWriter trace = new StringWriter();
            r.getThrowable().printStackTrace(new PrintWriter(trace));
            details.append("<div class=\"error\">").append(esc(shortMessage(r.getThrowable()))).append("</div>")
                   .append("<details><summary>Stack trace</summary><pre>").append(esc(trace.toString())).append("</pre></details>");
        }
        Object shot = r.getAttribute(SCREENSHOT_ATTRIBUTE);
        if (shot instanceof Path path && Files.exists(path)) {
            try {
                String b64 = Base64.getEncoder().encodeToString(Files.readAllBytes(path));
                details.append("<details open><summary>Screenshot</summary><img alt=\"Failure screenshot\" src=\"data:image/png;base64,")
                       .append(b64).append("\"></details>");
            } catch (IOException ignored) {
                details.append("<div>Screenshot: ").append(esc(path.toString())).append("</div>");
            }
        }

        return "<tr class=\"" + status + "\">"
                + "<td class=\"id\">" + esc(id) + "</td>"
                + "<td><div class=\"desc\">" + esc(description.replaceFirst("^" + id + "\\s*", "")) + "</div>"
                + "<div class=\"method\">" + esc(r.getTestClass().getRealClass().getSimpleName() + "." + name) + "</div>"
                + (params.isEmpty() ? "" : "<div class=\"params\">Data: " + esc(params) + "</div>")
                + details + "</td>"
                + "<td><span class=\"badge " + status + "\">" + status.toUpperCase() + "</span></td>"
                + "<td class=\"num\">" + seconds(r.getEndMillis() - r.getStartMillis()) + "</td>"
                + "</tr>\n";
    }

    /** Assertion / wait message without Selenium's "Build info / Capabilities" footer. */
    private static String shortMessage(Throwable t) {
        String msg = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
        int footer = msg.indexOf("\nBuild info:");
        return (footer > 0 ? msg.substring(0, footer) : msg).trim();
    }

    private static String seconds(long millis) {
        return String.format("%.1f s", Duration.ofMillis(millis).toMillis() / 1000.0);
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final String TEMPLATE = """
            <!doctype html>
            <html lang="en">
            <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>{{suite}} – Test Report</title>
            <style>
              :root { --bg:#f6f7f9; --card:#fff; --text:#1d2330; --muted:#6b7280; --line:#e5e7eb;
                      --pass:#15803d; --pass-bg:#dcfce7; --fail:#b91c1c; --fail-bg:#fee2e2; --skip:#a16207; --skip-bg:#fef3c7; }
              @media (prefers-color-scheme: dark) {
                :root { --bg:#111418; --card:#1a1f26; --text:#e6e8eb; --muted:#9aa3ae; --line:#2b323c;
                        --pass:#4ade80; --pass-bg:#12321f; --fail:#f87171; --fail-bg:#3b1616; --skip:#fbbf24; --skip-bg:#3a2e10; }
              }
              * { box-sizing:border-box; }
              body { margin:0; background:var(--bg); color:var(--text); font:14px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,sans-serif; }
              main { max-width:1100px; margin:0 auto; padding:24px 16px 48px; }
              header { display:flex; flex-wrap:wrap; justify-content:space-between; align-items:flex-end; gap:12px; margin-bottom:20px; }
              h1 { margin:0; font-size:22px; }
              .meta { color:var(--muted); font-size:13px; }
              .overall { font-weight:700; padding:6px 12px; border-radius:6px; }
              .overall.pass { color:var(--pass); background:var(--pass-bg); }
              .overall.fail { color:var(--fail); background:var(--fail-bg); }
              .tiles { display:grid; grid-template-columns:repeat(auto-fit,minmax(140px,1fr)); gap:12px; margin-bottom:20px; }
              .tile { background:var(--card); border:1px solid var(--line); border-radius:8px; padding:12px 14px; }
              .tile .label { color:var(--muted); font-size:12px; text-transform:uppercase; letter-spacing:.04em; }
              .tile .value { font-size:24px; font-weight:700; font-variant-numeric:tabular-nums; }
              .tile.pass .value { color:var(--pass); } .tile.fail .value { color:var(--fail); } .tile.skip .value { color:var(--skip); }
              .filters { display:flex; gap:8px; margin-bottom:10px; flex-wrap:wrap; }
              .filters button { border:1px solid var(--line); background:var(--card); color:var(--text); border-radius:6px; padding:5px 12px; cursor:pointer; font:inherit; }
              .filters button.active { border-color:var(--text); font-weight:600; }
              .table-wrap { overflow-x:auto; background:var(--card); border:1px solid var(--line); border-radius:8px; }
              table { width:100%; border-collapse:collapse; }
              th, td { text-align:left; padding:10px 12px; border-bottom:1px solid var(--line); vertical-align:top; }
              th { font-size:12px; color:var(--muted); text-transform:uppercase; letter-spacing:.04em; }
              tr:last-child td { border-bottom:none; }
              td.id { font-weight:700; white-space:nowrap; }
              td.num { white-space:nowrap; font-variant-numeric:tabular-nums; color:var(--muted); }
              .desc { font-weight:600; }
              .method, .params { color:var(--muted); font-size:12px; font-family:ui-monospace,Menlo,monospace; word-break:break-word; }
              .badge { font-size:11px; font-weight:700; padding:2px 8px; border-radius:999px; }
              .badge.pass { color:var(--pass); background:var(--pass-bg); }
              .badge.fail { color:var(--fail); background:var(--fail-bg); }
              .badge.skip { color:var(--skip); background:var(--skip-bg); }
              .error { margin-top:8px; color:var(--fail); white-space:pre-wrap; word-break:break-word; }
              details { margin-top:6px; } summary { cursor:pointer; color:var(--muted); }
              pre { font-size:12px; overflow-x:auto; background:var(--bg); padding:8px; border-radius:6px; }
              img { max-width:100%; margin-top:6px; border:1px solid var(--line); border-radius:6px; }
            </style>
            </head>
            <body>
            <main>
              <header>
                <div>
                  <h1>{{suite}}</h1>
                  <div class="meta">{{date}} · {{browser}} · {{baseUrl}}</div>
                </div>
                <div class="overall {{overall}}">{{overallText}}</div>
              </header>
              <section class="tiles">
                <div class="tile"><div class="label">Total</div><div class="value">{{total}}</div></div>
                <div class="tile pass"><div class="label">Passed</div><div class="value">{{passed}}</div></div>
                <div class="tile fail"><div class="label">Failed</div><div class="value">{{failed}}</div></div>
                <div class="tile skip"><div class="label">Skipped</div><div class="value">{{skipped}}</div></div>
                <div class="tile"><div class="label">Duration</div><div class="value">{{duration}}</div></div>
              </section>
              <div class="filters" role="group" aria-label="Filter by status">
                <button class="active" data-f="all">All</button>
                <button data-f="pass">Passed</button>
                <button data-f="fail">Failed</button>
                <button data-f="skip">Skipped</button>
              </div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>ID</th><th>Test</th><th>Status</th><th>Time</th></tr></thead>
                  <tbody>
            {{rows}}
                  </tbody>
                </table>
              </div>
            </main>
            <script>
              document.querySelectorAll('.filters button').forEach(b => b.addEventListener('click', () => {
                document.querySelectorAll('.filters button').forEach(x => x.classList.toggle('active', x === b));
                document.querySelectorAll('tbody tr').forEach(tr =>
                  tr.style.display = b.dataset.f === 'all' || tr.classList.contains(b.dataset.f) ? '' : 'none');
              }));
            </script>
            </body>
            </html>
            """;
}
