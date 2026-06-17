package com.nirbhay.aetherstore.storage;

import java.util.concurrent.ConcurrentHashMap;

public class StorageEngine
{
    private static final StorageEngine instance = new StorageEngine();
    
    private StorageEngine() 
    {
    	startActiveEviction();
    }
    
    public static  StorageEngine getInstance() 
    {
    	return instance;
    }
    
    private final ConcurrentHashMap<String, String> db = new ConcurrentHashMap<>(); 
    private final ConcurrentHashMap<String,Long> expiryMap = new ConcurrentHashMap<>();
    
    public void set(String key , String value) 
    {
    	expiryMap.remove(key);
    	db.put(key, value);
    }
    public String get(String key) 
    {
    	 Long deathTime = expiryMap.get(key);
    	 if(deathTime != null && System.currentTimeMillis() > deathTime) 
    	 {
    		this.del(key);
    		return null;
    	 }
    	 return db.get(key);
    }
    public boolean del(String key) 
    { 
    	  expiryMap.remove(key);
         return db.remove(key) != null;	
    }
    
    public boolean expire(String key,int seconds) 
    {
    	if(!db.containsKey(key)) return false;
    	Long deathTime = System.currentTimeMillis() + (seconds * 1000L);
    	
    	expiryMap.put(key, deathTime);
    	return true;
    	
    }
    
    private void startActiveEviction() 
    {
    	Thread janitorThread = new Thread(()-> {
    		 while(true) {
    			 try {Thread.sleep(1000);}
    			 catch(InterruptedException e) {
    				 e.printStackTrace();
    			 }
    			 Long now = System.currentTimeMillis();
    			 
    			 for(String key:expiryMap.keySet()) 
    			 {
    				Long deathTime  = expiryMap.get(key); 
    				if(deathTime != null && deathTime < now) 
    				{
    					System.out.println("Janitor swept key: "+ key);
    					this.del(key);
    				} 
    			 }
    			 
    		 }
    		
    	});
    	
    	janitorThread.setDaemon(true);
    	janitorThread.start();
    }
    
    
}
