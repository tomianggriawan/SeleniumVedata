package org.test.common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TestReportManager - Custom premium HTML & Console reporter.
 * Tracks test suites, scenarios, execution steps, and compile-to-HTML/PDF features.
 */
public class TestReportManager {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<TestCase> testCases = new ArrayList<>();
    private TestCase currentTest;
    private long suiteStartTime;

    public void startSuite() {
        this.suiteStartTime = System.currentTimeMillis();
        System.out.println("\n========================================================");
        System.out.println("🚀 STARTING AUTOMATION TEST SUITE");
        System.out.println("========================================================\n");
    }

    public void startTest(String testName, String description) {
        if (currentTest != null) {
            endTest();
        }
        currentTest = new TestCase(testName, description);
        System.out.println("🎬 [TEST SCENARIO] " + testName + " - " + description);
        System.out.println("--------------------------------------------------------");
    }

    public void logStep(String stepDescription) {
        if (currentTest != null) {
            currentTest.addStep("INFO", stepDescription);
            System.out.println("  [STEP] " + stepDescription);
        }
    }

    public void logPass(String message) {
        if (currentTest != null) {
            currentTest.setStatus("PASS");
            currentTest.addStep("PASS", message);
            System.out.println("  🟢 [PASS] " + message);
        }
    }

    public void logFail(String message, Throwable t) {
        if (currentTest != null) {
            currentTest.setStatus("FAIL");
            currentTest.addStep("FAIL", message);
            currentTest.setError(message, t);
            System.err.println("  🔴 [FAIL] " + message);
            if (t != null) {
                System.err.println("     Error: " + t.getMessage());
            }
        }
    }

    public void logError(String message, Throwable t) {
        if (currentTest != null) {
            currentTest.setStatus("ERROR");
            currentTest.addStep("ERROR", message);
            currentTest.setError(message, t);
            System.err.println("  ⚠️ [ERROR] " + message);
            if (t != null) {
                System.err.println("     Detail: " + t.getMessage());
            }
        }
    }

    public void endTest() {
        if (currentTest != null) {
            currentTest.end();
            testCases.add(currentTest);
            System.out.println("--------------------------------------------------------");
            System.out.println("📊 STATUS: " + getStatusEmoji(currentTest.getStatus()) + " " + currentTest.getStatus() + " | Duration: " + currentTest.getDuration() + "s\n");
            currentTest = null;
        }
    }

    private String getStatusEmoji(String status) {
        switch (status) {
            case "PASS": return "🟢";
            case "FAIL": return "🔴";
            case "ERROR": return "⚠️";
            default: return "⚪";
        }
    }

    public void generateHtmlReport(String filePath) {
        if (currentTest != null) {
            endTest();
        }
        long suiteEndTime = System.currentTimeMillis();
        double totalDuration = (suiteEndTime - suiteStartTime) / 1000.0;

        int passed = 0;
        int failed = 0;
        int errors = 0;
        for (TestCase tc : testCases) {
            if ("PASS".equals(tc.getStatus())) passed++;
            else if ("FAIL".equals(tc.getStatus())) failed++;
            else errors++;
        }
        int total = testCases.size();
        double successRate = total == 0 ? 0.0 : (passed * 100.0) / total;

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
            .append("<meta charset=\"UTF-8\">\n")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            .append("<title>VEDATA HCM Employee Test Report</title>\n")
            .append("<link href=\"https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap\" rel=\"stylesheet\">\n")
            .append("<style>\n")
            .append("  :root {\n")
            .append("    --bg-primary: #0b0f19;\n")
            .append("    --bg-secondary: #161c2a;\n")
            .append("    --bg-card: #1f293d;\n")
            .append("    --text-main: #f3f4f6;\n")
            .append("    --text-muted: #9ca3af;\n")
            .append("    --primary: #1868db;\n")
            .append("    --success: #10b981;\n")
            .append("    --fail: #ef4444;\n")
            .append("    --warning: #f59e0b;\n")
            .append("    --border: #2d3748;\n")
            .append("  }\n")
            .append("  body {\n")
            .append("    font-family: 'Plus Jakarta Sans', sans-serif;\n")
            .append("    background-color: var(--bg-primary);\n")
            .append("    color: var(--text-main);\n")
            .append("    margin: 0; padding: 20px;\n")
            .append("    line-height: 1.6;\n")
            .append("  }\n")
            .append("  .container {\n")
            .append("    max-width: 1200px;\n")
            .append("    margin: 0 auto;\n")
            .append("  }\n")
            .append("  header {\n")
            .append("    display: flex;\n")
            .append("    justify-content: space-between;\n")
            .append("    align-items: center;\n")
            .append("    padding-bottom: 20px;\n")
            .append("    border-bottom: 1px solid var(--border);\n")
            .append("    margin-bottom: 30px;\n")
            .append("  }\n")
            .append("  h1 { margin: 0; font-size: 28px; font-weight: 700; color: #fff; }\n")
            .append("  .subtitle { margin: 5px 0 0 0; color: var(--text-muted); font-size: 14px; }\n")
            .append("  .btn-print {\n")
            .append("    background-color: var(--primary);\n")
            .append("    color: #fff;\n")
            .append("    border: none;\n")
            .append("    padding: 10px 20px;\n")
            .append("    border-radius: 8px;\n")
            .append("    font-weight: 600;\n")
            .append("    cursor: pointer;\n")
            .append("    transition: all 0.2s;\n")
            .append("  }\n")
            .append("  .btn-print:hover { opacity: 0.9; transform: translateY(-1px); }\n")
            .append("  .summary-cards {\n")
            .append("    display: grid;\n")
            .append("    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));\n")
            .append("    gap: 20px;\n")
            .append("    margin-bottom: 40px;\n")
            .append("  }\n")
            .append("  .card {\n")
            .append("    background-color: var(--bg-secondary);\n")
            .append("    border: 1px solid var(--border);\n")
            .append("    border-radius: 12px;\n")
            .append("    padding: 20px;\n")
            .append("    text-align: center;\n")
            .append("  }\n")
            .append("  .card .value { font-size: 32px; font-weight: 700; margin-top: 5px; }\n")
            .append("  .card .label { font-size: 12px; font-weight: 600; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.5px; }\n")
            .append("  .card.success-rate .value { color: var(--success); }\n")
            .append("  .card.passed .value { color: var(--success); }\n")
            .append("  .card.failed .value { color: var(--fail); }\n")
            .append("  .card.errors .value { color: var(--warning); }\n")
            .append("  .test-list { display: flex; flex-direction: column; gap: 20px; }\n")
            .append("  .test-item {\n")
            .append("    background-color: var(--bg-secondary);\n")
            .append("    border: 1px solid var(--border);\n")
            .append("    border-radius: 12px;\n")
            .append("    overflow: hidden;\n")
            .append("    transition: border-color 0.2s;\n")
            .append("  }\n")
            .append("  .test-item:hover { border-color: var(--text-muted); }\n")
            .append("  .test-header {\n")
            .append("    display: flex;\n")
            .append("    justify-content: space-between;\n")
            .append("    align-items: center;\n")
            .append("    padding: 20px;\n")
            .append("    background-color: var(--bg-card);\n")
            .append("    cursor: pointer;\n")
            .append("    user-select: none;\n")
            .append("  }\n")
            .append("  .test-title-section { display: flex; flex-direction: column; }\n")
            .append("  .test-title {\n")
            .append("    font-size: 18px; font-weight: 600; color: #fff;\n")
            .append("    display: flex; align-items: center; gap: 10px;\n")
            .append("  }\n")
            .append("  .test-desc { font-size: 13px; color: var(--text-muted); margin-top: 4px; }\n")
            .append("  .badge {\n")
            .append("    padding: 4px 10px;\n")
            .append("    border-radius: 6px;\n")
            .append("    font-size: 11px;\n")
            .append("    font-weight: 700;\n")
            .append("    text-transform: uppercase;\n")
            .append("  }\n")
            .append("  .badge.pass { background-color: rgba(16, 185, 129, 0.15); color: var(--success); border: 1px solid var(--success); }\n")
            .append("  .badge.fail { background-color: rgba(239, 68, 68, 0.15); color: var(--fail); border: 1px solid var(--fail); }\n")
            .append("  .badge.error { background-color: rgba(245, 158, 11, 0.15); color: var(--warning); border: 1px solid var(--warning); }\n")
            .append("  .test-meta { display: flex; align-items: center; gap: 20px; font-size: 13px; color: var(--text-muted); }\n")
            .append("  .test-details {\n")
            .append("    padding: 20px;\n")
            .append("    border-top: 1px solid var(--border);\n")
            .append("  }\n")
            .append("  .step-table {\n")
            .append("    width: 100%;\n")
            .append("    border-collapse: collapse;\n")
            .append("    font-size: 14px;\n")
            .append("  }\n")
            .append("  .step-table th, .step-table td {\n")
            .append("    padding: 10px 15px;\n")
            .append("    text-align: left;\n")
            .append("    border-bottom: 1px solid var(--border);\n")
            .append("  }\n")
            .append("  .step-table th {\n")
            .append("    font-weight: 600;\n")
            .append("    color: var(--text-muted);\n")
            .append("    background-color: var(--bg-primary);\n")
            .append("  }\n")
            .append("  .step-status {\n")
            .append("    font-weight: 700;\n")
            .append("  }\n")
            .append("  .step-status.pass { color: var(--success); }\n")
            .append("  .step-status.fail { color: var(--fail); }\n")
            .append("  .step-status.error { color: var(--warning); }\n")
            .append("  .step-status.info { color: var(--primary); }\n")
            .append("  .error-block {\n")
            .append("    margin-top: 20px;\n")
            .append("    background-color: rgba(239, 68, 68, 0.08);\n")
            .append("    border-left: 4px solid var(--fail);\n")
            .append("    padding: 15px;\n")
            .append("    border-radius: 4px;\n")
            .append("    font-family: monospace;\n")
            .append("    font-size: 12px;\n")
            .append("    white-space: pre-wrap;\n")
            .append("    overflow-x: auto;\n")
            .append("  }\n")
            .append("  footer {\n")
            .append("    text-align: center;\n")
            .append("    margin-top: 50px;\n")
            .append("    padding-top: 20px;\n")
            .append("    border-top: 1px solid var(--border);\n")
            .append("    color: var(--text-muted);\n")
            .append("    font-size: 12px;\n")
            .append("  }\n")
            .append("  /* Accordion functionality using details tag */\n")
            .append("  details summary::-webkit-details-marker { display: none; }\n")
            .append("  details summary { list-style: none; outline: none; }\n")
            .append("  @media print {\n")
            .append("    body {\n")
            .append("      background-color: #fff;\n")
            .append("      color: #000;\n")
            .append("      padding: 0;\n")
            .append("    }\n")
            .append("    .btn-print { display: none; }\n")
            .append("    .card, .test-item {\n")
            .append("      background-color: #fff;\n")
            .append("      border: 1px solid #ddd;\n")
            .append("      color: #000;\n")
            .append("      page-break-inside: avoid;\n")
            .append("    }\n")
            .append("    h1, .test-title, .test-desc { color: #000 !important; }\n")
            .append("    .test-header { background-color: #f5f5f5 !important; }\n")
            .append("    .test-details { display: block !important; }\n")
            .append("    .step-table th { background-color: #eee !important; color: #000; }\n")
            .append("    .step-table td { border-bottom: 1px solid #ddd; }\n")
            .append("    .badge { border: 1px solid #000 !important; color: #000 !important; }\n")
            .append("  }\n")
            .append("</style>\n")
            .append("</head>\n<body>\n")
            .append("<div class=\"container\">\n")
            .append("  <header>\n")
            .append("    <div>\n")
            .append("      <h1>VEDATA HCM Test execution Report</h1>\n")
            .append("      <p class=\"subtitle\">Generated on: ").append(LocalDateTime.now().format(DATE_FORMAT)).append("</p>\n")
            .append("    </div>\n")
            .append("    <button class=\"btn-print\" onclick=\"window.print()\">📄 Print/Save to PDF</button>\n")
            .append("  </header>\n")
            .append("  <div class=\"summary-cards\">\n")
            .append("    <div class=\"card\">\n")
            .append("      <div class=\"label\">Total Scenarios</div>\n")
            .append("      <div class=\"value\">").append(total).append("</div>\n")
            .append("    </div>\n")
            .append("    <div class=\"card success-rate\">\n")
            .append("      <div class=\"label\">Success Rate</div>\n")
            .append("      <div class=\"value\">").append(String.format("%.1f", successRate)).append("%</div>\n")
            .append("    </div>\n")
            .append("    <div class=\"card passed\">\n")
            .append("      <div class=\"label\">Passed</div>\n")
            .append("      <div class=\"value\">").append(passed).append("</div>\n")
            .append("    </div>\n")
            .append("    <div class=\"card failed\">\n")
            .append("      <div class=\"label\">Failed</div>\n")
            .append("      <div class=\"value\">").append(failed).append("</div>\n")
            .append("    </div>\n")
            .append("    <div class=\"card errors\">\n")
            .append("      <div class=\"label\">Errors</div>\n")
            .append("      <div class=\"value\">").append(errors).append("</div>\n")
            .append("    </div>\n")
            .append("    <div class=\"card\">\n")
            .append("      <div class=\"label\">Duration</div>\n")
            .append("      <div class=\"value\">").append(String.format("%.2f", totalDuration)).append("s</div>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("  <h2>Test Scenarios Details</h2>\n")
            .append("  <div class=\"test-list\">\n");

        for (TestCase tc : testCases) {
            String badgeClass = tc.getStatus().toLowerCase();
            html.append("    <details open class=\"test-item\">\n")
                .append("      <summary class=\"test-header\">\n")
                .append("        <div class=\"test-title-section\">\n")
                .append("          <div class=\"test-title\">")
                .append(tc.getName())
                .append(" <span class=\"badge ").append(badgeClass).append("\">").append(tc.getStatus()).append("</span>")
                .append("          </div>\n")
                .append("          <div class=\"test-desc\">").append(tc.getDescription()).append("</div>\n")
                .append("        </div>\n")
                .append("        <div class=\"test-meta\">\n")
                .append("          <span>⏱️ ").append(tc.getDuration()).append("s</span>\n")
                .append("        </div>\n")
                .append("      </summary>\n")
                .append("      <div class=\"test-details\">\n")
                .append("        <table class=\"step-table\">\n")
                .append("          <thead>\n")
                .append("            <tr>\n")
                .append("              <th style=\"width: 100px;\">Time</th>\n")
                .append("              <th style=\"width: 80px;\">Status</th>\n")
                .append("              <th>Description</th>\n")
                .append("            </tr>\n")
                .append("          </thead>\n")
                .append("          <tbody>\n");

            for (TestStep step : tc.getSteps()) {
                String stepStatusClass = step.status.toLowerCase();
                html.append("            <tr>\n")
                    .append("              <td style=\"color: var(--text-muted);\">").append(step.timestamp.format(TIME_FORMAT)).append("</td>\n")
                    .append("              <td><span class=\"step-status ").append(stepStatusClass).append("\">").append(step.status).append("</span></td>\n")
                    .append("              <td>").append(step.description).append("</td>\n")
                    .append("            </tr>\n");
            }

            html.append("          </tbody>\n")
                .append("        </table>\n");

            if (tc.getErrorMessage() != null) {
                html.append("        <div class=\"error-block\">\n")
                    .append("<strong>[Error Message]</strong> ").append(tc.getErrorMessage()).append("\n\n")
                    .append(tc.getStackTrace())
                    .append("        </div>\n");
            }

            html.append("      </div>\n")
                .append("    </details>\n");
        }

        html.append("  </div>\n")
            .append("  <footer>\n")
            .append("    <p>VEDATA HCM Automation Testing Suite - &copy; ").append(LocalDateTime.now().getYear()).append("</p>\n")
            .append("  </footer>\n")
            .append("</div>\n</body>\n</html>");

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(html.toString());
            System.out.println("🎉 HTML Report compiled successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to write HTML Report to: " + filePath + " | Error: " + e.getMessage());
        }
    }

    // ==================== Inner classes to hold test data ====================

    private static class TestCase {
        private final String name;
        private final String description;
        private String status = "PASS"; // Default
        private final List<TestStep> steps = new ArrayList<>();
        private long startTime;
        private double duration;
        private String errorMessage;
        private String stackTrace;

        public TestCase(String name, String description) {
            this.name = name;
            this.description = description;
            this.startTime = System.currentTimeMillis();
        }

        public void setStatus(String status) {
            // Only escalate status, e.g. FAIL overrides PASS, ERROR overrides FAIL
            if ("ERROR".equals(this.status)) return;
            if ("FAIL".equals(this.status) && !"ERROR".equals(status)) return;
            this.status = status;
        }

        public void addStep(String status, String description) {
            steps.add(new TestStep(status, description));
        }

        public void setError(String message, Throwable t) {
            this.errorMessage = message;
            if (t != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                this.stackTrace = sw.toString();
            }
        }

        public void end() {
            this.duration = (System.currentTimeMillis() - startTime) / 1000.0;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public List<TestStep> getSteps() { return steps; }
        public double getDuration() { return duration; }
        public String getErrorMessage() { return errorMessage; }
        public String getStackTrace() { return stackTrace; }
    }

    private static class TestStep {
        private final LocalDateTime timestamp;
        private final String status;
        private final String description;

        public TestStep(String status, String description) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.description = description;
        }
    }
}
