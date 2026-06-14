package com.nirbhay.aetherstore;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import protocol.RESPParser;

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
        
         RESPParser parser = new RESPParser(input);
         
         while(true) 
         {
        	 try 
        	 {
        		 String[] command = parser.parseCommand();
            	 System.out.println("Parsed command : " + Arrays.toString(command) );   
        	 } catch (Exception e) {
             	if(e.getMessage().equals("Disconnected")) 
             	{
             		System.out.println("Client Disconnected");
             		break;
             	}
            		
            	else System.out.println("Protocol Error :" + e.getMessage());
    		}
        	 
         }
       
		} catch (IOException e1) {
			
			e1.printStackTrace();
		} 
         
         
	  finally
	  {
		try 
		{
			clientSocket.close();
		}  
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	  }
		
	}

}
