package com.katok09.realestate.management.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワードハッシュ生成とテスト用クラス 一時的にmainメソッドで実行してハッシュを確認
 */
public class PasswordGenerator {

  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    String password = "password123";

    // 新しいハッシュを生成
    String newHash = encoder.encode(password);
    System.out.println("=== パスワードハッシュ生成結果 ===");
    System.out.println("元のパスワード: " + password);
    System.out.println("生成されたハッシュ: " + newHash);

    // 既存のハッシュとの比較テスト
    String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b6rCiWAXEBkEfC";
    boolean matchesExisting = encoder.matches(password, existingHash);
    System.out.println("既存ハッシュとの一致: " + matchesExisting);

    // 新しいハッシュとの比較テスト
    boolean matchesNew = encoder.matches(password, newHash);
    System.out.println("新しいハッシュとの一致: " + matchesNew);

    // 複数回生成して確認
    System.out.println("\n=== 複数回生成テスト ===");
    for (int i = 1; i <= 3; i++) {
      String hash = encoder.encode(password);
      boolean matches = encoder.matches(password, hash);
      System.out.println(i + "回目: " + hash + " (一致: " + matches + ")");
    }

    // MySQL UPDATE文を出力
    System.out.println("\n=== MySQL更新SQL ===");
    System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'admin';");
  }
}