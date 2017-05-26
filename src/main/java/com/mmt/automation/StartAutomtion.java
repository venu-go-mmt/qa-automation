package com.mmt.automation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.mmt.automation.bo.TestExecutionBO;
import com.mmt.automation.datareader.ExcelDataReader;
import com.mmt.automation.formatter.APIFormatter;
import com.mmt.automation.formatter.bo.TestCaseBO;

public class StartAutomtion {

	private static final String SERVICE_UNDER_TEST = "http://localhost:4000";

	public static void main(String[] args) {
		new StartAutomtion().start();
	}

	private ExcelDataReader dataReader;
	private APIFormatter apiFormatter;
	private TestExecutionEngine testExecutionEngine;
	
	public StartAutomtion() {
		dataReader = new ExcelDataReader();
		apiFormatter = new APIFormatter();
		testExecutionEngine = new TestExecutionEngine();
	}

	private void start() {
		Map<String, List<Map<String, Map<String, Map<String, String>>>>> testScenarioMap = dataReader.getTestScenarios();
		Map<String, TestCaseBO> testCases = apiFormatter.formatTests(testScenarioMap);
		for(String testCaseId : testCases.keySet()) {
			TestCaseBO testCaseBO = testCases.get(testCaseId);
			try {
				TestExecutionBO executeTest = testExecutionEngine.executeTest(testCaseBO.getRequestBody(), SERVICE_UNDER_TEST + testCaseBO.getRequestURL(), 
						testCaseBO.getRequestMethod(),testCaseBO.getRequestHeaders(), testCaseBO.getDependencyBO());
				System.out.println("Test Case:"+testCaseId);
//				System.out.println("actual response:"+executeTest.getResponseBody());
//				System.out.println("expected response:"+testCaseBO.getResponseBody());
				System.out.println(testCaseBO.getResponseBody().equalsIgnoreCase(executeTest.getResponseBody()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		testExecutionEngine.shutdown();
	}

}
