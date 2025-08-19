package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.dto.UpdateRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ユーザー情報のデータアクセスを提供するリポジトリ
 */
@Mapper
public interface UserRepository {

  /**
   * ユーザー名からユーザー情報を取得します。
   *
   * @param username ユーザー名
   * @return ユーザー情報
   */
  Optional<User> findByUsername(@Param("username") String username);

  /**
   * ユーザーIDからユーザー情報を取得します。
   *
   * @param id ユーザーID
   * @return ユーザー情報
   */
  Optional<User> findById(@Param("id") int id);

  /**
   * 指定されたユーザーIDが既に登録されているかを検証します。
   *
   * @param id ユーザーID
   * @return 既に登録されていればtrue、無ければfalseが返ります。
   */
  boolean existsByUserId(@Param("id") int id);

  /**
   * 指定されたユーザー名が既に登録されているかを検証します。
   *
   * @param username ユーザー名
   * @return 既に登録されていればtrue、無ければfalseが返ります。
   */
  boolean existsByUsername(@Param("username") String username);

  /**
   * 指定されたEmailが既に登録されているかを検証します。
   *
   * @param email Email
   * @return 既に登録されていればtrue、無ければfalseが返ります。
   */
  boolean existsByEmail(@Param("email") String email);

  /**
   * 自身を除いて指定されたユーザー名が既に登録されているかを検証します。
   *
   * @param username ユーザー名
   * @param id       自身のユーザーID
   * @return 既に登録されていればtrue、無ければfalseが返ります。
   */
  boolean existsByUsernameNotSelfId(@Param("username") String username, @Param("id") int id);

  /**
   * 自身を除いて指定されたEmailが既に登録されているかを検証します。
   *
   * @param email Email
   * @param id    自身のユーザーID
   * @return 既に登録されていればtrue、無ければfalseが返ります。
   */
  boolean existsByEmailNotSelfId(@Param("email") String email, @Param("id") int id);

  /**
   * 削除済み以外の全てのユーザー情報を取得します。
   *
   * @return ユーザー情報リスト
   */
  List<User> findAll();

  /**
   * 新規ユーザー登録をします。
   *
   * @param user ユーザー情報
   */
  void registerUser(@Param("user") User user);

  /**
   * ユーザー情報更新をします。
   *
   * @param id            更新対象ユーザーID
   * @param updateRequest ユーザー更新リクエストDTO
   */
  void updateUser(@Param("id") int id, @Param("updateRequest") UpdateRequest updateRequest);

  /**
   * ユーザーパスワードを更新します。
   *
   * @param id          更新対象ユーザーID
   * @param newPassword 新しいパスワード
   */
  void updatePassword(@Param("id") int id, @Param("newPassword") String newPassword);

  /**
   * ユーザー情報を削除します（削除フラグをtrueにし論理削除となります）
   *
   * @param id 削除対象ユーザーID
   */
  void deleteUserById(@Param("id") int id);

  /**
   * ログイン連続失敗回数、アカウントロック期限を設定します。
   *
   * @param id                  対象のユーザーID
   * @param loginFailedAttempts ログイン連続失敗回数
   * @param accountLockedUntil  アカウントロック期限
   */
  void updateLoginFailed(@Param("id") int id, @Param("loginFailedAttempts") int loginFailedAttempts,
      @Param("accountLockedUntil") LocalDateTime accountLockedUntil);

  /**
   * 最終ログイン日時を更新します。
   *
   * @param id          対象のユーザーID
   * @param lastLoginAt 最終ログイン日時
   */
  void updateLastLoginAt(@Param("id") int id, @Param("lastLoginAt") LocalDateTime lastLoginAt);

  /**
   * 各ユーザーのステータス情報を更新します（管理者専用）
   *
   * @param id            対象のユーザーID
   * @param statusRequest ステータスリクエストDTO
   */
  void updateStatus(@Param("id") int id, @Param("statusRequest") StatusRequest statusRequest);
}
