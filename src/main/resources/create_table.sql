CREATE DATABASE doordash_assessment;
USE doordash_assessment;
CREATE TABLE IF NOT EXISTS phone_number_data (
    id varchar(40) NOT NULL,
    phone_number varchar(40) NOT NULL,
    phone_type varchar(10) NOT NULL,
    search_key varchar(51) NOT NULL,
    occurrences int NOT NULL,
    PRIMARY KEY (id)
)