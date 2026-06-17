package com.nirbhay.aetherstore.command;

import java.nio.charset.StandardCharsets;

import com.nirbhay.aetherstore.storage.StorageEngine;

public class CommandEngine 
{
	public static String execute(String[] command) 
	{   
		
		if (command==null || command.length ==0) 
		{
			return "-ERR Empty command\r\n";
		}
		String mainCommand = command[0].toUpperCase();
		
		StorageEngine db = StorageEngine.getInstance();
	         
	     switch(mainCommand)
	     {
	        
	     case "SET":
	    	 if(command.length != 3) 
	        		return "-ERR wrong number of arguments for 'SET' command\r\n";
	    	 
	    	 db.set(command[1], command[2]);
	    	 return "+OK\r\n";
	    	 
	     case "GET":
	    	 if(command.length != 2) 
	        		return "-ERR wrong number of arguments for 'GET' command\r\n";
	    	 
	    	 String val = db.get(command[1]);
	    	 if(val==null) return "$-1\r\n";
	    	 
	    	 int byteLenG = val.getBytes(StandardCharsets.UTF_8).length;
	    	 return "$" + byteLenG + "\r\n" + val + "\r\n";
	    	 
	     case "DEL":
	    	 if(command.length != 2) 
	        		return "-ERR wrong number of arguments for 'DEL' command\r\n";
	    	 
	    	 boolean deleted = db.del(command[1]);
	    	 if(deleted) return ":1\r\n";
	    	 return ":0\r\n";
	    	 
	        
	      case "PING" :
	        	if(command.length != 1) 
	        		return "-ERR wrong number of arguments for 'PING' command\r\n";
	        	
	            return "+PONG\r\n";
	            
	      case "ECHO" :
	            
	        	if(command.length != 2) 
	        		return "-ERR wrong number of arguments for 'echo' command\r\n";
	        	
	        	String bulkString = command[1];
		    	int byteLen = bulkString.getBytes(StandardCharsets.UTF_8).length;	
		        return ("$"+byteLen+"\r\n"+bulkString+"\r\n");
	        	
	      case "EXPIRE":
	    	  if(command.length != 3) 
	        		return "-ERR wrong number of arguments for 'EXPIRE' command\r\n";
	    	  
	    	  int seconds;
	    	  try 
	    	  {
	    		seconds = Integer.parseInt(command[2]);  
	    	  }catch(NumberFormatException e) {
	    		  return "-ERR value is not an integer or out of range\r\n";
	    	  }
	    	  Boolean success = db.expire(command[1], seconds);
	    	  if(success) 
	    		  return ":1\r\n";
	    	  return ":0\r\n";
	        
	        default :
	        	return "-ERR unknown command\r\n";
	        
	     }
	}
}
