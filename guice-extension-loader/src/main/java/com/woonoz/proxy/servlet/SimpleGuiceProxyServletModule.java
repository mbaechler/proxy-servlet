package com.woonoz.proxy.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class SimpleGuiceProxyServletModule extends AbstractModule {

	private final Iterable<Class<? extends HeadersFilter>> clientHeadersFilters;
	private final Iterable<Class<? extends HeadersFilter>> serverHeadersFilters;
	private final ProxyServletConfig proxyServletConfig;

	
	public SimpleGuiceProxyServletModule(ProxyServletConfig proxyServletConfig, 
			Iterable<Class<? extends HeadersFilter>> clientHeadersFilters,
			Iterable<Class<? extends HeadersFilter>> serverHeadersFilters) {
		super();
		this.proxyServletConfig = proxyServletConfig;
		this.clientHeadersFilters = clientHeadersFilters;
		this.serverHeadersFilters = serverHeadersFilters;
	}
	
	@Override
	protected void configure() {
		install(new ProxyServletModule());
		defineProxyTarget();
		defineClientHeadersFilters();
		defineServerHeadersFilters();
	}

	private void defineProxyTarget() {
		bind(ProxyServletConfig.class).toInstance(proxyServletConfig);
	}

	private void defineClientHeadersFilters() {
		Multibinder<HeadersFilter> clientHeadersFiltersBinder = newNamedSetBinder("clientFilters");
		setHeadersFiltersBinding(clientHeadersFiltersBinder, clientHeadersFilters);
	}
	
	private void defineServerHeadersFilters() {
		Multibinder<HeadersFilter> serverHeadersFiltersBinder =  newNamedSetBinder("serverFilters");
		setHeadersFiltersBinding(serverHeadersFiltersBinder, serverHeadersFilters);
	}

	private void setHeadersFiltersBinding(Multibinder<HeadersFilter> binder,
			Iterable<Class<? extends HeadersFilter>> headersFilters) {
		for (Class<? extends HeadersFilter> filter: headersFilters) {
			binder.addBinding().to(filter);
		}
	}

	private Multibinder<HeadersFilter> newNamedSetBinder(String name) {
		return Multibinder.newSetBinder(binder(), HeadersFilter.class, Names.named(name));
	}
}
