/**
 * 
 */
package com.iotcore.mongo;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.util.BaseConfig;
import com.iotcore.core.util.StringUtil;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class MongoConfig extends BaseConfig {
	
	public static final String URI_PATTERN = "mongodb://%s%s/%s?authSource=%s";
	public static final String URI_PATTERN_0 = "mongodb://%s/%s";

	
	/**
	 * Configuration keys
	 *
	 */
	public enum ConfigKey implements BaseConfig.ConfigKey {
		
		/** JNDI_PROPERTY */
		JNDI_PROPERTY("mongo.jndi", String.class),
		URI_PROPERTY("mongo.uri", String.class),
		/** DBHOST */
		DBHOST("mongo.host", String.class),
		/** DBPORT */
		DBPORT("mongo.port", Integer.class),
		/** DBNAME */
		DBNAME("mongo.dbname", String.class),
		/** DBAUTH_PROPERTY */
		DBAUTH_PROPERTY("mongo.dbauth", String.class),
		/** DBUSER_PROPERTY */
		DBUSER_PROPERTY("mongo.username", String.class),
		/** DBPASS_PROPERTY */
		DBPASS_PROPERTY("mongo.password", String.class),
		/** DAO_PACKAGES: packages to scan for DAOs */
		DOMAIN_PACKAGES("mongo.dao.entities", String.class),
		/** DAO_PACKAGES: packages to scan for DAOs */
		DAO_PACKAGES("mongo.dao.packages", String.class),
		/** DAO_CLASSES: DAO classes to load */
		DAO_CLASSES("mongo.dao.classes", String.class);
		
		private String key;
		private Class<?> type;
		
		private ConfigKey(String key, Class<?> type) {
			this.key = key;
			this.type = type;
		}

		/**
		 * @return the key
		 */
		@Override
		public String getKey() {
			return key;
		}
		
		/**
		 * @return the key
		 */
		@Override
		public Class<?> getType() {
			return type;
		}
	}

	
	private static final Logger LOG = LoggerFactory.getLogger(MongoConfig.class);
	
	
	private static String 	DBHOST_DEFAULT = "localhost";
	private static Integer 	DBPORT_DEFAULT = 27017;
	private static String 	DBNAME_DEFAULT = "admin";
	
	private String jndiRes	= null;
	private String dbUri	= null;
	private String dbAuth 	= null;
	private String dbName 	= null;
	private String dbHost 	= null;
	private Integer dbPort 	= null;
	private String username = null;
	private String password = null;
	private String[] domainPackages;
	
	private MongoCredential 	credential 	= null;
	// private MongoClientOptions 	options 	= null;
	    
    protected boolean fileParsed = false;
    

	/**
	 * 
	 */
	public MongoConfig() {
		super();
	}

	/**
	 * @param configFile
	 */
	public MongoConfig(String configFile) {
		super(configFile);
	}


	/**
	 * Parse the configuration file and set the connection properties
	 * 
	 */
	public void parseConfiguration() {

		if (jndiRes == null) {
			jndiRes =  getProperty(ConfigKey.JNDI_PROPERTY);
		}
		if (dbUri == null) {
			dbUri =  getProperty(ConfigKey.URI_PROPERTY);
		}
		if (username == null) {
			username = getProperty(ConfigKey.DBUSER_PROPERTY);
		}
		if (password == null) {
			password = getProperty(ConfigKey.DBPASS_PROPERTY);
		}
		if (dbHost== null) {
			dbHost =  getProperty(ConfigKey.DBHOST, DBHOST_DEFAULT);
		}
		if (dbAuth == null) {
			dbAuth =  getProperty(ConfigKey.DBAUTH_PROPERTY, DBNAME_DEFAULT);
		}
		if (dbName == null) {
			dbName =  getProperty(ConfigKey.DBNAME, DBNAME_DEFAULT);
		}
		if (dbPort == null) {
			dbPort = getProperty(ConfigKey.DBPORT, DBPORT_DEFAULT);
		}
		
		
		if ((dbUri == null) && (jndiRes == null)) {
			
			StringBuffer userCredsSection = new StringBuffer();
			if (username != null) {
				userCredsSection.append(username);
				if (password != null) {
					userCredsSection.append(":" + password);
				}
				userCredsSection.append('@');
			}
			else {
			}
			
			
			StringBuffer hostPortSection = new StringBuffer(dbHost);
			if (dbPort != null) {
				hostPortSection.append(":" + dbPort);
			}
			
			
			StringBuffer auth = new StringBuffer();
			if (dbAuth != null) {
				auth.append(dbAuth);
			}
			
			
			if (userCredsSection.isEmpty()) {
				dbUri = String.format(URI_PATTERN_0, hostPortSection.toString(), dbName.toString(), auth.toString());
			}
			else {
				dbUri = String.format(URI_PATTERN, userCredsSection.toString(), hostPortSection.toString(), dbName.toString(), auth.toString());
			}
			LOG.trace("Connection URI set to {}", dbUri);
		}

		//options = MongoClientOptions.builder().build();
		
	}


	/**
	 * @return the dbUri
	 */
	public String getDbUri() {
		if ((dbUri == null)) {
			
			StringBuffer userCredsSection = new StringBuffer();
			if (getUsername() != null) {
				userCredsSection.append(getUsername());
				if (getPassword() != null) {
					userCredsSection.append(":" + getPassword());
				}
				userCredsSection.append('@');
			}
			else {
			}
			
			
			StringBuffer hostPortSection = new StringBuffer(getDbHost());
			if (getDbPort() != null) {
				hostPortSection.append(":" + getDbPort());
			}
			
			
			StringBuffer auth = new StringBuffer();
			if (getDbAuth() != null) {
				auth.append(getDbAuth());
			}
			
			
			if (userCredsSection.isEmpty()) {
				dbUri = String.format(URI_PATTERN_0, hostPortSection.toString(), dbName.toString(), auth.toString());
			}
			else {
				dbUri = String.format(URI_PATTERN, userCredsSection.toString(), hostPortSection.toString(), dbName.toString(), auth.toString());
			}
			LOG.trace("Connection URI set to {}", dbUri);
		}

		return dbUri;
	}

	/**
	 * @param dbUri the dbUri to set
	 */
	public void setDbUri(String dbUri) {
		this.dbUri = dbUri;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		if (dbName == null) {
			dbName =  getProperty(ConfigKey.DBNAME, DBNAME_DEFAULT); 
		}
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the dbHost
	 */
	public String getDbHost() {
		if (dbHost == null) {
			dbHost =  getProperty(ConfigKey.DBHOST, DBHOST_DEFAULT);
		}
		return dbHost;
	}

	/**
	 * @param dbHost the dbHost to set
	 */
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	/**
	 * @return the dbPort
	 */
	public Integer getDbPort() {
		if (dbPort == null) {
			dbPort = getProperty(ConfigKey.DBPORT, DBPORT_DEFAULT);
		}
		return dbPort;
	}

	/**
	 * @param dbPort the dbPort to set
	 */
	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		if (username == null) {
			username = getProperty(ConfigKey.DBUSER_PROPERTY);
		}
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		if (password == null) {
			password = getProperty(ConfigKey.DBPASS_PROPERTY);
		}
		return password;
	}
	
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the dbAuth
	 */
	public String getDbAuth() {
		if (dbAuth == null) {
			dbAuth =  getProperty(ConfigKey.DBAUTH_PROPERTY, DBNAME_DEFAULT);
		}
		return dbAuth;
	}

	/**
	 * @param dbAuth the dbAuth to set
	 */
	public void setDbAuth(String dbAuth) {
		this.dbAuth = dbAuth;
	}

	/**
	 * @return the credential
	 */
	public MongoCredential getCredential() {
		if (credential == null) {
			char [] passwdChars = null;
			if (password != null) {
				passwdChars = password.toCharArray();
			}
			credential = MongoCredential.createCredential(getUsername(), dbAuth, passwdChars);
		}
		return credential;
	}

	/**
	 * @param credential the credential to set
	 */	
	public void setCredential(MongoCredential credential) {
		this.credential = credential;
	}

	/**
	 * Instantiates a new MongoDB Client
	 * @return the client
	 * @throws Exception
	 */
	public MongoClient buildClient()  {
		MongoClient ret = null;
		
		if (!StringUtil.isBlank(jndiRes)) {
			try {
				Context initContext = new InitialContext();
				Context envContext  = (Context)initContext.lookup("java:/comp/env");
				LOG.trace("Building MongoDB client from JNDI java:/comp/env/{}",  jndiRes);
				ret = (MongoClient) envContext.lookup(jndiRes);
			} catch (NamingException e) {
				LOG.error("Exception thrown: {}", e.getMessage());
			}
			
		}
		else {
			LOG.trace("Building MongoDB client from URI {}", dbUri);
			ret = MongoClients.create(new ConnectionString(getDbUri()));
		}
		
		return ret;
	}

	public String[] getDomainPackages() {
		return domainPackages;
	}
	public void setDomainPackages(String ... packages) {
		domainPackages = packages;
	}

}

