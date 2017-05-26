package com.mmt.automation.bo;

import java.util.List;
import java.util.Map;

public class TestExecutionBO {
	
	private String responseBody;
	private int responseStatus;
	private Map<String, List<String>> mockVerificationStatusMessage;
	public String getResponseBody() {
		return responseBody;
	}
	public TestExecutionBO setResponseBody(String responseBody) {
		this.responseBody = responseBody;
		return this;
	}
	public int getResponseStatus() {
		return responseStatus;
	}
	public TestExecutionBO setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
		return this;
	}
	public Map<String, List<String>> getMockVerificationStatusMessage() {
		return mockVerificationStatusMessage;
	}
	public void setMockVerificationStatusMessage(Map<String, List<String>> mockVerificationStatusMessage) {
		this.mockVerificationStatusMessage = mockVerificationStatusMessage;
	}
}
