/**
 * 
 */
package com.iotcore.core.util.net;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Kincho
 *
 */
public class SSLBase {
	
	// Create a trust manager that does not validate certificate chains
	/** trustAllCerts */
	public static	TrustManager[] trustAllCerts = new TrustManager[] { 
		new X509TrustManager() {
			
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
			
		}
	};

	/** hostnameVerifier */
	public static	HostnameVerifier hostnameVerifier = new HostnameVerifier() {
		
		@Override
		public boolean verify(String string, SSLSession ssls) {
			return true;
		}
		
	};

	/**
	 * 
	 */
	public SSLBase() {
	}
	
	
	/**
	 * @return
	 */
	public static SSLContext  trustAllCerts() {
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sc;
	}
	
	/**
	 * @param sc
	 * @return
	 */
	public static SSLContext  trustAllCerts(SSLContext sc) {
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sc;
	}

}
