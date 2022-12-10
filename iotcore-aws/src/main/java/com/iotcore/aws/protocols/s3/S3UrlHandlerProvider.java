package com.iotcore.aws.protocols.s3;

import java.net.URLStreamHandler;

public class S3UrlHandlerProvider extends java.net.spi.URLStreamHandlerProvider{

    
    /**
     * 
     */
    public S3UrlHandlerProvider() {
    	super();
    }
    
    /**
     *
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
    	if (Handler.S3_PROTOCOL.equals(protocol)) {
			return new Handler();
		} 
    	else {
			return null;
		}
    }
    
}