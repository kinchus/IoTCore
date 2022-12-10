/**
 * 
 */
package com.iotcore.core.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.util.json.JsonConfiguration;


/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class BackendConfiguration implements Serializable, IBackendConfiguration {

	private static final long serialVersionUID = -3511898267041831206L;
	private static Logger LOG = LoggerFactory.getLogger(BackendConfiguration.class);
	
	
	/** BACKEND_CFG_FILE */
	private static final String CONFIGURATION_FILE = "backend.json";
	
	
	protected static String location = null;
	protected static IBackendConfiguration _instance;
	

	/**
	 * @return
	 */
	public static IBackendConfiguration getInstance() {
		if (_instance == null) {
			setInstance(new BackendConfiguration());
		}
		return _instance;
	}

	/**
	 * @return the location
	 */
	public static String getLocation() {
		return location;
	}
	
	/**
	 * @param instance
	 */
	public static void setInstance(IBackendConfiguration instance) {
		_instance = instance;
	}
	

	protected boolean initialized = false;
	protected JsonConfiguration config = null;
	private URI resourceUrl = null;
	private boolean initializing = false;

	/**
	 * 
	 */
	public BackendConfiguration() {
		config = new JsonConfiguration();
		resourceUrl = getConfigurationResource();
	}


	/**
	 * Read the configuration defined ixn the system var
	 * 
	 */
	@Override
	public synchronized void init() {

		if (initialized) {
			return;
		}

		if (resourceUrl == null) {
			LOG.warn("NULL location. Exiting");
			System.exit(1);
		}

		setInstance(this);
		
		initializing = true;

		LOG.info("Loading backend configuration file: {}", resourceUrl.toString());
		
		try {
			loadConfigurationResource();
		} catch (IOException e) {
			initializing = false;
			LOG.error("{}", e.getMessage());
			e.printStackTrace();
			return;
		}

		String tz = get(IBackendConfiguration.PLATFORM_TIMEZONE);
		if (tz != null) {
			TimeZone.setDefault(TimeZone.getTimeZone(tz));
		}

		initializing = false;
		initialized = true;

		if (LOG.isTraceEnabled() && initialized)
			LOG.trace("Configuration loaded OK. Notify pending threads...");

		synchronized (this) {
			this.notifyAll();
		}

	}


	/**
	 * @return the initialized
	 */
	@Override
	public synchronized boolean waitForInitialization() {
		if (initialized) {
			return true;
		} 
		else if (initializing) {
			LOG.trace("Already initializing. Wait...");
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		
		return initialized;
	}

	/**
	 * @return the bucketName
	 */
	@Override
	public URI getResourceUrl() {
		return resourceUrl;
	}

	/**
	 * @param location the bucketName to set
	 * 
	 */
	@Override
	public void setResourceUrl(URI location) {
		this.resourceUrl = location;
	}


	/**
	 * @throws IOException
	 */
	@Override
	public void update() throws IOException {

		InputStream istream = config.getInputStream();
		byte[] buf = istream.readAllBytes();
		istream.close();
		
		OutputStream ostream = resourceUrl.toURL().openConnection().getOutputStream();
		ostream.write(buf);
		LOG.debug("Writing configuration resource {}: OK ({} bytes)", resourceUrl.toString(), buf.length);
		LOG.trace("File contents:\n{}", new String(buf));
		ostream.close();
	}


	/**
	 * @param <T>
	 * @param field
	 * @return
	 */
	@Override
	public <T> T get(String field) {
		return config.get(field);
	}

	/**
	 * @param <T>
	 * @param field
	 * @param defVal
	 * @return
	 */
	@Override
	public <T> T get(String field, T defVal) {
		return config.get(field, defVal);
	}

	/**
	 * @param field
	 * @param value
	 * 
	 */
	@Override
	public void put(String field, Object value) {
		config.put(field, value);
	}

	/**
	 * @param field
	 * 
	 */
	@Override
	public synchronized void delete(String field) {
		config.delete(field);
	}
	
	/**
	 * 
	 */
	@Override
	public synchronized void clear() {
		config.clear();
	}

	/**
	 * Parse  the Json configuration resource of the associated backend.
	 * 
	 * @throws IOException
	 */
	protected void loadConfigurationResource() throws IOException {
		
		
		InputStream istream = getResourceUrl().toURL().openStream();
		
		if (istream == null) {
			LOG.error("Unable to open resource {}", resourceUrl.toString());
			throw new FileNotFoundException(resourceUrl.toString());
		}
		config.load(istream);
		istream.close();
	}
	

	private static URI getConfigurationResource() {
		URI cfgResource = null;
		
		String configLocation = location;
		if (configLocation == null) {
			configLocation = System.getenv(ENV_BACKEND_LOCATION);
		}
		if (configLocation == null) {
			configLocation = System.getProperty(ENV_BACKEND_LOCATION);
		}
		if (configLocation == null) {
			LOG.trace("Undefined backend location. Environment variable {} not set", ENV_BACKEND_LOCATION);
			cfgResource = getFromLocalFile(CONFIGURATION_FILE);
		} 
		else if (!configLocation.endsWith(".json")) {
			if (configLocation.endsWith("/")) {
				configLocation = configLocation + CONFIGURATION_FILE;
			}
			else {
				configLocation = configLocation + "/" + CONFIGURATION_FILE;
			}
			cfgResource = URI.create(configLocation);
		}
		else {
			cfgResource = URI.create(configLocation);
		}
		return cfgResource;
	}
	
	
	private static URI getFromLocalFile(String fileName) {
		URL configUrl = Thread.currentThread().getContextClassLoader().getResource(fileName);
		if (configUrl != null) {
			LOG.debug("File {} found in classpath", fileName);
			try {
				return configUrl.toURI();
			} catch (URISyntaxException e) {
				return null;
			}
		}

		LOG.trace("File {} not found in classpath", fileName);
		File f = new File(fileName);
		if (f.exists()) {
			LOG.debug("File {} found in current dir", fileName);
			return f.toURI();
		} else {
			LOG.trace("File {} not found in current dir", fileName);
			return null;
		}
	}


}
