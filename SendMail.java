/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Russell Brown
 */
public class SendMail {
    
    
    
    
    
    
    public static void send_message(String subject, String text)  throws Exception
    {
        //https://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
        
        
		try {
        
        final String username = "russ.brown@rogers.com";
        
		final String password = "zkbkuinftaeimzkd";

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.broadband.rogers.com");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
                

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });


                
                        
                         MimeMessage message = new MimeMessage(session);

                        
                         
                        message.setFrom(new InternetAddress("russ.brown@rogers.com"));

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress("4373530673@msg.telus.com"));
                      
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress("russ.brown@rogers.com"));
                      
                      

                        message.setSubject(subject);

                        message.setText(text);
                        
			Transport.send(message);
                        
                        

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
      
    }
    
    
    
}
