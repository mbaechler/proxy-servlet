package com.woonoz.proxy.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;

import com.woonoz.proxy.servlet.base.AbstractHttpRequestCommand;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.HttpRequestHandler;
import com.woonoz.proxy.servlet.http.HttpRequestHandler.Factory;
import com.woonoz.proxy.servlet.http.verb.HttpDeleteRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpGetRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpHeadRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpOptionsRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpPostRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpPutRequestHandler;
import com.woonoz.proxy.servlet.http.verb.HttpTraceRequestHandler;

public abstract class AbstractProxyServlet extends HttpServlet {

	private static final int HTTP_DEFAULT_PORT = 80;
	private URL targetServer;
	private DefaultHttpClient client;
	private Factory requestHandlerFactory;

	public AbstractProxyServlet() {
		super();
	}

	public void init(ProxyServletConfig config, HttpRequestHandler.Factory requestHandlerFactory) {
		this.requestHandlerFactory = requestHandlerFactory;
		targetServer = config.getTargetUrl();
	    if (targetServer != null) {
	        SchemeRegistry schemeRegistry = new SchemeRegistry();
	        schemeRegistry.register(new Scheme(targetServer.getProtocol(), getPortOrDefault(targetServer.getPort()), PlainSocketFactory.getSocketFactory()));
	        BasicHttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout( httpParams, config.getConnectionTimeout() );
	        HttpConnectionParams.setSoTimeout( httpParams, config.getSocketTimeout() );
	        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
	        cm.setDefaultMaxPerRoute( config.getMaxConnections() );
	        cm.setMaxTotal( config.getMaxConnections() );
	        client = new DefaultHttpClient(cm, httpParams);
	        client.removeResponseInterceptorByClass(ResponseProcessCookies.class);
	        client.removeRequestInterceptorByClass(RequestAddCookies.class);
	        
	        final String remoteUserHeader = config.getRemoteUserHeader(); 
	        if (null != remoteUserHeader) {
	            client.addRequestInterceptor(new HttpRequestInterceptor() {
	                
	                @Override
	                public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
	                    request.removeHeaders(remoteUserHeader);
	                    AbstractHttpRequestCommand handler;
	                    if (context != null && (handler = (AbstractHttpRequestCommand) context.getAttribute(AbstractHttpRequestCommand.class.getName())) != null) {
	                        String remoteUser = handler.getRequest().getRemoteUser();
	                        if (remoteUser != null) {
	                            request.addHeader(remoteUserHeader, remoteUser);
	                        }
	                    }
	                }
	            });
	        }
	    }
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpGetRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpDeleteRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpHeadRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpOptionsRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpTraceRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpPostRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		HttpRequestHandler httpRequestHandler = requestHandlerFactory.create(request, targetServer);
		new HttpPutRequestHandler(httpRequestHandler, request, response, client).execute();
	}

	private int getPortOrDefault(int port) {
		if (port == -1) {
			return HTTP_DEFAULT_PORT;
		} else {
			return port;
		}
	}

}