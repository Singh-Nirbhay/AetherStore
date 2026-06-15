package command;

import java.nio.charset.StandardCharsets;

public class CommandEngine 
{
	public static String execute(String[] command) 
	{   
		
		if (command==null || command.length ==0) 
		{
			return "-ERR Empty command\r\n";
		}
		String mainCommand = command[0].toUpperCase();
	         
	     switch(mainCommand)
	     {
	        case "PING" :
	        	if(command.length != 1) 
	        		return "-ERR wrong number of arguments for 'PING' command\r\n";
	        	
	        	 return "+PONG\r\n";
	        case "ECHO" :
	        {    
	        	if(command.length != 2) return "-ERR wrong number of arguments for 'echo' command\r\n";
	        	
	        	String bulkString = command[1];
		    	int byteLen = bulkString.getBytes(StandardCharsets.UTF_8).length;	
		        return ("$"+byteLen+"\r\n"+bulkString+"\r\n");
	        	
	        }
	        default :
	        	return "-ERR unknown command\r\n";
	        
	     }
	}
}
