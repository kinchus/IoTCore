
--
-- Alarms query
--
WITH
binned_timeseries AS (
      SELECT 
	deviceEui, 
	BIN(time, 1h) AS binned_timestamp, 
	SUM(size) AS binned_sum_size,
	AVG(size) AS binned_avg_size,
	COUNT(distinct(time)) AS binned_msg_count
      FROM \"" + DATABASE_NAME + "\".\"" +  TABLE_NAME + "\"
      WHERE measure_name = 'message'
          AND holderId = '%s'
          AND time > ago(%dd)
      GROUP BY deviceEui, BIN(time, 1h)
)
, interpolated_data AS (
      SELECT 
        deviceEui
        ,INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_timestamp, binned_sum_size),
              SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 0) AS sum_size
  		,INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_timestamp, binned_avg_size),
              SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 0) AS avg_size
  		,INTERPOLATE_FILL(
              CREATE_TIME_SERIES(binned_timestamp, binned_msg_count),
              SEQUENCE(min(binned_timestamp), max(binned_timestamp), 1h), 0) AS msg_count
	FROM binned_timeseries
	GROUP BY deviceEui
	ORDER BY deviceEui
)
SELECT deviceEui, sum_size, avg_size, msg_count
FROM interpolated_data





