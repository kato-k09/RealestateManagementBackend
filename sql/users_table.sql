-- ============================================
-- 本番環境用：usersテーブル作成SQL
-- ============================================

-- usersテーブル作成
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
                       is_deleted BOOLEAN NOT NULL DEFAULT false COMMENT '論理削除フラグ'
) COMMENT = 'ユーザー情報テーブル';

-- インデックス作成
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_last_login ON users(last_login_at);

-- ============================================
-- 初期管理者アカウント作成
-- ============================================

-- 管理者アカウント（パスワード: admin123）
-- 本番環境では必ずパスワードを変更してください
INSERT INTO users (username, password, email, display_name, role, enabled, is_deleted)
VALUES ('admin', '$2a$10$rQ8Q8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8Z8', 'admin@yourdomain.com', 'システム管理者', 'ADMIN', true, false);

-- ============================================
-- テーブル構造確認
-- ============================================

-- テーブル構造を確認
-- DESCRIBE users;

-- インデックス確認
-- SHOW INDEX FROM users;

-- ============================================
-- 運用上の注意事項
-- ============================================

/*
1. パスワードポリシー
   - 最低8文字以上を推奨
   - 英数字+記号の組み合わせを推奨
   - 定期的なパスワード変更を推奨

2. セキュリティ対策
   - 管理者アカウントのパスワードは必ず変更
   - 不要なアカウントは無効化（enabled = false）
   - 定期的なアクセスログの確認

3. バックアップ
   - usersテーブルの定期的なバックアップ
   - パスワードハッシュを含むため、バックアップファイルの厳重管理

4. メンテナンス
   - 論理削除されたユーザーの物理削除（必要に応じて）
   - ログイン履歴の古いデータのアーカイブ
*/