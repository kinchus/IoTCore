--
-- MSG COUNT per HOUR ALARM
--
WITH
	binned_timeseries AS (
		SELECT 
        	deviceEui,
        	BIN(time, 1h) AS binned_timestamp, 
			COUNT(*) AS binned_num_msgs
		FROM "IoTCloud"."000000000000000000000000.MsgMetrics"
		WHERE measure_name = 'message'
			AND time > ago(15d)
		GROUP BY deviceEui, BIN(time, 1h)
	),
	interpolated_data AS (
		SELECT 
			deviceEui,
			INTERPOLATE_FILL(
            	CREATE_TIME_SERIES(binned_timestamp, binned_num_msgs),
				SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 
				0) AS num_msgs
		FROM binned_timeseries
      	GROUP BY deviceEui
		ORDER BY deviceEui
	)
SELECT deviceEui, time, value AS num_msgs
FROM interpolated_data
CROSS JOIN UNNEST(num_msgs)
WHERE value <= 0 OR value >= 9223372036854775807

