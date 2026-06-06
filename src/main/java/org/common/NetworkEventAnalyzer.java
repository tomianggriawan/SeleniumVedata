package org.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * NetworkEventAnalyzer — Utilitas analisis mendalam berbasis Chrome DevTools Protocol (CDP)
 * melalui Performance Logs Selenium 4.
 *
 * <p>Cara kerja:
 * <ol>
 *   <li>Parsing {@code Network.requestWillBeSent} → simpan payload POST/PUT/PATCH per requestId.</li>
 *   <li>Parsing {@code Network.responseReceived} → deteksi status HTTP &gt;= 400.</li>
 *   <li>Cross-reference requestId → gabungkan URL + metode + payload + status ke satu error record.</li>
 *   <li>Parsing {@code LogType.BROWSER} → deteksi AxiosError, ERR_BAD_RESPONSE, SyntaxError, dll.</li>
 * </ol>
 *
 * <p>Digunakan oleh semua kelas test di paket {@code org.test.setting} melalui method
 * {@link #drainLogs(WebDriver)} dan {@link #analyze(WebDriver)}.
 */
public class NetworkEventAnalyzer {

    // ==================== Public Data Model ====================

    /**
     * Satu record error jaringan yang terdeteksi.
     */
    public static class NetworkError {
        public final String requestId;
        public final String url;
        public final String method;
        public final int    statusCode;
        public final String payload;        // POST/PUT body, kosong jika GET
        public final String consoleContext; // Pesan error dari browser console terkait
        public final ErrorType type;

        public enum ErrorType { BACKEND, FRONTEND }

        public NetworkError(String requestId, String url, String method,
                            int statusCode, String payload,
                            String consoleContext, ErrorType type) {
            this.requestId      = requestId;
            this.url            = url;
            this.method         = method;
            this.statusCode     = statusCode;
            this.payload        = payload;
            this.consoleContext = consoleContext;
            this.type           = type;
        }

        /** Membangun string HTML terstruktur untuk ditampilkan dalam test report. */
        public String toHtmlDetail() {
            String typeLabel  = type == ErrorType.BACKEND ? "BACKEND ERROR" : "FRONTEND ERROR";
            String labelColor = type == ErrorType.BACKEND ? "#ef4444" : "#f59e0b";
            StringBuilder sb = new StringBuilder();
            sb.append("<div style=\"border-left:4px solid ").append(labelColor)
              .append(";padding:12px 16px;margin:8px 0;background:rgba(0,0,0,0.2);border-radius:4px;font-family:monospace;font-size:12px;\">");
            sb.append("<div style=\"color:").append(labelColor)
              .append(";font-weight:700;margin-bottom:6px;\">&#9888; [").append(typeLabel).append("]</div>");
            sb.append("<div><b>URL :</b> ").append(escapeHtml(url)).append("</div>");
            if (method != null && !method.isEmpty())
                sb.append("<div><b>Method :</b> ").append(escapeHtml(method)).append("</div>");
            if (statusCode > 0)
                sb.append("<div><b>Status :</b> <span style=\"color:").append(labelColor).append(";\">")
                  .append(statusCode).append("</span></div>");
            if (payload != null && !payload.isEmpty())
                sb.append("<div><b>Payload :</b> <pre style=\"white-space:pre-wrap;word-break:break-all;margin:4px 0;\">")
                  .append(escapeHtml(payload)).append("</pre></div>");
            if (consoleContext != null && !consoleContext.isEmpty())
                sb.append("<div><b>Console :</b> <span style=\"color:#fbbf24;\">")
                  .append(escapeHtml(consoleContext)).append("</span></div>");
            sb.append("</div>");
            return sb.toString();
        }

        /** Membangun string teks ringkas untuk laporan konsol. */
        public String toConsoleDetail() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(type.name()).append("] HTTP ").append(statusCode)
              .append(" | ").append(method).append(" ").append(url);
            if (payload != null && !payload.isEmpty())
                sb.append("\n        Payload: ").append(payload, 0, Math.min(200, payload.length()));
            if (consoleContext != null && !consoleContext.isEmpty())
                sb.append("\n        Console: ").append(consoleContext);
            return sb.toString();
        }

        private static String escapeHtml(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    /**
     * Hasil penuh analisis satu siklus (setelah satu aksi Save/Submit).
     */
    public static class AnalysisResult {
        private final List<NetworkError> errors = new ArrayList<>();

        public void addError(NetworkError e) { errors.add(e); }

        /** Mengembalikan true jika ada error apapun yang terdeteksi. */
        public boolean hasErrors() { return !errors.isEmpty(); }

        public List<NetworkError> getErrors() { return errors; }

        /**
         * Membangun satu blok HTML lengkap untuk ditambahkan ke dalam laporan test.
         * Memisahkan BACKEND dan FRONTEND errors secara visual.
         */
        public String buildHtmlReport() {
            if (!hasErrors()) return "";
            StringBuilder sb = new StringBuilder();
            sb.append("<div style=\"margin-top:16px;\">");
            sb.append("<div style=\"font-weight:700;font-size:13px;color:#f3f4f6;margin-bottom:8px;\">"
                    + "&#128269; Browser Network & Console Analysis</div>");
            for (NetworkError e : errors) sb.append(e.toHtmlDetail());
            sb.append("</div>");
            return sb.toString();
        }

        /**
         * Membangun teks deskripsi ringkas untuk {@code reporter.logFail(...)}.
         */
        public String buildSummary() {
            StringBuilder sb = new StringBuilder();
            long backends  = errors.stream().filter(e -> e.type == NetworkError.ErrorType.BACKEND).count();
            long frontends = errors.stream().filter(e -> e.type == NetworkError.ErrorType.FRONTEND).count();
            sb.append("Ditemukan ").append(errors.size()).append(" error");
            if (backends > 0)  sb.append(" | BACKEND: ").append(backends);
            if (frontends > 0) sb.append(" | FRONTEND: ").append(frontends);
            sb.append("\\n");
            for (NetworkError e : errors) sb.append("  ").append(e.toConsoleDetail()).append("\\n");
            return sb.toString().trim();
        }
    }

    // ==================== Core Analysis Methods ====================

    /**
     * Mengosongkan (drain) kedua buffer log agar bersih sebelum aksi utama.
     * Dipanggil SEBELUM mengklik tombol Save/Submit.
     *
     * @param driver WebDriver instance
     */
    public static void drainLogs(WebDriver driver) {
        try { driver.manage().logs().get(LogType.BROWSER);     } catch (Exception ignored) {}
        try { driver.manage().logs().get(LogType.PERFORMANCE); } catch (Exception ignored) {}
    }

    /**
     * Melakukan analisis penuh setelah aksi Save/Submit.
     * <ol>
     *   <li>Baca Performance Log → parsing semua CDP events dalam satu pass.</li>
     *   <li>Baca Browser Console Log → cari AxiosError, ERR_BAD_RESPONSE, SEVERE entries.</li>
     *   <li>Gabungkan dan kembalikan {@link AnalysisResult}.</li>
     * </ol>
     *
     * @param driver WebDriver instance
     * @return AnalysisResult berisi semua error yang terdeteksi
     */
    public static AnalysisResult analyze(WebDriver driver) {
        // 1. Baca raw logs
        List<String> perfJsonLines  = readPerformanceLogs(driver);
        List<String> consoleSevere  = readConsoleLogs(driver);

        // 2. Parse CDP Network events dari Performance Log
        Map<String, String> requestPayloads = new LinkedHashMap<>(); // requestId → postData
        Map<String, String> requestUrls     = new LinkedHashMap<>(); // requestId → url
        Map<String, String> requestMethods  = new LinkedHashMap<>(); // requestId → method
        List<NetworkError>  networkErrors   = new ArrayList<>();

        for (String json : perfJsonLines) {
            parseRequestWillBeSent(json, requestUrls, requestMethods, requestPayloads);
        }
        for (String json : perfJsonLines) {
            parseResponseReceived(json, requestUrls, requestMethods, requestPayloads,
                                  consoleSevere, networkErrors);
        }

        // 3. Parse Console Log untuk FRONTEND errors mandiri (bukan network)

        // URL dan pesan yang diketahui sebagai false-positive (server-side noise,
        // tidak mempengaruhi fungsionalitas aplikasi):
        //  - main.ts MIME error: server mengembalikan HTML saat browser request /src/main.ts
        //    (Vite dev mode artifact), namun app sudah ter-render via /static/js/index.*.js
        //  - Failed to load module script: MIME type mismatch untuk asset dev Vite
        final String[] KNOWN_NOISE_PATTERNS = {
            "/src/main.ts",
            "Failed to load module script",
            "Vite",
            "/src/main"
        };

        List<NetworkError> consoleOnlyErrors = new ArrayList<>();
        for (String msg : consoleSevere) {
            // Skip known noise yang bukan indikator kegagalan test aktual
            boolean isKnownNoise = false;
            for (String pattern : KNOWN_NOISE_PATTERNS) {
                if (msg.contains(pattern)) {
                    isKnownNoise = true;
                    System.out.println("  [NetworkAnalyzer] Skipping known noise: " + msg.substring(0, Math.min(80, msg.length())));
                    break;
                }
            }
            if (isKnownNoise) continue;

            boolean alreadyCoveredByNetwork = networkErrors.stream()
                .anyMatch(e -> e.consoleContext != null && e.consoleContext.contains(
                    msg.substring(0, Math.min(40, msg.length()))));
            if (!alreadyCoveredByNetwork) {
                boolean isBackend = (msg.contains("Failed to load resource") &&
                                    (msg.contains("status of 4") || msg.contains("status of 5")));
                NetworkError.ErrorType etype = isBackend
                    ? NetworkError.ErrorType.BACKEND
                    : NetworkError.ErrorType.FRONTEND;

                // Ekstrak kode status dari pesan console jika ada
                int statusFromConsole = extractStatusFromMessage(msg);
                String urlFromConsole = extractUrlFromMessage(msg);

                consoleOnlyErrors.add(new NetworkError(
                    "console", urlFromConsole, "?", statusFromConsole, "", msg, etype
                ));
            }
        }

        // 4. Gabungkan semua error
        AnalysisResult result = new AnalysisResult();
        networkErrors.forEach(result::addError);
        consoleOnlyErrors.forEach(result::addError);

        // 5. Cetak ke console untuk debug
        if (result.hasErrors()) {
            System.err.println("\n  ╔══════════════════════════════════════════════════════╗");
            System.err.println("  ║     BROWSER NETWORK/CONSOLE ERROR ANALYSIS          ║");
            System.err.println("  ╠══════════════════════════════════════════════════════╣");
            for (NetworkError e : result.getErrors()) {
                System.err.println("  ║ " + e.toConsoleDetail());
            }
            System.err.println("  ╚══════════════════════════════════════════════════════╝\n");
        }
        return result;
    }

    // ==================== Private Parsing Helpers ====================

    private static List<String> readPerformanceLogs(WebDriver driver) {
        List<String> lines = new ArrayList<>();
        try {
            LogEntries perfLogs = driver.manage().logs().get(LogType.PERFORMANCE);
            for (LogEntry entry : perfLogs) {
                lines.add(entry.getMessage());
            }
        } catch (Exception ignored) {}
        return lines;
    }

    private static List<String> readConsoleLogs(WebDriver driver) {
        List<String> severe = new ArrayList<>();
        try {
            LogEntries browserLogs = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : browserLogs) {
                String msg = entry.getMessage();
                Level  lvl = entry.getLevel();
                // Tangkap SEVERE dan pesan dengan kata kunci error terlepas dari level
                boolean isSevere  = (lvl == Level.SEVERE);
                boolean hasErrKey = msg.contains("AxiosError") || msg.contains("ERR_BAD_RESPONSE")
                    || msg.contains("Error submitting") || msg.contains("SyntaxError")
                    || msg.contains("TypeError") || msg.contains("ReferenceError")
                    || msg.contains("Uncaught") || msg.contains("Unhandled")
                    || msg.contains("Failed to load resource");
                if (isSevere || hasErrKey) {
                    severe.add(msg.trim());
                }
            }
        } catch (Exception ignored) {}
        return severe;
    }

    /**
     * Parsing event {@code Network.requestWillBeSent} dari JSON Performance Log.
     * Mengekstrak dan menyimpan: requestId, URL, HTTP method, dan POST body (jika ada).
     */
    private static void parseRequestWillBeSent(
            String json,
            Map<String, String> requestUrls,
            Map<String, String> requestMethods,
            Map<String, String> requestPayloads) {

        if (!json.contains("Network.requestWillBeSent")) return;

        String requestId = extractJsonString(json, "requestId");
        if (requestId == null || requestId.isEmpty()) return;

        String url    = extractJsonString(json, "url");
        String method = extractJsonString(json, "method");

        // postData hanya ada pada request POST/PUT/PATCH
        String postData = extractJsonString(json, "postData");

        if (url    != null) requestUrls.put(requestId, url);
        if (method != null) requestMethods.put(requestId, method);
        if (postData != null && !postData.isEmpty()) requestPayloads.put(requestId, postData);
    }

    /**
     * Parsing event {@code Network.responseReceived} dari JSON Performance Log.
     * Jika status &gt;= 400, buat {@link NetworkError} dan tambahkan ke daftar.
     */
    private static void parseResponseReceived(
            String json,
            Map<String, String> requestUrls,
            Map<String, String> requestMethods,
            Map<String, String> requestPayloads,
            List<String> consoleSevere,
            List<NetworkError> errors) {

        if (!json.contains("Network.responseReceived")) return;

        // Parse status
        int statusCode = extractJsonInt(json, "status");
        if (statusCode < 400) return; // Hanya proses error 4xx/5xx

        String requestId = extractJsonString(json, "requestId");
        String url       = requestUrls.getOrDefault(requestId, extractJsonString(json, "url"));
        String method    = requestMethods.getOrDefault(requestId, "?");
        String payload   = requestPayloads.getOrDefault(requestId, "");

        // Filter URL — skip asset statis (JS, CSS, gambar) kecuali yang relevan API
        if (url != null && isStaticAsset(url)) return;

        // Cari konteks konsol yang relevan dengan URL ini
        String consoleCtx = findConsoleContext(url, consoleSevere);

        NetworkError error = new NetworkError(
            requestId, url != null ? url : "(unknown)",
            method, statusCode, payload, consoleCtx,
            NetworkError.ErrorType.BACKEND
        );
        errors.add(error);
    }

    /** Cari pesan console yang mengandung bagian dari URL yang error. */
    private static String findConsoleContext(String url, List<String> consoleLogs) {
        if (url == null) return "";
        // Ambil path dari URL untuk pencocokan lebih fleksibel
        String urlPath = url.contains("/api/") ? url.substring(url.indexOf("/api/")) : url;
        StringBuilder ctx = new StringBuilder();
        for (String msg : consoleLogs) {
            if (msg.contains(urlPath) || msg.contains("AxiosError")
                    || msg.contains("ERR_BAD_RESPONSE") || msg.contains("Error submitting")) {
                if (ctx.length() > 0) ctx.append(" | ");
                ctx.append(msg.length() > 250 ? msg.substring(0, 250) + "..." : msg);
            }
        }
        return ctx.toString();
    }

    /** Apakah URL merupakan file statis (tidak perlu dilaporkan sebagai error API)? */
    private static boolean isStaticAsset(String url) {
        String lower = url.toLowerCase();
        return lower.contains("/static/") || lower.endsWith(".js") || lower.endsWith(".css")
            || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".woff2")
            || lower.endsWith(".svg") || lower.contains("fonts.googleapis")
            || lower.contains("favicon");
    }

    // ==================== JSON String Helpers (tanpa library eksternal) ====================

    /**
     * Ekstrak nilai string dari JSON berformat {@code "key":"value"}.
     * Mendukung escaped quotes di dalam value.
     */
    static String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        // Temukan akhir string dengan mempertimbangkan escaped quotes
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '\\') { end += 2; continue; } // skip escaped char
            if (c == '"')  break;
            end++;
        }
        return json.substring(start, end);
    }

    /**
     * Ekstrak nilai integer dari JSON berformat {@code "key":123}.
     */
    static int extractJsonInt(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return -1;
        int start = idx + search.length();
        // Lewati spasi
        while (start < json.length() && json.charAt(start) == ' ') start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        if (end == start) return -1;
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Coba ekstrak kode status HTTP dari pesan console seperti "status of 500". */
    private static int extractStatusFromMessage(String msg) {
        int idx = msg.indexOf("status of ");
        if (idx == -1) return 0;
        int start = idx + 10;
        int end   = start;
        while (end < msg.length() && Character.isDigit(msg.charAt(end))) end++;
        try { return Integer.parseInt(msg.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Coba ekstrak URL dari pesan console — ambil bagian yang diawali http/https. */
    private static String extractUrlFromMessage(String msg) {
        int idx = msg.indexOf("http");
        if (idx == -1) return "(console-only)";
        int end = msg.indexOf(" ", idx);
        return end == -1 ? msg.substring(idx) : msg.substring(idx, end);
    }
}
