/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;



import java.net.*;
import java.io.*;
import java.util.*;

//Source:  https://syntaxcorrect.com/Java/5_Ultra_Lightweight_Http_Server_Implementations_in_Java_for_Blazing_Fast_Microservices_APIs_or_Even_Websites


public class HTTPServer
{
 static final int port=76;
 static final String newLine="\r\n";
 
 
 public static String ConvertByteArrayToString(byte[] data, int size)
 {
     String result = "";
     
     for (int i = 0; i < size; i++)
     {
         result += (char)data[i];
     }
     
     return result;
 }
 
 public static String GetFileContents()
 {
     String result = "";
     
     try
     {
        String path = "C:\\Users\\Administrator\\Documents\\NetBeansProjects\\TWS_Client_1b\\TWS_Client_1b\\src\\tws_client_1b\\index.html";
        RandomAccessFile f = new RandomAccessFile(path, "rw");
        
        while (true)
        {
            byte[] data = new byte[1000];
            int size = f.read(data);
            
            
            if (size == -1) 
            {
                break;
            }
            
            result += ConvertByteArrayToString(data, size);
            
        }
        
     }
     catch (Exception ex)
     {
         ex.printStackTrace();
     }
     
     return result;
 }
 
 public static boolean ready;
 
 public static boolean started;
 
 

 public static void start_web_server()
 {
  try
  {
      ready = false;
      

      String website_data = GetFileContents();
      
      
      
   ServerSocket socket=new ServerSocket(port);
       System.out.println("http server started");
      
       InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println("IP Address:- " + inetAddress.getHostAddress());
        System.out.println("Host Name:- " + inetAddress.getHostName());

   while (true)
   {
       
       ready = true;
       
    Socket connection=socket.accept();

    try
    {
     BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));
     OutputStream out=new BufferedOutputStream(connection.getOutputStream());
     PrintStream pout=new PrintStream(out);

     // read first line of request
     String request=in.readLine();
     if (request==null) continue;

     // we ignore the rest
     while (true)
     {
      String ignore=in.readLine();
      if (ignore==null || ignore.length()==0) break;
     }

     if (!request.startsWith("GET ") ||
         !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1")))
     {
      // bad request
      pout.print("HTTP/1.0 400 Bad Request"+newLine+newLine);
     }
     else
     {
         
         //https://stackoverflow.com/questions/23714383/what-are-all-the-possible-values-for-http-content-type-header
         
         
       //   String response = website;
       
          String response = "<html><head></head><body> "
               + " <script>  var wsocket = new WebSocket(\"ws://www.rbtrading.ca:74\"); "
               + "wsocket.onopen = function (event) { alert(\"opened\"); };"
               + " </script>  "
               + " websockets test  </body></html>";
          
          response = website_data;
          
       
      pout.print(
       "HTTP/1.0 200 OK"+newLine+
       "Content-Type: text/html"+newLine+
       "Date: "+new Date()+newLine+
       "Content-length: "+response.length()+newLine+newLine+
       response
      );
         
         
      
      
     }

     pout.close();
    }
    catch (Throwable tri)
    {
     System.err.println("Error handling request: "+tri);
    }
   }
  }
  catch (Throwable tr)
  {
   System.err.println("Could not start server: "+tr);
  }
 }
}