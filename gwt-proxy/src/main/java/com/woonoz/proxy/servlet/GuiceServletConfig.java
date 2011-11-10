package com.woonoz.proxy.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.woonoz.proxy.servlet.config.ProxyServletConfig;
import com.woonoz.proxy.servlet.http.header.HeadersFilter;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ProxyServletModule(),
				new Module() {
					
					@Override
					public void configure(Binder binder) {
						try {
							binder.bind(ProxyServletConfig.class).toInstance(
									new ProxyServletConfig(new URL("http://www.google.fr")));
						} catch (MalformedURLException e) {
							binder.addError(e);
						}
					}
				},
				new AbstractModule() {
					@Override
					protected void configure() {
						Multibinder<HeadersFilter> headersFilters = Multibinder.newSetBinder(binder(), HeadersFilter.class);
						headersFilters.addBinding().to(UserAgentRemover.class);
					}
				},
				new ServletModule() {
			@Override
			protected void configureServlets() {
				super.configureServlets();
				serve("/happy/*").with(GuiceProxyServlet.class);
			}
		});
	}
	
}
