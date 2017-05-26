package com.mmt.automation.formatter.bo;

import java.util.Map;

import com.mmt.automation.bo.TestDependencyBO;

public class TestCaseBO {
	
	private String testCaseId;
	private String requestURL;
	private String requestMethod;
	private String requestBody;
	private String responseBody;
	private Map<String, String> requestHeaders;
	private Map<String, String> responseHeaders;
	
	private Map<String , TestDependencyBO> dependencyBO;
	
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
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
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
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
	public Map<String, TestDependencyBO> getDependencyBO() {
		return dependencyBO;
	}
	public void setDependencyBO(Map<String, TestDependencyBO> dependencyBO) {
		this.dependencyBO = dependencyBO;
	}

}
