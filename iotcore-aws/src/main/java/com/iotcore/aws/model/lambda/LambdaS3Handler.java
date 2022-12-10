package com.iotcore.aws.model.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;


/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public abstract class LambdaS3Handler implements RequestHandler<S3Event, String> {

	private static final Logger LOG = LoggerFactory.getLogger(LambdaS3Handler.class);

	/**
	 * @param record
	 * @throws Exception
	 */
	public void handleObjectCreated(S3EventNotificationRecord record) throws Exception {

	}

	/**
	 * @param record
	 * @throws Exception
	 */
	public void handleObjectRemoved(S3EventNotificationRecord record) throws Exception {

	}

	/**
	 * @param record
	 * @throws Exception
	 */
	public void handleObjectRestore(S3EventNotificationRecord record) throws Exception {

	}

	/**
	 * @param record
	 * @throws Exception
	 */
	public void handleReplication(S3EventNotificationRecord record) throws Exception {

	}

	/**
	 * @param s3Event
	 * @param context
	 * @return
	 * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object,
	 *      com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public String handleRequest(S3Event s3Event, Context context) {

		if (context != null) {
			LOG.trace("Request {} processing STARTED", context.getAwsRequestId());
		}

		for (final S3EventNotificationRecord record : s3Event.getRecords()) {

			if (LOG.isTraceEnabled()) {
				LOG.trace("Request S3 Event {} {}", record.getEventName(), record.getEventSource());
			}

			try {
				switch (record.getEventNameAsEnum()) {
				case ObjectCreated:
				case ObjectCreatedByCompleteMultipartUpload:
				case ObjectCreatedByCopy:
				case ObjectCreatedByPost:
				case ObjectCreatedByPut:
					handleObjectCreated(record);
					break;
				case ObjectRemoved:
				case ObjectRemovedDelete:
				case ObjectRemovedDeleteMarkerCreated:
					handleObjectRemoved(record);
					break;
				case ObjectRestoreCompleted:
				case ObjectRestorePost:
					handleObjectRestore(record);
					break;
				case Replication:
				case ReplicationOperationFailed:
				case ReplicationOperationMissedThreshold:
				case ReplicationOperationNotTracked:
				case ReplicationOperationReplicatedAfterThreshold:
					handleReplication(record);
					break;
				default:
					break;

				}

			} catch (final Exception e) {
				e.printStackTrace();
				return "ERROR " + e.getMessage();
			}

		}

		if (context != null) {
			LOG.debug("Request {} processing END", context.getAwsRequestId());
		}

		return "OK";
	}

}
