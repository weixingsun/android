CREATE TABLE IF NOT EXISTS wsn_parking_space_info (
	id     int not null primary key,
	status smallint not null,
	type     smallint(2) NOT NULL,
	lat    decimal(10,7) NOT NULL,
	lng    decimal(10,7) NOT NULL,
	name   varchar(50), --operator
	admin    char(6),
	country  char(6) NOT NULL,
	comment  varchar(50),
	install_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	KEY i_park_lat_lng (lat,lng)
);