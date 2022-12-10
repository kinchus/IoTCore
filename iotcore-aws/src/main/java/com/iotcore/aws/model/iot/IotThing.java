/**
 * 
 */
package com.iotcore.aws.model.iot;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jmgarcia
 *
 */
public class IotThing {

	public static final String CERT_EXT = "_cert.pem";
	/** Extension for private key files (PEM format) */
	public static final String PRIVKEY_EXT = "_priv.pem";
	/** Extension for public key files (PEM format) */
	public static final String PUBKEY_EXT = "_pub.pem";

	public static final String EUI_ATTR = "eui";
	public static final String OWNER_ATTR = "owner";
	public static final String SUBSCRIPTION_ATTR = "subscription";

	private static final String CERT_PATH = "devices/%s/certs/%s";

	/**
	 * @param dev
	 * @return
	 */
	public static String getCertificatePath(Thing thing) {
		return String.format(CERT_PATH, thing.getName(), thing.getCertificateId());
	}


	public static Map<String, String> getThingAttributes(String deviceEui, String appSubscription, String ownerId) {
		final Map<String, String> ret = new HashMap<String, String>();
		if (appSubscription != null) {
			ret.put(SUBSCRIPTION_ATTR, appSubscription);
		}
		if (deviceEui != null) {
			ret.put(EUI_ATTR, deviceEui);
		}
		if (ownerId != null) {
			ret.put(OWNER_ATTR, ownerId);
		}
		return ret;
	}

}
