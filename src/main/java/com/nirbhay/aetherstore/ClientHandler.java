package com.nirbhay.aetherstore;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
	private Socket clientSocket ;
	
	ClientHandler(Socket clientSocket)
	{
	     this.clientSocket = clientSocket;	
	}
	
	@Override
	public void run() {
	  try {
		 InputStream input = clientSocket.getInputStream();
         byte[] buffer = new byte[1024];
         String sb = "";
        
         
		 // This blocks until the client sends data
        while(true) {
     	   int bytesRead = input.read(buffer);
     	   
            if (bytesRead != -1) {
                String message = new String(buffer, 0, bytesRead);
                System.out.println("Received raw data: " + message);
                sb += message;
                
            }else if(bytesRead==-1)  break;
        }
        
        System.out.println("Client disconnected. the message Recived is "+ sb);
        
         
       
        clientSocket.close();
		} 
         
         catch (IOException e) {
        	System.err.println("Server exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

}
