--
-- message_count per hour
--
WITH
	binned_timeseries AS (
		SELECT 
        	deviceEui,
        	BIN(time, 1h) AS binned_timestamp, 
			COUNT(*) AS binned_msg_count
		FROM "IoTCloud"."IoTCloud.MessageStats"
		WHERE measure_name = 'message'
			AND holderId = '000000000000000000000000'
			AND time between ago(5d) and now() 
		GROUP BY deviceEui, BIN(time, 1h)
	),
	interpolated_data AS (
		SELECT 
			deviceEui,
			INTERPOLATE_FILL(
            	CREATE_TIME_SERIES(binned_timestamp, binned_msg_count),
				SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 
				0) AS msg_count_timeserie
		FROM binned_timeseries
      	GROUP BY deviceEui
		ORDER BY deviceEui
	)
SELECT deviceEui, t.time AS time, msg_count
FROM interpolated_data
CROSS JOIN unnest(msg_count_timeserie) AS t(time, msg_count)
