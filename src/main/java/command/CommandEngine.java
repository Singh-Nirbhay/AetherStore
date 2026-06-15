package command;

public class CommandEngine 
{
	public static String execute(String[] command) 
	{
	     String mainCommand = command[0].toUpperCase();
	         
	     switch(mainCommand)
	     {
	        case "PING" :
	        	return "+PONG\r\n";
	        case "ECHO" :
	        {    
	        	String bulkString = command[1].toUpperCase();
	    	    int bulkStringLen = bulkString.length();	
	        	return ("$"+bulkStringLen+"\r\n"+bulkString+"\r\n");
	        }
	        default :
	        	return "-ERR unknown command\r\n";
	        
	     }
	}
}
