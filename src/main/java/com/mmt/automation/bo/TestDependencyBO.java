package com.mmt.automation.bo;

import java.util.List;
import java.util.Map;

public class TestDependencyBO {
	
	private String requestURL;
	private String requestMethod;
	private String requestString;
	private String responseString;
	private Map<String, String> requestHeaders;
	private Map<String, String> responseHeaders;
	private int statusCode;
	
	public String getRequestURL() {
		return requestURL;
	}
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getRequestString() {
		return requestString;
	}
	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}
	public String getResponseString() {
		return responseString;
	}
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}
	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	

}
