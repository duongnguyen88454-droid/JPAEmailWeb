package data;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailUtilGmail {
    final static String EMAIL_ADDRESS = System.getenv("MAIL_USER") != null
            ? System.getenv("MAIL_USER") : "phaty9147@gmail.com";
    final static String EMAIL_PASSWORD = System.getenv("MAIL_PASSWORD") != null
            ? System.getenv("MAIL_PASSWORD") : "lmuvsqzzpwmxjvgh";

    public static void sendMail(String to, String from,
            String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        // 1 - get a mail session
        // Port 587 + STARTTLS
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.user", EMAIL_ADDRESS);
        // Timeout 5 giây để tránh treo web khi chạy trên cloud bị firewall chặn port SMTP
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });
        session.setDebug(true);

        // 2 - create a message
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body);
        }

        // 3 - address the message
        Address fromAddress = new InternetAddress(from);
        Address toAddress   = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // 4 - send the message (STARTTLS - port 587)
        Transport transport = session.getTransport("smtp");
        try {
            transport.connect("smtp.gmail.com", 587, EMAIL_ADDRESS, EMAIL_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
