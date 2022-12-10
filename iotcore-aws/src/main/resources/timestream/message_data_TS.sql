--
-- Get message metrics, with filled empty values in timeseries data for each device row.
--
WITH
	binned_timeseries AS (
		SELECT 
        	deviceEui,
        	time as binned_time, 
      		num_msgs AS binned_num_msgs,
      		rx_data AS binned_rx_data,
      		avg_data AS binned_avg_data,
      		meter_reading AS binned_meter_reading,
      		image_data AS binned_image_data
		FROM "IoTCloud"."%d.MessageStats" 
		WHERE  
			time between ago(%dd) and now() 
			AND deviceEui IN '%s'
		GROUP BY deviceEui, time, rx_data, avg_data, num_msgs, meter_reading, image_data
      	ORDER BY time DESC
	),
	interpolated_data AS (
		SELECT 
			deviceEui,
			INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_rx_data),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 
              0) AS rx_data,
            INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_avg_data),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 
              0) AS avg_data,
      		INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_num_msgs),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 
              0) AS num_msgs,
			INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_meter_reading),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 
              0) AS meter_reading,
			INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_image_data),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 
              0) AS image_data
		FROM binned_timeseries
      	GROUP BY deviceEui
		ORDER BY deviceEui
	)
SELECT *
FROM interpolated_data
