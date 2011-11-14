package com.woonoz.proxy.servlet;

import java.net.URL;
import java.util.Arrays;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class SimpleGuiceProxyServletModule extends AbstractModule {

	private final URL targetUrl;
	private final Iterable<Class<? extends HeadersFilter>> headersFilters;

	public SimpleGuiceProxyServletModule(URL targetUrl, 
			Class<? extends HeadersFilter>... headersFilters) {
		this(targetUrl, Arrays.asList(headersFilters));
	}
	
	public SimpleGuiceProxyServletModule(URL targetUrl, 
			Iterable<Class<? extends HeadersFilter>> headersFilters) {
		super();
		this.targetUrl = targetUrl;
		this.headersFilters = headersFilters;
	}
	
	@Override
	protected void configure() {
		install(new ProxyServletModule());
		defineProxyTarget();
		defineHeadersFilters();
	}

	private void defineProxyTarget() {
		bind(ProxyServletConfig.class).toInstance(new ProxyServletConfig(targetUrl));
	}

	private void defineHeadersFilters() {
		Multibinder<HeadersFilter> headersFiltersBinder = 
				Multibinder.newSetBinder(binder(), HeadersFilter.class);
		
		for (Class<? extends HeadersFilter> filter: headersFilters) {
			headersFiltersBinder.addBinding().to(filter);
		}
	}
}
