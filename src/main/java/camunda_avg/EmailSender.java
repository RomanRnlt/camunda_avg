package camunda_avg;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

  private static final String SMTP_HOST = "localhost"; // MailHog SMTP-Server-Host
  private static final String SMTP_PORT = "1025"; // MailHog SMTP-Server-Port

  public static void sendEmail(String to, String subject, String body) {
    Properties props = new Properties();
    props.put("mail.smtp.host", SMTP_HOST);
    props.put("mail.smtp.port", SMTP_PORT);

    Session session = Session.getInstance(props);

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress("max.musterpersonaler@avg.com")); // Absender-E-Mail-Adresse (kann statisch
                                                                            // sein)
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject);
      message.setText(body);

      Transport.send(message);

      System.out.println("Email gesendet an: " + to + "; Betrff: " + subject);

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
