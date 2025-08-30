-- ============================================
-- H2DB：データベース全体構造作成SQL
-- ============================================

-- 1. プロジェクトテーブル
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    project_name VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- 2. 土地テーブル
CREATE TABLE land_parcels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    land_parcel_price BIGINT,
    land_parcel_address VARCHAR(100),
    land_parcel_category VARCHAR(50),
    land_parcel_size DOUBLE,
    land_parcel_remark VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- 3. 建物テーブル
CREATE TABLE buildings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    building_price BIGINT,
    building_type VARCHAR(50),
    building_structure VARCHAR(50),
    building_size DOUBLE,
    building_date DATE,
    building_remark VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- 4. 収支テーブル
CREATE TABLE income_and_expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    rent INT,
    maintenance_cost INT,
    repair_fund INT,
    management_fee INT,
    principal INT,
    interest INT,
    tax INT,
    water_bill INT,
    electric_bill INT,
    gas_bill INT,
    fire_insurance INT,
    other VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- 5. ユーザーテーブル
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
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
    login_failed_attempts INT DEFAULT 0,
    account_locked_until TIMESTAMP DEFAULT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- ============================================
-- インデックス作成
-- ============================================

-- プロジェクトテーブル
CREATE INDEX idx_projects_user_id ON projects(user_id);

-- 土地テーブル
CREATE INDEX idx_land_parcels_project_id ON land_parcels(project_id);
CREATE INDEX idx_land_parcels_user_id ON land_parcels(user_id);

-- 建物テーブル
CREATE INDEX idx_buildings_project_id ON buildings(project_id);
CREATE INDEX idx_buildings_user_id ON buildings(user_id);

-- 収支テーブル
CREATE INDEX idx_income_expenses_project_id ON income_and_expenses(project_id);
CREATE INDEX idx_income_expenses_user_id ON income_and_expenses(user_id);

-- ユーザーテーブル
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
