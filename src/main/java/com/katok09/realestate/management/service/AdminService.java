package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.User;
import com.katok09.realestate.management.dto.StatusRequest;
import com.katok09.realestate.management.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理者専用APIに関するビジネスロジックを担当するサービス
 */
@Service
public class AdminService {

  private final UserRepository userRepository;

  public AdminService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * 削除済み以外の全てのユーザー情報を取得
   *
   * @return ユーザー情報リスト
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * 指定したユーザーステータスの更新（ADMINロールユーザーがいなくなることを防ぐため自身のステータスは更新できない仕様です）
   *
   * @param userId        ユーザーステータス更新対象のユーザーID
   * @param selfUserId    リクエスト元のユーザーID
   * @param statusRequest ユーザーステータスリクエストDTO
   */
  @Transactional
  public void updateStatus(int userId, int selfUserId, StatusRequest statusRequest) {
    if (userId == selfUserId) {
      throw new IllegalArgumentException("自信のステータスは変更できません。");
    }
    userRepository.updateStatus(userId, statusRequest);
  }
}
