/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Russell Brown
 */
public class ExceptionHandler {
    
     public static void HandleCaution(String message)
    {
        try
        {
            
            System.out.println("caution message: " + message);
            SendMail.send_message("caution message: ", message);
            
        }
        
        catch (Exception ex2)
        {
            ex2.printStackTrace();
        }
        
        
    }
    
    public static void HandleException(Exception e)
    {
        try
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            
            System.out.println("exception thrown: " + exceptionAsString);
            SendMail.send_message("exception thrown: ", exceptionAsString);
            

            System.out.println("system exiting");
            SendMail.send_message("system exiting", "system exiting");
            System.exit(0);
        }
        
        catch (Exception ex2)
        {
            ex2.printStackTrace();
        }
        
        
    }
    
}
