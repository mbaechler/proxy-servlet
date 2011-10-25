package com.woonoz.proxy.servlet.http.verb;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import com.woonoz.proxy.servlet.base.HttpRequestHandlerImpl;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpGetRequestHandler;

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
		
		HttpRequestHandler handler = 
				new HttpRequestHandlerImpl.HttpRequestHandlerFactoryImpl().create(request, targetServer);
		HttpGetRequestHandler httpGetRequestHandler = 
			new HttpGetRequestHandler(handler, request, response, client);
		
		httpGetRequestHandler.execute();
		
		EasyMock.verify(response);
	}

}
