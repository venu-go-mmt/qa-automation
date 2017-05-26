package com.mmt.automation;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.mmt.automation.bo.TestDependencyBO;
import com.mmt.automation.bo.TestExecutionBO;

public class TestExecutionEngine {

	public WireMockRule mockRule = new WireMockRule(8080);
//	private Map<String, String> dependencyMap;
	
	public TestExecutionEngine() {
//		dependencyMap = new HashMap<>();
//		dependencyMap.put("dependency_AP", "/test/wiremock");
		mockRule.start();
	}
	
	public void shutdown() {
		mockRule.shutdown();
	}
	
	@Ignore
	@Test
	public TestExecutionBO executeTest(String searchPriceReq, String relativeURL, String requestMethod, 
			Map<String, String> requestHeaders, Map<String, TestDependencyBO> dependencyMap) throws JSONException, ClientProtocolException, IOException{
		for(Map.Entry<String, TestDependencyBO> entry : dependencyMap.entrySet()) {
			TestDependencyBO dependencyBO = entry.getValue();
			setDependencyAPIs(entry.getKey(), dependencyBO);
		}
//		try {
//			Thread.sleep(1000000L);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		TestExecutionBO testExecutionBO = executeRequest(searchPriceReq, relativeURL, requestMethod, requestHeaders);
//		for(Map.Entry<String, TestDependencyBO> entry : dependencyMap.entrySet()) {
//			testExecutionBO.setMockVerificationStatusMessage(verifyDependencyCalls(entry.getKey(), entry.getValue()));
//		}
		return testExecutionBO;
	}

	private Map<String, List<String>> verifyDependencyCalls(String dependency, TestDependencyBO dependencyBO) {
		Map<String, List<String>> dependencyVerificationStatus = new HashMap<>();
		RequestPatternBuilder stubRequest = null;
		if(HttpMethod.POST.name().equalsIgnoreCase(dependencyBO.getRequestMethod())) {
			stubRequest = postRequestedFor(urlEqualTo(dependencyBO.getRequestURL()));
		} else if(HttpMethod.GET.name().equalsIgnoreCase(dependencyBO.getRequestMethod())) {
			stubRequest = getRequestedFor(urlEqualTo(dependencyBO.getRequestURL()));
		}
		if(MapUtils.isNotEmpty(dependencyBO.getRequestHeaders())) {
			for(Map.Entry<String, String> entry : dependencyBO.getRequestHeaders().entrySet()) {
				try {
					verify(stubRequest.withHeader(entry.getKey(),
							equalTo(entry.getValue())));
					System.out.println("Verified:"+dependency);
				} catch (AssertionError e) {
					List<String> verificationStatuses = dependencyVerificationStatus.get(dependency);
					if(verificationStatuses == null) {
						verificationStatuses = new ArrayList<>();
						dependencyVerificationStatus.put(dependency, verificationStatuses);
					}
					verificationStatuses.add(e.getMessage());
				}
			}
		} else {
			try {
				verify(stubRequest);
				System.out.println("Verified:"+dependency);
			} catch (AssertionError e) {
				List<String> verificationStatuses = dependencyVerificationStatus.get(dependency);
				if(verificationStatuses == null) {
					verificationStatuses = new ArrayList<>();
					dependencyVerificationStatus.put(dependency, verificationStatuses);
				}
				verificationStatuses.add(e.getMessage());
			}
		}
		return dependencyVerificationStatus ;
	}

	private TestExecutionBO executeRequest(String searchPriceReq, String relativeURL, String requestMethod, 
			Map<String, String> requestHeaders)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		StringEntity stringEntity = new StringEntity(searchPriceReq);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpUriRequest httpRequest = null;
		if(HttpMethod.POST.name().equalsIgnoreCase(requestMethod)) {
			httpRequest = new HttpPost(relativeURL);
			((HttpPost)httpRequest).setEntity(stringEntity);
		} else if(HttpMethod.GET.name().equalsIgnoreCase(requestMethod)) {
			httpRequest = new HttpGet(relativeURL);
		}
		for(Map.Entry<String, String> entry : requestHeaders.entrySet()) {
			httpRequest.addHeader(entry.getKey(), entry.getValue());
		}
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		String responseBody = EntityUtils.toString(httpResponse.getEntity());
		return new TestExecutionBO().setResponseBody(responseBody).setResponseStatus(httpResponse.getStatusLine().getStatusCode());
	}

	private void setDependencyAPIs(String dependency, TestDependencyBO dependencyBO) {
//		stubFor(post(urlEqualTo("/partner/aps/search")).withHeader("Content-Type", equalTo("application/xml"))
//				.withRequestBody(equalToXml(apRequest))
//				.willReturn(aResponse().withStatus(200).withBody(apResponse)
//				.withHeader("Content-Type", "application/json")));
		
		ResponseDefinitionBuilder stubResponse = aResponse().withStatus(dependencyBO.getStatusCode()).withBody(dependencyBO.getResponseString());
		if(MapUtils.isNotEmpty(dependencyBO.getResponseHeaders())) {
			for(Map.Entry<String, String> entry : dependencyBO.getResponseHeaders().entrySet()) {
				stubResponse = stubResponse.withHeader(entry.getKey(), entry.getValue());
			}
		}
		
		MappingBuilder stubRequest = null;
		if(HttpMethod.POST.name().equalsIgnoreCase(dependencyBO.getRequestMethod())) {
			stubRequest = post(urlEqualTo(dependencyBO.getRequestURL()));
			if(dependencyBO.getRequestString() != null)
				stubRequest = stubRequest.withRequestBody(equalTo(dependencyBO.getRequestString()));
			if(MapUtils.isNotEmpty(dependencyBO.getRequestHeaders())) {
				for(Map.Entry<String, String> entry : dependencyBO.getRequestHeaders().entrySet()) {
					stubRequest = stubRequest.withHeader(entry.getKey(), equalTo(entry.getValue()));
				}
			}
		}
		else if(HttpMethod.GET.name().equalsIgnoreCase(dependencyBO.getRequestMethod()))
			stubRequest = get(dependencyBO.getRequestURL());
		System.out.println("Stub started for:"+dependency);
		stubFor(stubRequest.willReturn(stubResponse));
	}

//	@Test
//	public void test_test() throws ClientProtocolException, IOException {
//
//		InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("apresponse.json");
//		String AP_RESPONSE = convertInputStreamToString(jsonInputStream);
//
//		setDependencyAPIs(AP_RESPONSE);
//		
//		StringEntity stringEntity = new StringEntity(REQUEST_BODY);
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		HttpPost httpPost = new HttpPost("http://localhost:8989/partner/aps/search");
//		httpPost.addHeader("Content-Type", "application/xml");
//		httpPost.setEntity(stringEntity);
//		HttpResponse httpResponse = httpClient.execute(httpPost);
//
//		verify(postRequestedFor(urlEqualTo("/partner/aps/search")).withHeader("Content-Type",
//				equalTo("application/xml")));
//		
//		assertEquals(200, httpResponse.getStatusLine().getStatusCode());
//	}
}

