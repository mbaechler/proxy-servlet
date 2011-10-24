package com.woonoz.proxy.servlet.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;

public class ProxyServletConfigXmlFactory {


    public static ProxyServletConfig createConfig(ServletConfig config)
            throws MalformedURLException, NumberFormatException {
    	
        String targetUrlParam = config.getInitParameter( "targetUrl" );
        if ( targetUrlParam == null ) {
            targetUrlParam = config.getInitParameter( "target-url" );
        }
        URL targetUrl = targetUrlParam != null ? new URL( targetUrlParam ) : null;

        String maxCoParam = config.getInitParameter( "max-connections" );
        Integer maxConnections = maxCoParam != null ? Integer.valueOf( maxCoParam ) : null;

        String coTimeoutParam = config.getInitParameter( "connection-timeout" );
        Integer connectionTimeout = coTimeoutParam != null ? Integer.valueOf( coTimeoutParam ) : null;

        String soTimeoutParam = config.getInitParameter( "socket-timeout" );
        Integer socketTimeout = soTimeoutParam != null ? Integer.valueOf( soTimeoutParam ) : null;
        
        String remoteUserHeader = config.getInitParameter( "remote-user-header" );
        
        return new ProxyServletConfig(targetUrl, maxConnections, connectionTimeout, socketTimeout, remoteUserHeader);
    }
	
}
