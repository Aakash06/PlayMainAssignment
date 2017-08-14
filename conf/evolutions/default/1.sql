# --- !Ups

CREATE TABLE if not exists userData (
id      serial not null primary key,
firstname varchar(500) not null,
middlename varchar(500),
lastname varchar(500) not null,
username varchar(500) not null,
password varchar(500) not null,
mobilenumber bigint not null,
gender varchar(8) not null,
age int not null,
isAdmin boolean not null,
isEnable boolean not null
);

# --- !Downs

DROP TABLE userData;

