/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.qrgenerate;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class QRCodeEmailSender {

    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static void sendEmailWithAttachment(String host, String port, final String userName, final String password, 
                                               String toAddress, String subject, String message, String attachFiles) {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(userName, password);
            }
        });

        try {
            // creates a new e-mail message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(userName));
            InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new java.util.Date());

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(message, "text/html");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // adds attachments
            if (attachFiles != null) {
                MimeBodyPart attachPart = new MimeBodyPart();

                try {
                    DataSource source = new FileDataSource(attachFiles);
                    attachPart.setDataHandler(new DataHandler(source));
                    attachPart.setFileName(source.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                multipart.addBodyPart(attachPart);
            }

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            // sends the e-mail
            Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Generate QR Code
            String qrCodeFilePath = "./QRCode.png";
            generateQRCodeImage("Hello World!", 350, 350, qrCodeFilePath);
            System.out.println("QR Code generated successfully.");

            // Email details
            String host = "smtp.gmail.com";
            String port = "587";
            String mailFrom = "Rageclothing0@gmail.com";
            String password = "zqnzgzstsjjkxlgz";

            // Outgoing email details
            String mailTo = "jonaldkielaustria135@gmail.com";
            String subject = "QR Code";
            String message = "Here is your QR code.";

            // Send email
            sendEmailWithAttachment(host, port, mailFrom, password, mailTo, subject, message, qrCodeFilePath);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

