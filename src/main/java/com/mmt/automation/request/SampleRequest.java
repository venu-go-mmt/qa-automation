package com.mmt.automation.request;

public class SampleRequest {
	
	private String key1 = "value1";
	private String key2 = "value2";
	
	public SampleRequest(String key1, String key2) {
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public String getKey1() {
		return key1;
	}
	public String getKey2() {
		return key2;
	}

}
