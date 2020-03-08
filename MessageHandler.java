/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

/**
 *
 * @author Russell Brown
 */
public class MessageHandler {
    
    
     public static void SendMessage(String subject, String message)
    {
        try
        {
            
            System.out.println(subject + "  " + message);
            SendMail.send_message(subject, message);
            
        }
        
        catch (Exception ex2)
        {
            ex2.printStackTrace();
        }
        
        
    }
    
}
