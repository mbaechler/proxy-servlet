package com.woonoz.proxy.servlet;

import java.util.Arrays;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class SimpleGuiceProxyServletModule extends AbstractModule {

	private final Iterable<Class<? extends HeadersFilter>> headersFilters;
	private final ProxyServletConfig proxyServletConfig;

	public SimpleGuiceProxyServletModule(ProxyServletConfig proxyServletConfig, 
			Class<? extends HeadersFilter>... headersFilters) {
		this(proxyServletConfig, Arrays.asList(headersFilters));
	}
	
	public SimpleGuiceProxyServletModule(ProxyServletConfig proxyServletConfig, 
			Iterable<Class<? extends HeadersFilter>> headersFilters) {
		super();
		this.proxyServletConfig = proxyServletConfig;
		this.headersFilters = headersFilters;
	}
	
	@Override
	protected void configure() {
		install(new ProxyServletModule());
		defineProxyTarget();
		defineHeadersFilters();
	}

	private void defineProxyTarget() {
		bind(ProxyServletConfig.class).toInstance(proxyServletConfig);
	}

	private void defineHeadersFilters() {
		Multibinder<HeadersFilter> headersFiltersBinder = 
				Multibinder.newSetBinder(binder(), HeadersFilter.class);
		
		for (Class<? extends HeadersFilter> filter: headersFilters) {
			headersFiltersBinder.addBinding().to(filter);
		}
	}
}
