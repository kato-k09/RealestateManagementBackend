INSERT INTO projects (project_name, is_deleted)
VALUES ('東三条AP', false),
       ('寿町AP', false),
       ('村上市ボロ戸建', false),
       ('南四日町戸建', false),
       ('吉田旭町AP', false),
       ('日興パレス長岡', false),
       ('東裏館新築AP', false),
       ('RC1棟', false);

-- 土地データ
INSERT INTO parcels (project_id, parcel_price, parcel_address, parcel_category, parcel_size,
                     parcel_remark, is_deleted)
VALUES (1, 10000000, '新潟県三条市', '宅地', 452.65, '', false),
       (2, 5000000, '燕市寿町', '宅地', 265.32, '', false),
       (3, 100000, '村上市', '宅地', 85.1, '', false),
       (4, 1000000, '三条市南四日町', '宅地', 121.11, '', false),
       (5, 36000000, '燕市吉田旭町', '宅地', 532.11, '', false),
       (6, 0, '長岡市柏町', '宅地', 0.0, '', false),
       (7, 20000000, '三条市東裏館', '田', 672.65, '取得価格の内造成費用1000万円を想定。',
        false),
       (8, 10000000, '三条市', '宅地', 660.0, '', false);

-- 建物データ
INSERT INTO buildings (project_id, building_price, building_type, building_structure,
                       building_size, building_date, building_remark, is_deleted)
VALUES (1, 5000000, 'アパート', '木造', 150.32, '1988-06-09', '', false),
       (2, 1000000, 'アパート', '木造', 176.91, '1986-06-20', '建物の傾き有り。',
        false),
       (3, 50000, '戸建て', '木造', 52.5, '1957-06-13', '', false),
       (4, 500000, '戸建て', '木造', 85.89, '1975-06-11', '', false),
       (5, 26000000, 'アパート', '鉄骨造', 265.32, '1994-06-15', '', false),
       (6, 1700000, 'マンション', '鉄筋コンクリート造', 26.12, '1991-01-23', '',
        false),
       (7, 90000000, 'アパート', '木造', 282.98, '2025-10-01', '2LDK×8部屋',
        false),
       (8, 50000000, 'マンション', '鉄筋コンクリート造', 1300.0, '1981-01-20',
        '2DK×24部屋', false);

-- 収支データ
INSERT INTO income_and_expenses (project_id, rent, maintenance_cost, repair_fund,
                                 management_fee, principal, interest, tax, water_bill,
                                 electric_bill, gas_bill, fire_insurance, other, is_deleted)
VALUES (1, 160000, 8000, 0, 0, 85000, 25000, 0, 0, 5000, 0, 5000, '', false),
       (2, 80000, 4000, 0, 0, 40000, 10000, 0, 0, 0, 0, 2000, '', false),
       (3, 30000, 1500, 0, 0, 0, 0, 0, 0, 0, 0, 1000, '', false),
       (4, 50000, 2500, 0, 0, 20000, 7000, 0, 0, 0, 0, 1000, '', false),
       (5, 700000, 14000, 0, 0, 350000, 40000, 0, 0, 5000, 0, 4000, '', false),
       (6, 30000, 1500, 8000, 7000, 0, 0, 0, 0, 0, 0, 800, '', false),
       (7, 640000, 32000, 0, 0, 250000, 220000, 0, 0, 8000, 0, 4000, '', false),
       (8, 1100000, 0, 0, 0, 0, 0, 0, 20000, 20000, 20000, 20000, '', false);


INSERT INTO users (username, password, email, display_name, role, enabled, is_deleted)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC',
        'admin@example.com', '管理者', 'ADMIN', true, false),
       ('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC',
        'user1@example.com', '山田太郎', 'USER', true, false),
       ('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC',
        'user2@example.com', '田中花子', 'USER', true, false);