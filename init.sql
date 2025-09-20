CREATE DATABASE IF NOT EXISTS pessoas;
USE pessoas;

CREATE TABLE IF NOT EXISTS person (
    id serial primary key,
    name varchar(100) not null,
    age int not null
);