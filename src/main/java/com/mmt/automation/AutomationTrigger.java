package com.mmt.automation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.mmt.automation.ap.dependency.RequestConstants;
import com.mmt.automation.bo.TestDependencyBO;
import com.mmt.automation.bo.TestExecutionBO;

public class AutomationTrigger {

	private TestExecutionEngine testExecutionEngine;
	private String testService = "http://localhost:4000";

	public AutomationTrigger() {
		testExecutionEngine = new TestExecutionEngine();
	}
	
	private String AP_DEPDENCY_REQUEST_BODY = "<MMTHotelSearchRequest><POS><CorrelationKey>FOO_CORRELATION_KEY</CorrelationKey>"
			+ "<Requestor type=\"B2CAgent\" idContext=\"312165\" channel=\"B2Cweb\"/>"
			+ "<Source iSOCurrency=\"USD\" applicationID=\"410\"/><SourceIp>10.106.5.103</SourceIp>"
			+ "</POS><ResultTransformer><GuestRecommendationEnabled maxRecommendations=\"1\">true</GuestRecommendationEnabled>"
			+ "<PriceBreakupEnabled>true</PriceBreakupEnabled></ResultTransformer><SearchCriteria><Criterion><Area>"
			+ "<CityCode>BOM</CityCode><CountryCode>IN</CountryCode></Area><HotelRef id=\"200704092047307700\" idContext=\"\"/>"
			+ "<MMTFreeCancellationOff>false</MMTFreeCancellationOff><RoomStayCandidates><RoomStayCandidate><"
			+ "GuestCounts><GuestCount count=\"2\" ageQualifyingCode=\"10\"/></GuestCounts></RoomStayCandidate></RoomStayCandidates>"
			+ "<StayDateRanges><StayDateRange start=\"2017-05-19\" end=\"2017-05-20\"/>"
			+ "</StayDateRanges></Criterion></SearchCriteria></MMTHotelSearchRequest>";
	
	private void startAutomation() {
		try {
//			startServiceAutomation();
			startNodeJSAutomation();
//			startAPSearchPriceAutomation();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startServiceAutomation() throws JSONException, ClientProtocolException, IOException {
		InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("search_price_request.json");
		String searchPriceReq = convertInputStreamToString(jsonInputStream);
		jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("search_price_response.json");
		String searchPriceRes = convertInputStreamToString(jsonInputStream);
		String searchPriceURL = testService+"/api/v2.0/hotels/searchPrice";
//		TestExecutionBO testExecutionBO = testExecutionEngine.executeTest(searchPriceReq, searchPriceURL, getRequestHeaderForNodeJS(), getDependencyForService());
//		System.out.println("Response body:"+testExecutionBO.getResponseBody());
//		JSONAssert.assertEquals(searchPriceRes, testExecutionBO.getResponseBody(), false);
	}
	
	private Map<String, TestDependencyBO> getDependencyForService() {
		InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("apresponse.json");
		String apResponse = convertInputStreamToString(jsonInputStream);
		// TODO Auto-generated method stub
		HashMap<String, TestDependencyBO> testExecutionBO = new HashMap<>();
		TestDependencyBO apDependencyBO = new TestDependencyBO();
		apDependencyBO.setStatusCode(200);
		apDependencyBO.setRequestString(AP_DEPDENCY_REQUEST_BODY);
		HashMap<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Content-Type", "application/xml");
		apDependencyBO.setRequestHeaders(requestHeaders );
		apDependencyBO.setResponseString(apResponse);
		HashMap<String, String> responseHeaders = new HashMap<>();
		responseHeaders.put("Content-Type", "application/json");
		apDependencyBO.setResponseHeaders(responseHeaders );
		testExecutionBO.put("/partner/aps/search", apDependencyBO);
		
//		TestDependencyBO cdfDependencyBO = new TestDependencyBO();
//		testExecutionBO.put("", cdfDependencyBO );
		return testExecutionBO;
	}

	private void startNodeJSAutomation() throws ClientProtocolException, JSONException, IOException {
		String apiURL = testService + "/testsample";
//		TestExecutionBO testExecutionBO = testExecutionEngine.executeTest("{\"key1\":\"key11\",\"key2\":\"key12\"}", apiURL, getRequestHeaderForNodeJS() ,getDependencyForNodeJS());
//		System.out.println("Response body:"+testExecutionBO.getResponseBody());
//		System.out.println("Dependency Verification:"+testExecutionBO.getMockVerificationStatusMessage());
//		JSONAssert.assertEquals("{\"res1\":\"res11\",\"res2\":\"res12\"}", testExecutionBO.getResponseBody(), false);
	}
	
	private void startAPSearchPriceAutomation() throws ClientProtocolException, JSONException, IOException {
		String apiURL = "http://172.16.117.143:7/partner/aps/search";
//		TestExecutionBO testExecutionBO = testExecutionEngine.executeTest(RequestConstants.AP_REQUEST_BODY , apiURL, getRequestHeaderForAPSearchPrice(), getDepdencyForAPSearchPrice());
//		System.out.println("Response body:"+testExecutionBO.getResponseBody());
//		System.out.println("Dependency Verification:"+testExecutionBO.getMockVerificationStatusMessage());
	}
	
	private HashMap<String, String> getRequestHeaderForNodeJS() {
		HashMap<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Content-Type", "application/json");
		return requestHeaders;
	}
	
	private HashMap<String, TestDependencyBO> getDependencyForNodeJS() {
		HashMap<String, TestDependencyBO> dependencies = new HashMap<>();
		TestDependencyBO dependency = new TestDependencyBO();
		dependency.setStatusCode(200);
		dependency.setRequestString("{\"key1\":\"key11\",\"key2\":\"key12\"}");
		HashMap<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		dependency.setRequestHeaders(requestHeaders );
		dependency.setResponseString("{\"res1\":\"res11\",\"res2\":\"res12\"}");
		HashMap<String, String> responseHeaders = new HashMap<>();
		responseHeaders.put("Content-Type", "application/json");
		dependency.setResponseHeaders(responseHeaders);
		dependencies.put("/test/wiremock", dependency);
		return dependencies;
	}
	
	public HashMap<String, String> getRequestHeaderForAPSearchPrice() {
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/xml");
		headers.put("Content-Type", "application/xml");
		return headers ;
	}
	
	public HashMap<String, TestDependencyBO> getDepdencyForAPSearchPrice() {
		HashMap<String, TestDependencyBO> dependencies = new HashMap<>();
		TestDependencyBO cacheDependency = new TestDependencyBO();
		cacheDependency.setStatusCode(200);
		InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("apdependencycacherequest.json");
		cacheDependency.setRequestString(convertInputStreamToString(jsonInputStream));
		jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("apdependencycacheresponse.json");
		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("Content-Type", "application/json");
		cacheDependency.setRequestHeaders(requestHeaders );
		cacheDependency.setResponseString(convertInputStreamToString(jsonInputStream));
		dependencies.put("/dmc-cache/cache/get", cacheDependency );
		TestDependencyBO engineDependency = new TestDependencyBO();
		engineDependency.setStatusCode(200);
		engineDependency.setRequestString(RequestConstants.AP_ENGINE_REQUEST_BODY);
		requestHeaders.put("Accept", "application/xml");
		requestHeaders.put("Content-Type", "application/xml");
		engineDependency.setRequestHeaders(requestHeaders );
		engineDependency.setResponseString(RequestConstants.AP_ENGINE_RESPONSE_BODY);
		dependencies.put("/HotelsSOA/hotels/search/v1.0/hotelExtendedSearch.xml", engineDependency );
		TestDependencyBO droolsDependency = new TestDependencyBO();
		droolsDependency.setStatusCode(200);
		droolsDependency.setRequestString(RequestConstants.AP_DROOLS_REQUEST_BODY);
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("Content-Type", "application/json");
		droolsDependency.setRequestHeaders(requestHeaders );
		jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("droolsdependencycacheresponse.json");
		droolsDependency.setResponseString(convertInputStreamToString(jsonInputStream));
		dependencies.put("/drools-service-web/droolsService/applyMetaRules", droolsDependency );
		return dependencies ;
	}
	
	public void stopAutomation() {
		System.out.println("Calling shutdown");
		testExecutionEngine.shutdown();
	}
	
	private static String convertInputStreamToString(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream, "UTF-8");
		String string = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return string;
	}
	
	public static void main(String[] args) {
		AutomationTrigger automationTrigger = new AutomationTrigger();
		automationTrigger.startAutomation();
		automationTrigger.stopAutomation();
	}

}
