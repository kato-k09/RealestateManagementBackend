-- ============================================
-- 本番環境用：初期データ投入SQL
-- ============================================

-- 管理者ユーザー（本番用）
INSERT INTO users (username, password, email, display_name, role, enabled, login_failed_attempts, account_locked_until, is_deleted)
VALUES ('admin', '$2a$10$7imSMTO8x43cBc9LdoeRTOPLsNvxUaxElB9dDzEv5RhoYdY6x8ve6',
        'admin@example.com', '管理者', 'ADMIN', true, 0, null, false);

-- ゲストユーザー（デモ用）
INSERT INTO users (username, password, email, display_name, role, enabled, login_failed_attempts, account_locked_until, is_deleted)
VALUES ('guest', '$2a$10$St177xwQG11Bx1WAvt4yqurgrVfeeuFJayIo8v2Zw1wrpmkGq1.aC',
        'guest@example.com', 'ゲストユーザー', 'GUEST', true, 0, null, false);

-- サンプルユーザー（ポートフォリオ用）
INSERT INTO users (username, password, email, display_name, role, enabled, login_failed_attempts, account_locked_until, is_deleted)
VALUES ('demo_user', '$2a$10$7imSMTO8x43cBc9LdoeRTOPLsNvxUaxElB9dDzEv5RhoYdY6x8ve6',
        'demo@example.com', 'デモユーザー', 'USER', true, 0, null, false);

-- サンプルプロジェクトデータ
INSERT INTO projects (user_id, project_name, is_deleted)
VALUES (2, '東三条アパート', false),
       (2, '寿町アパート', false),
       (3, '村上市戸建', false),
       (2, '南四日町戸建', false),
       (3, '吉田旭町アパート', false);

-- サンプル土地データ
INSERT INTO parcels (project_id, user_id, parcel_price, parcel_address, parcel_category, parcel_size, parcel_remark, is_deleted)
VALUES (1, 2, 10000000, '新潟県三条市東三条', '宅地', 452.65, '駅徒歩10分の好立地', false),
       (2, 2, 5000000, '燕市寿町', '宅地', 265.32, '住宅街の静かな立地', false),
       (3, 3, 100000, '村上市', '宅地', 85.1, '要リフォーム物件', false),
       (4, 2, 1000000, '三条市南四日町', '宅地', 121.11, '商業地区近く', false),
       (5, 3, 36000000, '燕市吉田旭町', '宅地', 532.11, '新築アパート用地', false);

-- サンプル建物データ
INSERT INTO buildings (project_id, user_id, building_price, building_type, building_structure, building_size, building_date, building_remark, is_deleted)
VALUES (1, 2, 5000000, 'アパート', '木造', 150.32, '1988-06-09', '2DK×4部屋', false),
       (2, 2, 1000000, 'アパート', '木造', 176.91, '1986-06-20', '要修繕箇所あり', false),
       (3, 3, 50000, '戸建て', '木造', 52.5, '1957-06-13', '古民家風戸建て', false),
       (4, 2, 500000, '戸建て', '木造', 85.89, '1975-06-11', 'リフォーム済み', false),
       (5, 3, 26000000, 'アパート', '鉄骨造', 265.32, '1994-06-15', '1K×8部屋', false);

-- サンプル収支データ
INSERT INTO income_and_expenses (project_id, user_id, rent, maintenance_cost, repair_fund, management_fee, principal, interest, tax, water_bill, electric_bill, gas_bill, fire_insurance, other, is_deleted)
VALUES (1, 2, 160000, 8000, 0, 0, 85000, 25000, 0, 0, 5000, 0, 5000, '', false),
       (2, 2, 80000, 4000, 0, 0, 40000, 10000, 0, 0, 0, 0, 2000, '', false),
       (3, 3, 30000, 1500, 0, 0, 0, 0, 0, 0, 0, 0, 1000, '', false),
       (4, 2, 50000, 2500, 0, 0, 20000, 7000, 0, 0, 0, 0, 1000, '', false),
       (5, 3, 700000, 14000, 0, 0, 350000, 40000, 0, 0, 5000, 0, 4000, '', false);
