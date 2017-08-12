< --- !UP>

CREATE TABLE userData (
id   serial not null primary key,
firstname varchar(50) not null,
middlename varchar(50),
lastname varchar(50) not null,
username varchar(50) not null,
password varchar(50) not null,
mobilenumber int not null,
gender varchar(8) not null,
age int not null
);

< --- !DOWN>

DROP TABLE userData;