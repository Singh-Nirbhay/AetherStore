package com.nirbhay.aetherstore.storage;

public class TemporalNode 
{
	private final  String value;
	private final long  timestamp;
	
	
	
	public String getValue() {
		return value;
	}



	public long getTimestamp() {
		return timestamp;
	}



	public TemporalNode(String value , long timestamp) 
	{
		this.value = value;
		this.timestamp = timestamp;
	}
	
}
