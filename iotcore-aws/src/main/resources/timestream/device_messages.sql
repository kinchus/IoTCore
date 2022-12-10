--
-- message_count per hour
--
WITH
	binned_timeseries AS (
		SELECT 
        	deviceEui,
        	BIN(time, 1h) AS binned_timestamp, 
			COUNT(*) AS binned_msg_count
		FROM "IoTCloud"."IoTCloud.DevMessageStats"
		WHERE measure_name = 'message'
			AND deviceEui = '0101010101010101'
			AND time > ago(6d)
		GROUP BY deviceEui, BIN(time, 1h)
	),
	interpolated_data AS (
		SELECT 
			deviceEui,
			INTERPOLATE_FILL(
            	CREATE_TIME_SERIES(binned_timestamp, binned_msg_count),
				SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 
				0) AS msg_count
		FROM binned_timeseries
      	GROUP BY deviceEui
		ORDER BY deviceEui
	)
SELECT sum_size, avg_size, msg_count
FROM interpolated_data


