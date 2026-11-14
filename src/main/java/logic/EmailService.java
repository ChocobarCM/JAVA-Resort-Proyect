package logic; 

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String HOST = "smtp.gmail.com"; 
    private static final String PORT = "587";
    private static final String USER = "eldedesgracias@gmail.com"; 
    private static final String PASS = "smdtpfzmvuviivjd";

    public static void enviarConfirmacionReserva(String emailDestino, String nombreCliente, String fechaEntrada, String fechaSalida) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.trust", HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, PASS);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject("Confirmación de Reserva - Tu Hotel");

            String htmlContent = "<h2>¡Hola " + nombreCliente + "!</h2>"
                    + "<p>Tu reserva ha sido confirmada con éxito.</p>"
                    + "<p><strong>Fecha de Entrada:</strong> " + fechaEntrada + "</p>"
                    + "<p><strong>Fecha de Salida:</strong> " + fechaSalida + "</p>"
                    + "<p>¡Gracias por elegirnos!</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Correo enviado exitosamente a: " + emailDestino);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}