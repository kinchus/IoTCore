/**
 * 
 */
package com.iotcore.aws.services;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.exception.AwsException;
import com.iotcore.core.model.exception.ObjectNotFoundException;
import com.iotcore.core.model.exception.ServiceException;
import com.iotcore.core.model.message.MqttService;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.GetThingShadowResponse;
import software.amazon.awssdk.services.iotdataplane.model.PublishRequest;
import software.amazon.awssdk.services.iotdataplane.model.PublishResponse;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowRequest;
import software.amazon.awssdk.services.iotdataplane.model.UpdateThingShadowResponse;

/**
 * @author jmgarcia
 *
 */
public class AwsIoTDataService extends AwsBase<IotDataPlaneClient> implements MqttService {

	private static final long serialVersionUID = -878790781266798286L;
	
	private static final Logger LOG = LoggerFactory.getLogger(AwsIoTDataService.class);
	
	

	@Override
	public IotDataPlaneClient create() {
		
		String region = getConfig().getRegion();
		String endpoint = getConfig().getProperty(AwsConfig.AWS_IoT_ENDPOINT);
		
		LOG.trace("Creating IoTData client for {} ({})", region, endpoint);
		
		return IotDataPlaneClient.builder()
				.region(Region.of(region))
				.endpointOverride( URI.create(endpoint) )
				.build();
	}

	
	/**
	 * 
	 */
	public AwsIoTDataService() {
		super();
	}
	

	/**
	 * 
	 *
	 * @param deviceFullName
	 * @return
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public String requestDeviceShadow(String deviceFullName) throws ObjectNotFoundException, ServiceException {
		String ret = "";

		GetThingShadowResponse result = null;
		try {
			LOG.debug("Requesting thing {} shadow", deviceFullName);
			final GetThingShadowRequest request = GetThingShadowRequest.builder().
					thingName(deviceFullName)
					.build();
			
			result = client().getThingShadow(request);
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(deviceFullName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("GetThingShadow", e);
		}

		if (responseOk(result)) {
			final byte[] resChars = result.payload().asByteArray();
			ret = new String(resChars);
		}
		return ret;
	}


	/**
	 * 
	 *
	 * @param deviceFullName
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws ServiceException
	 */
	public String updateDeviceShadow(String deviceFullName, String data) throws ObjectNotFoundException, ServiceException {
		String ret = "";

		UpdateThingShadowResponse result = null;
		try {
			final UpdateThingShadowRequest request = UpdateThingShadowRequest.builder()
					.thingName(deviceFullName)
					.payload(SdkBytes.fromByteArray(data.getBytes()))
					.build();
			
			result = client().updateThingShadow(request);
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(deviceFullName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("UpdateThingShadow", e);
		}

		if (responseOk(result)) {
			final byte[] resChars = result.payload().asByteArray();
			ret = new String(resChars);

		}
		return ret;
	}

	/**
	 * 
	 *
	 * @param deviceFullName
	 * @param state
	 * @return
	 * @throws AwsException
	 */
	public String updateDeviceShadowState(String deviceFullName, String state) throws ServiceException {
		final String status = String.format("{ \"state\" : { \"desired\" : %s } }", state);
		return updateDeviceShadow(deviceFullName, status);
	}

	/**
	 * 
	 *
	 * @param topic
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean publish(String topic, String message) throws ServiceException {

		final PublishRequest request = PublishRequest.builder()
				.payload(SdkBytes.fromUtf8String(message))
				.qos(1)
				.topic(topic)
				.build();
		
		final PublishResponse res = client().publish(request);
		if (responseOk(res)) {
			return true;
		}
		return false;
	}



}
