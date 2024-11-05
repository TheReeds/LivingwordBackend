package living.word.livingword.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Verify your account");

            String htmlMsg = "<html>" +
                    "<head>" +
                    "<style>" +
                    "    body {" +
                    "        font-family: 'Helvetica Neue', Arial, sans-serif;" +
                    "        background-color: #f7f7f7;" +
                    "        margin: 0;" +
                    "        padding: 0;" +
                    "        line-height: 1.6;" +
                    "        color: #333;" +
                    "    }" +
                    "    .header {" +
                    "        background-color: #4a90e2;" +
                    "        color: white;" +
                    "        padding: 20px;" +
                    "        text-align: center;" +
                    "        border-radius: 8px 8px 0 0;" +
                    "        font-size: 24px;" +
                    "        font-weight: bold;" +
                    "    }" +
                    "    .container {" +
                    "        max-width: 600px;" +
                    "        margin: 20px auto;" +
                    "        background-color: white;" +
                    "        padding: 30px;" +
                    "        border-radius: 8px;" +
                    "        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);" +
                    "    }" +
                    "    h1 {" +
                    "        color: #4a90e2;" +
                    "        font-size: 22px;" +
                    "        margin-bottom: 20px;" +
                    "    }" +
                    "    p {" +
                    "        margin-bottom: 20px;" +
                    "        font-size: 16px;" +
                    "        color: #555;" +
                    "    }" +
                    "    .button {" +
                    "        display: inline-block;" +
                    "        background-color: #28a745;" +
                    "        color: white;" +
                    "        padding: 12px 24px;" +
                    "        text-decoration: none;" +
                    "        border-radius: 5px;" +
                    "        font-size: 16px;" +
                    "        font-weight: bold;" +
                    "        transition: background-color 0.3s ease;" +
                    "    }" +
                    "    a.button {" +
                    "        color: #f5f6f7;" +
                    "        text-decoration: none;" +
                    "    }" +
                    "    .button:hover {" +
                    "        background-color: #218838;" +
                    "    }" +
                    "    .footer {" +
                    "        margin-top: 30px;" +
                    "        font-size: 12px;" +
                    "        color: #777;" +
                    "        text-align: center;" +
                    "    }" +
                    "    .footer a {" +
                    "        color: #4a90e2;" +
                    "        text-decoration: none;" +
                    "    }" +
                    "    .footer a:hover {" +
                    "        text-decoration: underline;" +
                    "    }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='header'>" +
                    "    Welcome to Living Word!" +
                    "</div>" +
                    "<div class='container'>" +
                    "    <h1>Verify Your Account</h1>" +
                    "    <p>Thank you for registering with us! To complete your registration, please verify your account by clicking the button below:</p>" +
                    "    <a class='button' href='http://localhost:6500/auth/verify?token=" + token + "'>Verify Account</a>" +
                    "    <p>If you did not create this account, you can safely ignore this email.</p>" +
                    "    <div class='footer'>" +
                    "        <p>Need help? Contact us at <a href='mailto:livingword@living.com'>livingword@living.com</a></p>" +
                    "        <p>&copy; 2024 Living Word Church. All rights reserved.</p>" +
                    "    </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlMsg, "text/html");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void sendResetPasswordEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Reset your password");

            String htmlMsg = "<html>" +
                    "<head>" +
                    "<style>" +
                    "    body {" +
                    "        font-family: 'Helvetica Neue', Arial, sans-serif;" +
                    "        background-color: #f7f7f7;" +
                    "        margin: 0;" +
                    "        padding: 0;" +
                    "        line-height: 1.6;" +
                    "        color: #333;" +
                    "    }" +
                    "    .header {" +
                    "        background-color: #176fe3;" +
                    "        color: white;" +
                    "        padding: 20px;" +
                    "        text-align: center;" +
                    "        border-radius: 8px 8px 0 0;" +
                    "        font-size: 24px;" +
                    "        font-weight: bold;" +
                    "    }" +
                    "    .container {" +
                    "        max-width: 600px;" +
                    "        margin: 20px auto;" +
                    "        background-color: white;" +
                    "        padding: 30px;" +
                    "        border-radius: 8px;" +
                    "        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);" +
                    "    }" +
                    "    h1 {" +
                    "        color: #ff6b6b;" +
                    "        font-size: 22px;" +
                    "        margin-bottom: 20px;" +
                    "    }" +
                    "    p {" +
                    "        margin-bottom: 20px;" +
                    "        font-size: 16px;" +
                    "        color: #555;" +
                    "    }" +
                    "    .button {" +
                    "        display: inline-block;" +
                    "        background-color: #00d451;" +
                    "        color: white;" +
                    "        padding: 12px 24px;" +
                    "        text-decoration: none;" +
                    "        border-radius: 5px;" +
                    "        font-size: 16px;" +
                    "        font-weight: bold;" +
                    "        transition: background-color 0.3s ease;" +
                    "    }" +
                    "    a.button {" +
                    "        color: #f5f6f7;" +
                    "        text-decoration: none;" +
                    "    }" +
                    "    .button:hover {" +
                    "        background-color: #60656b;" +
                    "    }" +
                    "    .footer {" +
                    "        margin-top: 30px;" +
                    "        font-size: 12px;" +
                    "        color: #777;" +
                    "        text-align: center;" +
                    "    }" +
                    "    .footer a {" +
                    "        color: #176fe3;" +
                    "        text-decoration: none;" +
                    "    }" +
                    "    .footer a:hover {" +
                    "        text-decoration: underline;" +
                    "    }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='header'>" +
                    "    Password Reset Request" +
                    "</div>" +
                    "<div class='container'>" +
                    "    <h1>Reset Your Password</h1>" +
                    "    <p>We received a request to reset your password. You can reset it by clicking the button below:</p>" +
                    "    <a class='button' href='http://localhost:6500/auth/reset-password-form?token=" + token + "'>Reset Password</a>" +
                    "    <p>If you did not request this password reset, you can ignore this email.</p>" +
                    "    <div class='footer'>" +
                    "        <p>Need help? Contact us at <a href='mailto:livingword@living.com'>livingword@living.com</a></p>" +
                    "        <p>&copy; 2024 Living Word Church. All rights reserved.</p>" +
                    "    </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlMsg, "text/html");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
