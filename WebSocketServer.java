/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tws_client_1b;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Russell Brown
 */
public class WebSocketServer {
    
    
    // Source:  https://stackoverflow.com/questions/43163592/standalone-websocket-server-without-jee-application-server
    
    
    public static ServerSocket server = null;
    public static ArrayList clients;
    
    public static void maintain_clients()
    {
        if (clients != null && clients.size() > 0)
        {
        
            int id = 0;

            while (true)
            {
                Object[] arr = (Object[])clients.get(id);
                Socket c = (Socket)arr[0];
                if (c.isConnected() == false || c.isClosed() == true)
                {
                    System.out.println("removed " + id);
                    clients.remove(id);
                    if (clients.size() == 0) break;
                }
                else
                {
                    id++;
                    if (id >= clients.size()) break;
                }
            }
        
        }
        
        
    }
    
    
    
    public static void start_server()
    {
        try
        {        
            
            
            System.out.println("starting websocket server");
            
            //server = new ServerSocket(74); 
            
            //IP Address:- 156.96.117.130
            //Host Name:- WIN-VF55I5M9BCG
            
            
            server = new ServerSocket(74, 0, InetAddress.getByName("0.0.0.0"));
            
            //server = new ServerSocket(74, 0, InetAddress.getByName("WIN-VF55I5M9BCG"));
        
            //server = new ServerSocket(74, 0, InetAddress.getByName("www.rbtrading.ca"));        
        
            //server = new ServerSocket(74, 0, InetAddress.getByName("156.96.117.130"));
            
           // server = new ServerSocket(74, 0, InetAddress.getByName("127.0.0.1"));
           
            clients = new ArrayList();
            
            new Thread(() -> {


                try
                {
                    while (true)
                    {               
                        System.out.println("maintaining clients");
                        maintain_clients();
                        
                        
                        
                        if (HTTPServer.started == false)
                        {
                            new Thread(() -> {
                                HTTPServer.start_web_server();
                            }).start();
                            HTTPServer.started = true;
                        }
                        
                        Socket clientSocket = server.accept(); 
                        
                        
                        InputStream inputStream = clientSocket.getInputStream();
                        OutputStream outputStream = clientSocket.getOutputStream();
                        doHandShakeToInitializeWebSocketConnection(inputStream, outputStream);
                        
                        
                        clients.add(new Object[]{clientSocket, inputStream, outputStream});
                        
                        
                        System.out.println("client accepted");
                        
                        System.out.println("clients = " + clients.size());
                    }
                }
                catch (Exception e)
                {// etc\hosts ???
                    e.printStackTrace();
                }

            }).start();
            
        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void send_data(String string)
    {
        try
        {
            
            maintain_clients();
                        
            
            for (int i = 0; i < clients.size(); i++)
            {
                Object[] arr = (Object[])clients.get(i);
                Socket s = (Socket)arr[0];
                if (s.isConnected())
                {
                    try
                    {
                        OutputStream outputStream = (OutputStream)arr[2];
                        outputStream.write(encode(string));
                        outputStream.flush();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    
    public static byte[] encode(String mess) throws IOException{
        byte[] rawData = mess.getBytes();

        int frameCount  = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) 129;

        if(rawData.length <= 125){
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        }else if(rawData.length >= 126 && rawData.length <= 65535){
            frame[1] = (byte) 126;
            int len = rawData.length;
            frame[2] = (byte)((len >> 8 ) & (byte)255);
            frame[3] = (byte)(len & (byte)255); 
            frameCount = 4;
        }else{
            frame[1] = (byte) 127;
            int len = rawData.length;
            frame[2] = (byte)((len >> 56 ) & (byte)255);
            frame[3] = (byte)((len >> 48 ) & (byte)255);
            frame[4] = (byte)((len >> 40 ) & (byte)255);
            frame[5] = (byte)((len >> 32 ) & (byte)255);
            frame[6] = (byte)((len >> 24 ) & (byte)255);
            frame[7] = (byte)((len >> 16 ) & (byte)255);
            frame[8] = (byte)((len >> 8 ) & (byte)255);
            frame[9] = (byte)(len & (byte)255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        byte[] reply = new byte[bLength];

        int bLim = 0;
        for(int i=0; i<frameCount;i++){
            reply[bLim] = frame[i];
            bLim++;
        }
        for(int i=0; i<rawData.length;i++){
            reply[bLim] = rawData[i];
            bLim++;
        }

        return reply;
    }

    
    private static void doHandShakeToInitializeWebSocketConnection(InputStream inputStream, OutputStream outputStream) throws UnsupportedEncodingException {
        String data = new Scanner(inputStream,"UTF-8").useDelimiter("\\r\\n\\r\\n").next();

        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();                 

            byte[] response = null;
            try {
                response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + DatatypeConverter.printBase64Binary(
                                MessageDigest
                                .getInstance("SHA-1")
                                .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                        .getBytes("UTF-8")))
                        + "\r\n\r\n")
                        .getBytes("UTF-8");
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                outputStream.write(response, 0, response.length);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

        }
    }
    
}
