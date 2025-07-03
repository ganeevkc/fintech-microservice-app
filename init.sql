CREATE DATABASE IF NOT EXISTS fintech_business;
use fintech_business;
CREATE USER IF NOT EXISTS 'fintech_user'@'%' IDENTIFIED BY 'fintech_pass';
GRANT ALL PRIVILEGES ON fintech_business.* TO 'fintech_user'@'%';
flush privileges;
show databases;
