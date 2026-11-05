CREATE DATABASE IF NOT EXISTS event_management;
USE event_management;

CREATE TABLE IF NOT EXISTS login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS evt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    date VARCHAR(10),
    category VARCHAR(50),
    attendees INT DEFAULT 0,
    waiting INT DEFAULT 0,
    capacity INT
);

-- NEW table: needed for the Attendees and Reports pages
CREATE TABLE IF NOT EXISTS attendee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    priority VARCHAR(10),
    event_id INT,
    checked_in TINYINT DEFAULT 0,
    rating INT DEFAULT 0,
    status VARCHAR(10)
);

-- only if your login table is empty
INSERT INTO login (username, password) VALUES ('admin', 'admin');
