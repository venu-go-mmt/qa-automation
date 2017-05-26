package com.mmt.automation.bo;

public class TestVerificationBO {
	
	private String dependency;
	private boolean status;
	private String failureMessage;
	
	public String getDependency() {
		return dependency;
	}
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getFailureMessage() {
		return failureMessage;
	}
	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}
	
}
