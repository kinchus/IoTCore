--
-- DATA USAGE per HOUR ALARM
--
WITH
binned_timeseries AS (
	SELECT 
		deviceEui,
		time as binned_time,
		rx_data AS binned_rx_data
      FROM "IoTCloud"."000000000000000000000000.MessageStats"
      WHERE 
          time > ago(24h)
      GROUP BY deviceEui, time, rx_data
)
, interpolated_data AS (
    SELECT 
        deviceEui,
        INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_time, binned_rx_data),
              SEQUENCE(min(binned_time), max(binned_time), 1h), 0) AS sum_size
    FROM binned_timeseries
	GROUP BY deviceEui
	ORDER BY deviceEui
)
SELECT deviceEui, time, value
FROM interpolated_data
CROSS JOIN UNNEST(sum_size)
WHERE value > MAX_RXDATA OR value < MIN_RXDATA


