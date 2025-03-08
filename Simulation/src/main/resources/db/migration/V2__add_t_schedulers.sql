CREATE TABLE schedulers
(
    name            VARCHAR(255) NOT NULL,
    type            VARCHAR(255),
    enabled         BOOLEAN,
    cron_expression VARCHAR(255),
    fixed_delay     BIGINT,
    start_time      TIMESTAMP WITHOUT TIME ZONE,
    status          VARCHAR(255),
    last_run_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_schedulers PRIMARY KEY (name)
);