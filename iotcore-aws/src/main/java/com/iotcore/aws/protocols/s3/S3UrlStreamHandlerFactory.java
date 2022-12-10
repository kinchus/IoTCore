package com.iotcore.aws.protocols.s3;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * A factory for creating S3URLStreamHandler objects.
 */
public class S3UrlStreamHandlerFactory implements URLStreamHandlerFactory {

	private static final String PROTOCOL_KEY = "java.protocol.handler.pkgs";

	private static boolean factoryRegistered = false;

	private static boolean _useIntrospection = true;

	URLStreamHandlerFactory previous;

	/**
	 * Instantiates a new s 3 URL stream handler factory.
	 */
	public S3UrlStreamHandlerFactory() {
	}

	/**
	 * Instantiates a new s 3 URL stream handler factory.
	 *
	 * @param prev the prev
	 */
	public S3UrlStreamHandlerFactory(URLStreamHandlerFactory prev) {
		previous = prev;
	}

	/**
	 * @param protocol
	 * @return
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (Handler.S3_PROTOCOL.equals(protocol)) {
			return new Handler();
		} else {
			return null;
		}
	}

	public static boolean isRegistered() {
		return factoryRegistered;
	}

	/**
	 * @throws Exception
	 */
	public static void register() throws Exception {
		if (factoryRegistered) {
			return;
		}

		if (_useIntrospection) {
			final Field factoryField = URL.class.getDeclaredField("factory");
			factoryField.setAccessible(true);
			final Field lockField = URL.class.getDeclaredField("streamHandlerLock");
			lockField.setAccessible(true);

			// use same lock as in java.net.URL.setURLStreamHandlerFactory
			synchronized (lockField.get(null)) {
				final URLStreamHandlerFactory urlStreamHandlerFactory = (URLStreamHandlerFactory) factoryField
						.get(null);
				// Reset the value to prevent Error due to a factory already defined
				factoryField.set(null, null);
				URL.setURLStreamHandlerFactory(new S3UrlStreamHandlerFactory(urlStreamHandlerFactory));
			}
		} else {
			String hdlrName = S3UrlStreamHandlerFactory.class.getName();
			String handlers = System.getProperty(PROTOCOL_KEY);
			if (handlers != null) {
				handlers += "|" + S3UrlStreamHandlerFactory.class.getName();
				System.setProperty(PROTOCOL_KEY, handlers);
			} else {
				System.setProperty(PROTOCOL_KEY, hdlrName);
			}
		}
		factoryRegistered = true;
	}

}
