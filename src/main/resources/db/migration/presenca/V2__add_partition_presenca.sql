CREATE OR REPLACE FUNCTION create_presenca_partition(start_date date)
RETURNS void AS $$
DECLARE
end_date date;
    partition_name text;
BEGIN

    end_date := start_date + interval '1 month';

    partition_name := 'presenca_' || to_char(start_date, 'YYYY_MM');

EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF presenca
         FOR VALUES FROM (%L) TO (%L)',
        partition_name,
        start_date,
        end_date
        );

END;
$$ LANGUAGE plpgsql;