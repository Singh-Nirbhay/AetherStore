package com.nirbhay.aetherstore.command;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.nirbhay.aetherstore.storage.StorageEngine;
import com.nirbhay.aetherstore.storage.TemporalNode;

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
	    	  
	      case "GETAT" :
	    	  if(command.length != 3)
	    		  return "-ERR wrong number of arguments for 'GETAT' command\r\n";
	    	  try 
	    	  {
	    		long targetTime = Long.parseLong(command[2]);
	    		String valAtTime = db.getAt(command[1], targetTime);
	    		if(valAtTime== null) return "$-1\r\n";
	    		int len = valAtTime.getBytes(StandardCharsets.UTF_8).length;
	    		return "$"+len+"\r\n"+valAtTime+"\r\n";
	    	  }
	    	  catch (NumberFormatException e) {
	    	        return "-ERR value is not a valid timestamp\r\n";
	    	    }
	    	  
	      case "TIMELINE":
	    	  if (command.length != 2) {
	    	        return "-ERR wrong number of arguments for 'TIMELINE' command\r\n";
	    	    }
	    	  List<TemporalNode>  history = db.timeline(command[1]);
	    	  if(history.isEmpty()) return "*0\r\n";
	    	  
	    	  StringBuilder sb = new StringBuilder("*" + history.size() + "\r\n");
	    	  for(TemporalNode node:history) 
	    	  {
	    		String record = node.getValue() + " @ " + node.getTimestamp() ;
	    		int recordLen = record.getBytes(StandardCharsets.UTF_8).length;
	    		sb
	    		.append("$")
	    		.append(recordLen)
	    		.append("\r\n")
	    		.append(record)
	    		.append("\r\n");
	    	  }
	    	  return sb.toString();
	        
	        default :
	        	return "-ERR unknown command\r\n";
	        
	     }
	}
}
