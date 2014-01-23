package com.woonoz.proxy.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

public class HttpGetRequestHandlerTest {

	@Test @Ignore
	public void testGet() throws IOException {
		URL targetServer = new URL("http://www.google.com/");
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createControl().createMock(HttpServletResponse.class); 
		HttpClient client = EasyMock.createMock(HttpClient.class);
		ServletOutputStream servletOutputStream = EasyMock.createMock(ServletOutputStream.class);
		EasyMock.expect(response.getOutputStream()).andReturn(servletOutputStream).anyTimes();
		EasyMock.replay(response);
		
		HttpGetRequestHandler httpGetRequestHandler = 
			new HttpGetRequestHandler(request, response, targetServer, client);
		
		httpGetRequestHandler.execute();
		
		EasyMock.verify(response);
	}
	
	/**
	 * Test GET request to target server failed. Proxy should return error 500
	 * @throws IOException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetTargetServerError() throws IOException {
		URL targetServer = new URL("http://unknownhost.example.com/");
		
		// Client
		HttpClient client = EasyMock.createMock(HttpClient.class);
		
		// request
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServerName()).andReturn("localhost").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(80).anyTimes();
		/*
		 *  The servlet used to process this request is matched using the "/*" pattern (see javadoc of HttpServletRequest#getServletPath())
		 */
		EasyMock.expect(request.getServletPath()).andReturn("").anyTimes();
		// Default (root) context
		EasyMock.expect(request.getContextPath()).andReturn("").anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		StringBuffer reqURL = new StringBuffer("http://localhost/");
		EasyMock.expect(request.getRequestURL()).andReturn(reqURL).anyTimes();
		// request headers
		String[] headers = {"Host", "User-Agent"};
		EasyMock.expect(request.getHeaderNames()).andReturn(Collections.enumeration(Arrays.asList(headers))).anyTimes();
		EasyMock.expect(request.getHeaders("Host")).andReturn(Collections.enumeration(Collections.singleton("localhost")));
		EasyMock.expect(request.getHeaders("User-Agent")).andReturn(Collections.enumeration(Collections.singleton("My HttpClient/1.0")));
		EasyMock.replay(request);
		
		// response
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
		EasyMock.expect(response.getOutputStream()).andReturn(os);
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		EasyMock.expectLastCall();
		EasyMock.replay(response);
		
		// Execute client request		
		HttpGetRequestHandler httpGetRequestHandler = 
			new HttpGetRequestHandler(request, response, targetServer, client);
		httpGetRequestHandler.execute();
		EasyMock.verify(response);
	}

}
