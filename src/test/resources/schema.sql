CREATE TABLE IF NOT EXISTS projects(id INT PRIMARY KEY AUTO_INCREMENT,project_name VARCHAR(100),is_deleted boolean);

CREATE TABLE IF NOT EXISTS parcels(id INT PRIMARY KEY AUTO_INCREMENT,project_id INT NOT NULL,parcel_price INT,parcel_address VARCHAR(100),parcel_category VARCHAR(50),parcel_size DOUBLE,parcel_remark VARCHAR(100),is_deleted boolean);

CREATE TABLE IF NOT EXISTS buildings(id INT PRIMARY KEY AUTO_INCREMENT, project_id INT NOT NULL, building_price INT, building_type VARCHAR(50), building_structure VARCHAR(50), building_size DOUBLE, building_date DATE, building_remark VARCHAR(100), is_deleted boolean);

CREATE TABLE IF NOT EXISTS income_and_expenses(id INT PRIMARY KEY AUTO_INCREMENT,project_id INT NOT NULL, rent INT, maintenance_cost INT, repair_fund INT, management_fee INT, principal INT, interest INT, tax INT, water_bill INT, electric_bill INT, gas_bill INT, fire_insurance INT, other VARCHAR(100), is_deleted boolean);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    password_changed_at TIMESTAMP NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
    );