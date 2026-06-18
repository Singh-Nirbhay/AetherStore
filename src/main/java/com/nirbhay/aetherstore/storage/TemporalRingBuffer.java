package com.nirbhay.aetherstore.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TemporalRingBuffer 
{
    private final int maxSize = 10;
    private final LinkedList<TemporalNode> history = new LinkedList<>();
    
    public synchronized void add(String value) 
    {
    	long now = System.currentTimeMillis();
    	TemporalNode newNode = new TemporalNode(value,now);
    	history.addFirst(newNode);
    	if(history.size()> maxSize) history.removeLast();
    }
    public synchronized List<TemporalNode> getHistory(){
    	return new ArrayList<>(history);
    	
    }
}
