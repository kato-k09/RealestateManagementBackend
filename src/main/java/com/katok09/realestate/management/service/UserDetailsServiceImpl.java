package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Securityが認証時に使用するUserDetailsServiceの実装 ユーザー名からユーザー情報を取得し、Spring SecurityのUserDetails形式で返す
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  /**
   * ユーザー名からUserDetailsを取得 Spring Securityの認証処理で自動的に呼び出される
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      // データベースからユーザー情報を取得
      User user = userRepository.findByUsername(username)
          .orElseThrow(
              () -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

      // 削除されたユーザーは認証不可
      if (user.isDeleted()) {
        throw new UsernameNotFoundException("ユーザーが削除されています: " + username);
      }

      // 無効なユーザーは認証不可
      if (!user.isEnabled()) {
        throw new UsernameNotFoundException("ユーザーが無効です: " + username);
      }

      // UserDetailsインターフェースを実装したオブジェクトを返す
      return new CustomUserPrincipal(user);

    } catch (UsernameNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new UsernameNotFoundException("ユーザー検索中にエラーが発生しました", e);
    }
  }

  /**
   * カスタムUserPrincipalクラス Spring SecurityのUserDetailsインターフェースを実装
   */
  public static class CustomUserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;
    private final User user;

    public CustomUserPrincipal(User user) {
      this.user = user;
    }

    /**
     * ユーザーの権限（ロール）を取得 ROLE_プレフィックスを付けてGrantedAuthorityのリストを返す
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List<GrantedAuthority> authorities = new ArrayList<>();

      // ユーザーのロールを権限として追加
      if (user.getRole() != null) {
        // Spring Securityの慣例に従い、ROLE_プレフィックスを付ける
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
      }

      return authorities;
    }

    /**
     * パスワードを取得
     */
    @Override
    public String getPassword() {
      return user.getPassword();
    }

    /**
     * ユーザー名を取得
     */
    @Override
    public String getUsername() {
      return user.getUsername();
    }

    /**
     * アカウントの有効期限が切れていないかチェック 今回は常にtrueを返す（有効期限なし）
     */
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    /**
     * アカウントがロックされていないかチェック 今回は常にtrueを返す（ロック機能なし）
     */
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    /**
     * 認証情報（パスワード）の有効期限が切れていないかチェック 今回は常にtrueを返す（パスワード有効期限なし）
     */
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    /**
     * ユーザーが有効かどうかチェック
     */
    @Override
    public boolean isEnabled() {
      return user.isEnabled() && !user.isDeleted();
    }

    /**
     * 元のUserオブジェクトを取得 コントローラーやサービスで使用
     */
    public User getUser() {
      return user;
    }

    /**
     * ユーザーIDを取得
     */
    public Long getUserId() {
      return user.getId();
    }

    /**
     * 表示名を取得
     */
    public String getDisplayName() {
      return user.getDisplayName();
    }

    /**
     * メールアドレスを取得
     */
    public String getEmail() {
      return user.getEmail();
    }

    /**
     * ロールを取得
     */
    public String getRole() {
      return user.getRole();
    }
  }
}