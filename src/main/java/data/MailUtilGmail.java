package data;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {
    final static String EMAIL_ADDRESS = System.getenv("MAIL_USER") != null
            ? System.getenv("MAIL_USER")
            : "phaty9147@gmail.com";
    final static String EMAIL_PASSWORD = System.getenv("MAIL_PASSWORD") != null
            ? System.getenv("MAIL_PASSWORD")
            : "lmuvsqzzpwmxjvgh";

    final static String SCRIPT_URL = System.getenv("MAIL_SCRIPT_URL") != null
            ? System.getenv("MAIL_SCRIPT_URL")
            : "https://script.google.com/macros/s/AKfycbw-cT1SJtgfIrw2mSJYoMcJUhtgkdP_aOdC5-LK9ViS2vyxy2W5kVFCF4qXAoiLRm39/exec";

    public static void sendMail(String to, String from,
            String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        if (SCRIPT_URL != null && !SCRIPT_URL.trim().isEmpty()) {
            try {
                System.out.println("MailUtilGmail: Sending email via Google Apps Script HTTPS webhook...");
                sendViaGoogleScript(SCRIPT_URL, to, subject, body, bodyIsHTML);
                System.out.println("MailUtilGmail: Email sent successfully via Google Apps Script!");
                return;
            } catch (Exception e) {
                System.err.println("MailUtilGmail: Failed via Google Apps Script webhook (" + e.getMessage()
                        + "), falling back to SMTP...");
            }
        }

        // Ưu tiên 2: Fallback qua SMTP cổng 587 (khi chạy local trên máy tính)
        sendViaSmtp(to, from, subject, body, bodyIsHTML);
    }

    private static void sendViaGoogleScript(String scriptUrl, String to, String subject, String body, boolean isHtml)
            throws Exception {
        URL url = new URL(scriptUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        String jsonPayload = "{"
                + "\"to\":\"" + escapeJson(to) + "\","
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"body\":\"" + escapeJson(body) + "\","
                + "\"isHtml\":" + isHtml
                + "}";

        System.out.println("MailUtilGmail: JSON payload: " + jsonPayload);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("MailUtilGmail: Initial response code: " + responseCode);

        // Google Apps Script trả về 302 redirect → cần follow redirect để script thực thi
        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 303) {
            String redirectUrl = conn.getHeaderField("Location");
            System.out.println("MailUtilGmail: Following redirect to: " + redirectUrl);
            if (redirectUrl != null) {
                HttpURLConnection conn2 = (HttpURLConnection) new URL(redirectUrl).openConnection();
                conn2.setRequestMethod("GET");
                conn2.setConnectTimeout(15000);
                conn2.setReadTimeout(15000);
                int redirectCode = conn2.getResponseCode();
                System.out.println("MailUtilGmail: Redirect response code: " + redirectCode);
                if (redirectCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Google Apps Script redirect HTTP status: " + redirectCode);
                }
            }
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Google Apps Script HTTP status: " + responseCode);
        }
    }

    private static String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void sendViaSmtp(String to, String from,
            String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.user", EMAIL_ADDRESS);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });
        session.setDebug(true);

        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        Address fromAddress = new InternetAddress(from);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        Transport transport = session.getTransport("smtp");
        try {
            transport.connect("smtp.gmail.com", 587, EMAIL_ADDRESS, EMAIL_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
