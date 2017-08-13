DROP TABLE if exists usertohobby;
DROP TABLE if exists userData;
DROP TABLE if exists hobby;
DROP TABLE if exists Assignment;

CREATE TABLE userData (
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

CREATE TABLE hobby(
id serial primary key,
hobbyText varchar(400) not null
);

INSERT INTO hobby (hobbyText) VALUES ( 'Cricket');
INSERT INTO hobby (hobbyText) VALUES ( 'Football');
INSERT INTO hobby (hobbyText) VALUES ( 'TV');

CREATE TABLE usertohobby(
id serial not null primary key,
userId  int REFERENCES userdata(id),
hobbyid int REFERENCES hobby(id)
);


CREATE table assignment(
id serial not null primary key,
title varchar(500),
description varchar(100000)
);
