package com.nirbhay.aetherstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.nirbhay.aetherstore.command.CommandEngine;
import com.nirbhay.aetherstore.protocol.RESPParser;

public class ClientHandler implements Runnable {
    
	private Socket clientSocket ;
	
	
	ClientHandler(Socket clientSocket)
	{
	     this.clientSocket = clientSocket;	
	}
	
	@Override
	public void run() {
	  try {
		 OutputStream output = clientSocket.getOutputStream();
		 InputStream input = clientSocket.getInputStream();
         String successMsg = "+Connection Successfull\r\n";
         byte[] successMsgByte = successMsg.getBytes();
         output.write(successMsgByte);
         output.flush();
         RESPParser parser = new RESPParser(input);
         
         while(true) 
         {
        	 try 
        	 {
        		 String[] command = parser.parseCommand();
        		 
        		 String  response = CommandEngine.execute(command);
        		 byte[] responseByte = response.getBytes();
        		 output.write(responseByte);
        		 output.flush();
            	 System.out.println("Parsed command : " + Arrays.toString(command) );   
        	 } catch (Exception e) {
             	if(e.getMessage().equals("Disconnected")) 
             	{
             		System.out.println("Client Disconnected");
             		break;
             	}
            		
            	else System.out.println("Protocol Error :" + e.getMessage() + " Retry");
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
