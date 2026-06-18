package com.nirbhay.aetherstore.storage;

import java.util.ArrayList;
import java.util.List;
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
    
    private final ConcurrentHashMap<String, TemporalRingBuffer> db = new ConcurrentHashMap<>(); 
    private final ConcurrentHashMap<String,Long> expiryMap = new ConcurrentHashMap<>();
    
    //Function to add data to db
    public void set(String key , String value) 
    {
    	expiryMap.remove(key);
    	
    	db.computeIfAbsent(key, k-> new TemporalRingBuffer()).add(value);
    }
    
    //function to get data form db it also does the lazy eviction 
    public String get(String key) 
    {
    	 Long deathTime = expiryMap.get(key);
    	 if(deathTime != null && System.currentTimeMillis() > deathTime) 
    	 {
    		this.del(key);
    		return null;
    	 }
    	 TemporalRingBuffer buffer = db.get(key);
    	 if(buffer==null) return null;
    	 
    	 List<TemporalNode> history = buffer.getHistory();
    	 if(history.isEmpty()) return null;
    	 
    	 return history.get(0).getValue();
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
    
    public String getAt(String key , long targetTimestamp) 
    {
    	
    	Long deathTime = expiryMap.get(key);
    	if(deathTime !=null  && deathTime < System.currentTimeMillis() ) {
    		this.del(key);
    		return null;
    	}
    	TemporalRingBuffer buffer = db.get(key);
    	if(buffer ==null) return null;
    	List<TemporalNode> history = buffer.getHistory();
    	if(history==null ) return null;
    	for(TemporalNode node : history) 
    	{
    		if(node.getTimestamp() <= targetTimestamp) return  node.getValue();
    	}
    	 return null;
    }
    
    public List<TemporalNode> timeline(String key)
    {
    	TemporalRingBuffer buffer = db.get(key);
    	if(buffer == null) return new ArrayList<>();
    	return buffer.getHistory();
    	
    }
    
    
}
