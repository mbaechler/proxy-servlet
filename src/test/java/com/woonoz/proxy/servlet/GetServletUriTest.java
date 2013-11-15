package com.woonoz.proxy.servlet;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class GetServletUriTest
{
	private static final String REQUEST_URI_ROOT = "/";
	private static final String ENCODED_ONE_PATH_FRAGMENT = "/a%2Eb%2Dc";
	
	private static final String ENCODED_TWO_PATH_FRAGMENTS_WITH_ENDING_SLASH = "/a%2Eb%2Dc/";
	private static final String ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH = "/a%2Eb%2Dc/d%20e";
	
	private static final String DECODED_ONE_PATH_FRAGMENT = "/a.b-c";
	private static final String DECODED_TWO_PATH_FRAGMENTS = "/a.b-c/d e";
	
	
	
	private final HttpServletRequest request;
	private final String expectedResult;

	@Parameters
	/**
	 * Test parameters
	 * @return list of parameter sets (context path, servlet path, request URI, expected result)
	 */
	public static Collection<Object[]> data()
	{
		Object[][] data = new Object[][] { 
				{"", "", REQUEST_URI_ROOT, "" },
				{"", "", ENCODED_ONE_PATH_FRAGMENT, "" },
				{"", "", ENCODED_TWO_PATH_FRAGMENTS_WITH_ENDING_SLASH, "" },
				{"", "", ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH, "" },
				
				{"", DECODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT },
				{"", DECODED_ONE_PATH_FRAGMENT, ENCODED_TWO_PATH_FRAGMENTS_WITH_ENDING_SLASH, ENCODED_ONE_PATH_FRAGMENT },
				{"", DECODED_ONE_PATH_FRAGMENT, ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH, ENCODED_ONE_PATH_FRAGMENT },
				
				{"", DECODED_TWO_PATH_FRAGMENTS, ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH, ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH },
				//
				{ENCODED_ONE_PATH_FRAGMENT, "", ENCODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT },
				{ENCODED_ONE_PATH_FRAGMENT, "", ENCODED_TWO_PATH_FRAGMENTS_WITH_ENDING_SLASH, ENCODED_ONE_PATH_FRAGMENT },
				{ENCODED_ONE_PATH_FRAGMENT, "", ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH, ENCODED_ONE_PATH_FRAGMENT },
				
				{ENCODED_ONE_PATH_FRAGMENT, DECODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT+ENCODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT+ENCODED_ONE_PATH_FRAGMENT },
				{ENCODED_ONE_PATH_FRAGMENT, DECODED_ONE_PATH_FRAGMENT, ENCODED_ONE_PATH_FRAGMENT+ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH , ENCODED_ONE_PATH_FRAGMENT+ENCODED_ONE_PATH_FRAGMENT },
				
				{ENCODED_ONE_PATH_FRAGMENT, DECODED_TWO_PATH_FRAGMENTS, ENCODED_ONE_PATH_FRAGMENT+ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH, ENCODED_ONE_PATH_FRAGMENT+ENCODED_TWO_PATH_FRAGMENTS_WITHOUT_ENDING_SLASH },
		};
		return Arrays.asList(data);
	}

	public GetServletUriTest(String contextPath, String servletPath, String requestURI, String expected)
	{
		request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getServletPath()).andReturn(servletPath).anyTimes();
		EasyMock.expect(request.getRequestURI()).andReturn(requestURI).anyTimes();
		EasyMock.expect(request.getContextPath()).andReturn(contextPath).anyTimes();
		EasyMock.expect(request.getQueryString()).andReturn(null).anyTimes();
		EasyMock.expect(request.getServerName()).andReturn("example.com").anyTimes();
		EasyMock.expect(request.getServerPort()).andReturn(80).anyTimes();
		EasyMock.replay(request);
		
		this.expectedResult = expected;
	}

	@Test
	public void pushTest()
	{
		 String result = UrlRewriterImpl.getEncodedServletURI(request);
		 Assert.assertEquals("Parameters: getContextPath()=" + request.getContextPath() + " | getServletPath()=" + request.getServletPath() + " | getRequestURI()=" + request.getRequestURI(), expectedResult, result);
		 EasyMock.verify(request);
	}

}
