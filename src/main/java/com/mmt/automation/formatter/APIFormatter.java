package com.mmt.automation.formatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.mmt.automation.bo.TestDependencyBO;
import com.mmt.automation.formatter.bo.TestCaseBO;

public class APIFormatter {

	private String requestTag = "Request";
	private String responseTag = "Response";
	private String testSheetTag = "testcases";
	private String headerTag = "header:";
	private String urlTag = "URL";
	private String methodTag = "Method";
	
	public Map<String, TestCaseBO> formatTests(Map<String, List<Map<String, Map<String, Map<String, String>>>>> testScenarioMap) {
		Map<String, TestCaseBO> testCases = new HashMap<>();
		testScenarioMap.get(testSheetTag).forEach(testScenarios->testScenarios.forEach((testCaseId,testCase)->{
			TestCaseBO testCaseBO = new TestCaseBO();
			testCaseBO.setDependencyBO(new HashMap<>());
			testCases.put(testCaseId, testCaseBO);
		}));
		for(String sheet : testScenarioMap.keySet()) {
			List<Map<String, Map<String, Map<String, String>>>> testScenarios = testScenarioMap.get(sheet);
			if(testSheetTag.equalsIgnoreCase(sheet))
				formatAndUpdateTestCases(testCases, testScenarios);
			else
				formatAndUpdateDepdenciesCases(sheet, testCases, testScenarios);
		}
		
		testCases.forEach((testCaseId, testCaseBO)->{
			System.out.println(testCaseId);
			System.out.println("request:"+testCaseBO.getRequestBody());
			System.out.println("response:"+testCaseBO.getResponseBody());
			System.out.println("request header:"+testCaseBO.getRequestHeaders());
			System.out.println("response header:"+testCaseBO.getResponseHeaders());
			System.out.println("response url:"+testCaseBO.getRequestURL());
			System.out.println("response method:"+testCaseBO.getRequestMethod());
			testCaseBO.getDependencyBO().forEach((dependency, testDependencyBO)->{
				System.out.println("dependency:"+dependency);
				System.out.println("dependency Request:"+testDependencyBO.getRequestString());
				System.out.println("dependency Response:"+testDependencyBO.getResponseString());
				System.out.println("dependency request header:"+testDependencyBO.getRequestHeaders());
				System.out.println("dependency response header:"+testDependencyBO.getResponseHeaders());
				System.out.println("dependency url:"+testDependencyBO.getRequestURL());
				System.out.println("dependency method:"+testDependencyBO.getRequestMethod());
			});
		});
		return testCases;
	}

	private void formatAndUpdateTestCases(Map<String, TestCaseBO> testCases, List<Map<String, Map<String, Map<String, String>>>> testScenarios) {
		testScenarios.forEach(scenario->{
			scenario.forEach((testCaseId, testCase)->{
				final TestCaseBO testCaseBO = testCases.get(testCaseId);
				testCase.forEach((criteria, criteriaFields)->{
					HeadersAndBody headersAndBody = getHeadersAndBody(criteriaFields);
					if(requestTag.equalsIgnoreCase(criteria)) {
						testCaseBO.setRequestURL(headersAndBody.url);
						testCaseBO.setRequestMethod(headersAndBody.requestMethod);
						testCaseBO.setRequestBody(headersAndBody.body);
						testCaseBO.setRequestHeaders(headersAndBody.headers);
					} else if(responseTag.equalsIgnoreCase(criteria)) {
						testCaseBO.setResponseBody(headersAndBody.body);
						testCaseBO.setResponseHeaders(headersAndBody.headers);
					}
				});
			});
		});
	}

	private void formatAndUpdateDepdenciesCases(String dependency, Map<String, TestCaseBO> testCases, List<Map<String, Map<String, Map<String, String>>>> testScenarios) {
		
		testScenarios.forEach(scenario->{
			scenario.forEach((testCaseId, testCase)->{
				final TestCaseBO testCaseBO = testCases.get(testCaseId);
				final Map<String, TestDependencyBO> dependencyBO = testCaseBO.getDependencyBO();
				TestDependencyBO testDependencyBO = new TestDependencyBO();
				dependencyBO.put(dependency, testDependencyBO);
				testCase.forEach((criteria, criteriaFields)->{
					HeadersAndBody headersAndBody = getHeadersAndBody(criteriaFields);
					if(requestTag.equalsIgnoreCase(criteria)) {
						testDependencyBO.setRequestURL(headersAndBody.url);
						testDependencyBO.setRequestMethod(headersAndBody.requestMethod);
						testDependencyBO.setRequestString(headersAndBody.body);
						testDependencyBO.setRequestHeaders(headersAndBody.headers);
					} else if(responseTag.equalsIgnoreCase(criteria)) {
						testDependencyBO.setResponseString(headersAndBody.body);
						testDependencyBO.setResponseHeaders(headersAndBody.headers);
					}
				});
			});
		});
	}

	private HeadersAndBody getHeadersAndBody(Map<String, String> criteriaFields) {
		HeadersAndBody headersAndBody = new HeadersAndBody();
		Map<String, String> bodyFields = new HashMap<>();
		for(String field : criteriaFields.keySet()) {
			if(field.startsWith(headerTag)) {
				String headerName = field.substring(headerTag.length(), field.length());
				headersAndBody.headers.put(headerName, criteriaFields.get(field));
			} else if(urlTag.equalsIgnoreCase(field)) {
				headersAndBody.url = criteriaFields.get(field);
			} else if(methodTag.equalsIgnoreCase(field)) {
				headersAndBody.requestMethod = criteriaFields.get(field);
			}
			else {
				bodyFields.put(field, criteriaFields.get(field));
			}
		}
		headersAndBody.body = new JSONObject(bodyFields).toString();
		return headersAndBody;
	}
	
	private static class HeadersAndBody {
		private Map<String, String> headers;
		private String body;
		private String url;
		private String requestMethod;
		private HeadersAndBody() {
			headers = new HashMap<>();
		}
	}
	
}
