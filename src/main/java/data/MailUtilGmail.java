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
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", 465);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);

        // 2 - create a message
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html");
        } else {
            message.setText(body);
        }

        // 3 - address the message
        Address fromAddress = new InternetAddress(from);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // 4 - send the message
        Transport transport = session.getTransport();
        transport.connect(EMAIL_ADDRESS, EMAIL_PASSWORD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
