/*
 * Copyright (c) 2011, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.woonoz.proxy.servlet;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletConfig;

public class ProxyServletConfig
{

    private static final int DEFAULT_MAX_CONNECTIONS = 200;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;
    private static final String DEFAULT_REMOTE_USER_HEADER = null;
    private final URL targetUrl;
    private final int maxConnections;
    private final int connectionTimeout;
    private final int socketTimeout;
    private final String remoteUserHeader;

    public ProxyServletConfig( ServletConfig config )
            throws MalformedURLException, NumberFormatException
    {
        String targetUrlParam = config.getInitParameter( "targetUrl" );
        if ( targetUrlParam == null ) {
            targetUrlParam = config.getInitParameter( "target-url" );
        }
        targetUrl = targetUrlParam != null ? new URL( targetUrlParam ) : null;

        String maxCoParam = config.getInitParameter( "max-connections" );
        maxConnections = maxCoParam != null ? Integer.valueOf( maxCoParam ) : DEFAULT_MAX_CONNECTIONS;

        String coTimeoutParam = config.getInitParameter( "connection-timeout" );
        connectionTimeout = coTimeoutParam != null ? Integer.valueOf( coTimeoutParam ) : DEFAULT_CONNECTION_TIMEOUT;

        String soTimeoutParam = config.getInitParameter( "socket-timeout" );
        socketTimeout = soTimeoutParam != null ? Integer.valueOf( soTimeoutParam ) : DEFAULT_SOCKET_TIMEOUT;
        
        remoteUserHeader = config.getInitParameter( "remote-user-header" );
    }

    public ProxyServletConfig( URL targetUrl )
    {
        this( targetUrl, DEFAULT_MAX_CONNECTIONS, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, DEFAULT_REMOTE_USER_HEADER );
    }

    public ProxyServletConfig( URL targetUrl, int maxConnections )
    {
        this( targetUrl, maxConnections, DEFAULT_MAX_CONNECTIONS, DEFAULT_SOCKET_TIMEOUT, DEFAULT_REMOTE_USER_HEADER );
    }

    public ProxyServletConfig( URL targetUrl, int maxConnections, int connectionTimeout, int socketTimeout )
    {
        this( targetUrl, maxConnections, connectionTimeout, socketTimeout, DEFAULT_REMOTE_USER_HEADER );
    }
    
    public ProxyServletConfig( URL targetUrl, int maxConnections, int connectionTimeout, int socketTimeout, String remoteUserHeader )
    {
        this.targetUrl = targetUrl;
        this.maxConnections = maxConnections;
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
        this.remoteUserHeader = remoteUserHeader;
    }

    public URL getTargetUrl()
    {
        return targetUrl;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public int getSocketTimeout()
    {
        return socketTimeout;
    }

    public String getRemoteUserHeader() 
    {
        return remoteUserHeader;
    }
}
