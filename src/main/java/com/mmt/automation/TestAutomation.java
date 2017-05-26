package com.mmt.automation;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mmt.automation.request.SampleRequest;
import com.mmt.automation.response.SampleResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class TestAutomation {
	
	
	private WireMockRule wireMockRule;
	private HttpClient httpClient;
	
	public TestAutomation() {
		wireMockRule = new WireMockRule(8080);
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(10000)
				.setConnectTimeout(10000).setSocketTimeout(10000).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(10);
		connManager.setMaxTotal(10);
		httpClient = HttpClients.custom().setDefaultRequestConfig(config).setConnectionManager(connManager).build();
	}
	
	public void testWiremockGet() {
		wireMockRule.start();
		stubFor(get(urlEqualTo("/test/wiremock")).willReturn(aResponse().withStatus(200).withBody("success wiremock")));
		HttpGet httpGet = new HttpGet("http://localhost:4000/testsample");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
			System.out.println("responseString:"+responseString);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			wireMockRule.shutdown();
		}
	}
	
	public void testWiremockPost(String sampleRequest, SampleResponse sampleResponse) {
		stubFor(post("/test/wiremock").willReturn(aResponse().withStatus(200).withBody(objectToJson(sampleResponse))));
		try {
			System.out.println("sampleRequest:"+sampleRequest);
			System.out.println("sampleResponse:"+sampleResponse);
			HttpGet httpGet = new HttpGet("http://localhost:4000/testsample?req="+URLEncoder.encode(sampleRequest, "UTF-8"));
			HttpResponse response = httpClient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
//			verify(postRequestedFor(urlEqualTo("/test/wiremock")).withHeader("Content-Type", equalTo("application/json")));
			verify(postRequestedFor(urlEqualTo("/test/wiremock")).withRequestBody(equalToJson(sampleRequest)));
			SampleResponse actualResponse = jsonToObject(responseString, SampleResponse.class);
			System.out.println(actualResponse.getRes1().equals(sampleResponse.getRes1()));
			System.out.println(actualResponse.getRes2().equals(sampleResponse.getRes2()));
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private <T> String objectToJson(T object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private <T> T jsonToObject(String jsonString, Class<T> t) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (T) mapper.readValue(jsonString, t);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void triggerWiremockTests() {
		wireMockRule.start();
		Thread thread1 = new Thread(new Runnable() {
			
			public void run() {
				String sampleRequest = objectToJson(new SampleRequest("key11", "key12"));
//				String sampleResponse = objectToJson();
				testWiremockPost(sampleRequest, new SampleResponse("res11", "res12"));
			}
		});
		Thread thread2 = new Thread(new Runnable() {
			
			public void run() {
				String sampleRequest = objectToJson(new SampleRequest("key21", "key22"));
				testWiremockPost(sampleRequest, new SampleResponse("res11", "res12"));				
			}
		});
		thread1.start();
//		thread2.start();
		try {
			thread1.join();
//			thread2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wireMockRule.shutdown();
	}
	
	public static void main(String[] args) {
//		new TestAutomation().testWiremockGet();
		new TestAutomation().triggerWiremockTests();
	}

}
