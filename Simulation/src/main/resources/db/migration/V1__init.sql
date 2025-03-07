CREATE TABLE spaces
(

    id          SERIAL PRIMARY KEY,

    name        VARCHAR(255) NOT NULL,

    create_date TIMESTAMP WITH TIME ZONE DEFAULT NOW()

);

CREATE TABLE reservoirs
(

    id       SERIAL PRIMARY KEY,

    space_id INT              NOT NULL, -- Связь с пространством

    pressure DOUBLE PRECISION NOT NULL CHECK (pressure >= 0),

    level    DOUBLE PRECISION NOT NULL CHECK (level >= 0),

    area     DOUBLE PRECISION NOT NULL CHECK (area > 0),

    FOREIGN KEY (space_id) REFERENCES spaces (id) ON DELETE CASCADE

);

CREATE TABLE pipes
(

    id        SERIAL PRIMARY KEY,

    source_id INT              NOT NULL,

    target_id INT              NOT NULL,

    diameter  DOUBLE PRECISION NOT NULL CHECK (diameter > 0),

    CONSTRAINT fk_source FOREIGN KEY (source_id) REFERENCES reservoirs (id) ON DELETE CASCADE,

    CONSTRAINT fk_target FOREIGN KEY (target_id) REFERENCES reservoirs (id) ON DELETE CASCADE,

    CONSTRAINT chk_source_not_target CHECK (source_id <> target_id)

);

CREATE TABLE pressure_history
(
    time  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    id_1  DOUBLE PRECISION,
    id_2  DOUBLE PRECISION,
    id_3  DOUBLE PRECISION,
    id_4  DOUBLE PRECISION,
    id_5  DOUBLE PRECISION,
    id_6  DOUBLE PRECISION,
    id_7  DOUBLE PRECISION,
    id_8  DOUBLE PRECISION,
    id_9  DOUBLE PRECISION,
    id_10 DOUBLE PRECISION
);

-- Преобразуем таблицу в гипертаблицу TimescaleDB
SELECT create_hypertable('pressure_history', 'time');