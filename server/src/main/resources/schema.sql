CREATE EXTENSION IF NOT EXISTS POSTGIS;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS car;
DROP INDEX IF EXISTS users_coordinate_idx;
DROP INDEX IF EXISTS car_coordinate_idx;

CREATE UNLOGGED TABLE users (
  id VARCHAR(255) NOT NULL,
  x VARCHAR(255),
  y VARCHAR(255),
  coordinate GEOMETRY,
  PRIMARY KEY (id)
);

CREATE UNLOGGED TABLE car (
  id VARCHAR(255) NOT NULL,
  x VARCHAR(255),
  y VARCHAR(255),
  coordinate GEOMETRY,
  PRIMARY KEY (id)
);


CREATE INDEX users_coordinate_idx ON users USING GIST (coordinate);
CREATE INDEX car_coordinate_idx ON car USING GIST (coordinate);
