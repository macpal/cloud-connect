-- Create sequence for primary key auto-increment
CREATE SEQUENCE user_sequence START WITH 1 INCREMENT BY 1;

-- Create users table
CREATE TABLE users (
                       id BIGINT PRIMARY KEY DEFAULT NEXTVAL('user_sequence'),
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       role VARCHAR(50) NOT NULL
);
