-- ============================================
-- 本番環境用：データベース全体構造作成SQL
-- ============================================

-- 1. プロジェクトテーブル
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    project_name VARCHAR(100) COMMENT 'プロジェクト名',
    is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = 'プロジェクト情報テーブル';

-- 2. 土地テーブル
CREATE TABLE parcels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    parcel_price BIGINT COMMENT '土地価格',
    parcel_address VARCHAR(100) COMMENT '土地住所',
    parcel_category VARCHAR(50) COMMENT '土地カテゴリ',
    parcel_size DOUBLE COMMENT '土地面積',
    parcel_remark VARCHAR(100) COMMENT '土地備考',
    is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = '土地情報テーブル';

-- 3. 建物テーブル
CREATE TABLE buildings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    building_price BIGINT COMMENT '建物価格',
    building_type VARCHAR(50) COMMENT '建物種別',
    building_structure VARCHAR(50) COMMENT '建物構造',
    building_size DOUBLE COMMENT '建物面積',
    building_date DATE COMMENT '建築年月日',
    building_remark VARCHAR(100) COMMENT '建物備考',
    is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = '建物情報テーブル';

-- 4. 収支テーブル
CREATE TABLE income_and_expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    rent INT COMMENT '家賃収入',
    maintenance_cost INT COMMENT '維持費',
    repair_fund INT COMMENT '修繕積立金',
    management_fee INT COMMENT '管理費',
    principal INT COMMENT '元本',
    interest INT COMMENT '利息',
    tax INT COMMENT '税金',
    water_bill INT COMMENT '水道代',
    electric_bill INT COMMENT '電気代',
    gas_bill INT COMMENT 'ガス代',
    fire_insurance INT COMMENT '火災保険',
    other VARCHAR(100) COMMENT 'その他',
    is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = '収支情報テーブル';

-- 5. ユーザーテーブル
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'ユーザー名（ログイン用）',
    password VARCHAR(255) NOT NULL COMMENT 'パスワード（BCryptハッシュ化済み）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT 'メールアドレス',
    display_name VARCHAR(100) NOT NULL COMMENT '表示名',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT 'ユーザーロール（USER, ADMIN）',
    enabled BOOLEAN NOT NULL DEFAULT true COMMENT 'アカウント有効フラグ',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'アカウント作成日時',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最終更新日時',
    last_login_at TIMESTAMP NULL COMMENT '最終ログイン日時',
    password_changed_at TIMESTAMP NULL COMMENT 'パスワード変更日時',
    login_failed_attempts INT DEFAULT 0 COMMENT 'ログイン失敗回数',
    account_locked_until TIMESTAMP DEFAULT NULL COMMENT 'アカウントロック期限',
    is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = 'ユーザー情報テーブル';

-- ============================================
-- インデックス作成
-- ============================================

-- プロジェクトテーブル
CREATE INDEX idx_projects_user_id ON projects(user_id);

-- 土地テーブル
CREATE INDEX idx_parcels_project_id ON parcels(project_id);
CREATE INDEX idx_parcels_user_id ON parcels(user_id);

-- 建物テーブル
CREATE INDEX idx_buildings_project_id ON buildings(project_id);
CREATE INDEX idx_buildings_user_id ON buildings(user_id);

-- 収支テーブル
CREATE INDEX idx_income_expenses_project_id ON income_and_expenses(project_id);
CREATE INDEX idx_income_expenses_user_id ON income_and_expenses(user_id);

-- ユーザーテーブル
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
