package protocol;

import java.io.IOException;
import java.io.InputStream;

public class RESPParser 
{
      private InputStream input ;
      
      public RESPParser(InputStream input)
      {
    	 this.input = input;  
      }
      
      private String readLine() throws IOException {
    	  int b;
    	  StringBuilder data = new StringBuilder();
    	  while((b=input.read())!=-1)
    	  {
    		 if(b==13) 
    		 {
    			 int next = input.read();
    			 if(next == 10) break;
    			 if(next==-1) break;
    			 data.append((char)b);
    			 data.append((char)next);
    		 }
    		 else data.append((char) b);
    	  }
    	  
    	 
    	  return data.toString();
    	  
      }
      public String[] parseCommand() throws Exception
      {
    	 String currStream = readLine() ;
    	 
    		 if(currStream == null || currStream.isEmpty()) 
        		 throw new Exception("Disconnected");
    		 if(!currStream.startsWith("*"))
    			 throw new Exception("Invalid RESP Array");
    	 
    	 int respArraySize = Integer.parseInt(currStream.substring(1));
    	 String [] respArray = new String[respArraySize];
    	 
    	 for(int i=0;i<respArraySize;i++) 
    	 {
    		 readLine();
    		 String data = readLine();
    		 respArray[i] = data;
    		 
    	 }
    	  return respArray;
      }
}
