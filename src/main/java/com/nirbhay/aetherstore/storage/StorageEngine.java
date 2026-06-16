package com.nirbhay.aetherstore.storage;

import java.util.concurrent.ConcurrentHashMap;

public class StorageEngine
{
    private static final StorageEngine instance = new StorageEngine();
    
    private StorageEngine() {}
    
    public static  StorageEngine getInstance() 
    {
    	return instance;
    }
    
    private final ConcurrentHashMap<String, String> db = new ConcurrentHashMap<>(); 
    
    public void set(String key , String value) 
    {
    	db.put(key, value);
    }
    public String get(String key) 
    {
    	 return db.get(key);
    }
    public boolean del(String key) 
    {
         return db.remove(key) != null;	
    }
    
    
}
