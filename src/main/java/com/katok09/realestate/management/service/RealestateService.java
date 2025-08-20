package com.katok09.realestate.management.service;

import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import com.katok09.realestate.management.exception.ResourceNotFoundException;
import com.katok09.realestate.management.repository.RealestateRepository;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不動産関係のビジネスロジックを担当するサービス
 */
@Service
public class RealestateService {

  private final RealestateRepository repository;
  private final JwtUtil jwtUtil;

  public RealestateService(RealestateRepository repository, JwtUtil jwtUtil) {
    this.repository = repository;
    this.jwtUtil = jwtUtil;
  }

  /**
   * 不動産情報の一覧表示・検索を行います。
   *
   * @param searchParams 不動産情報の検索パラメーター
   * @return 検索結果の不動産情報リスト
   */
  public List<RealestateDetail> searchRealestate(SearchParams searchParams,
      HttpServletRequest requestToken) {

    String token = jwtUtil.extractTokenFromRequest(requestToken);
    int userId = jwtUtil.getUserIdFromToken(token);
    searchParams.setUserId(userId);

    return repository.searchRealestate(searchParams);
  }

  /**
   * 不動産情報の登録を行います。
   *
   * @param request 不動産登録情報
   */
  @Transactional
  public void registerRealestate(RealestateDetail request, HttpServletRequest requestToken) {

    String token = jwtUtil.extractTokenFromRequest(requestToken);
    int userId = jwtUtil.getUserIdFromToken(token);
    request.getProject().setUserId(userId);
    request.getParcel().setUserId(userId);
    request.getBuilding().setUserId(userId);
    request.getIncomeAndExpenses().setUserId(userId);

    // プロジェクトオブジェクトの自動採番を行ってから各オブジェクトにプロジェクトIDを設定します。
    repository.registerProject(request.getProject());

    request.getParcel().setProjectId(request.getProject().getId());
    request.getBuilding().setProjectId(request.getProject().getId());
    request.getIncomeAndExpenses().setProjectId(request.getProject().getId());

    repository.registerParcel(request.getParcel());
    repository.registerBuilding(request.getBuilding());
    repository.registerIncomeAndExpenses(request.getIncomeAndExpenses());
  }

  /**
   * 不動産情報の更新を行います。
   *
   * @param request 不動産更新情報
   */
  @Transactional
  public void updateRealestate(RealestateDetail request, HttpServletRequest requestToken) {

    if (!isProjectIdConsistent(request)) {
      throw new IllegalArgumentException("プロジェクトIDが一致していません。");
    }

    // トークンから取得したユーザーIDとrequest内の各オブジェクトのユーザーIDを照合
    String token = jwtUtil.extractTokenFromRequest(requestToken);
    int userId = jwtUtil.getUserIdFromToken(token);

    if (!isUserIdConsistent(userId, request)) {
      throw new IllegalArgumentException("ユーザーIDが一致していません。");
    }

    if (repository.updateProject(request.getProject()) == 0) {
      throw new ResourceNotFoundException("更新対象のプロジェクトが存在しません。");
    }
    if (repository.updateParcel(request.getParcel()) == 0) {
      throw new ResourceNotFoundException("更新対象の土地情報が存在しません。");
    }
    if (repository.updateBuilding(request.getBuilding()) == 0) {
      throw new ResourceNotFoundException("更新対象の建物情報が存在しません。");
    }
    if (repository.updateIncomeAndExpenses(request.getIncomeAndExpenses()) == 0) {
      throw new ResourceNotFoundException("更新対象の収支情報が存在しません。");
    }
  }

  /**
   * 不動産情報の削除を行います。
   *
   * @param projectId    不動産情報のID
   * @param requestToken HTTPリクエストトークン
   */
  @Transactional
  public void deleteRealestate(int projectId, HttpServletRequest requestToken) {

    String token = jwtUtil.extractTokenFromRequest(requestToken);
    int userId = jwtUtil.getUserIdFromToken(token);

    // トークンから抽出したユーザーIDが設定されているプロジェクトIDのみ削除が実行されます。
    // これにより本人以外のプロジェクトが削除されることを防止します。
    if (repository.deleteProject(projectId, userId) == 0) {
      throw new ResourceNotFoundException("削除対象のプロジェクトが存在しません。");
    }
    if (repository.deleteParcel(projectId, userId) == 0) {
      throw new ResourceNotFoundException("削除対象の土地情報が存在しません。");
    }
    if (repository.deleteBuilding(projectId, userId) == 0) {
      throw new ResourceNotFoundException("削除対象の建物情報が存在しません。");
    }
    if (repository.deleteIncomeAndExpenses(projectId, userId) == 0) {
      throw new ResourceNotFoundException("削除対象の収支情報が存在しません。");
    }
  }

  /**
   * 指定されたユーザーの不動産情報を全て削除します。
   *
   * @param userId トークンから抽出したユーザーID（ユーザーID偽装防止）
   */
  @Transactional
  public void deleteRealestateByUserId(int userId) {

    repository.deleteProjectByUserId(userId);
    repository.deleteParcelByUserId(userId);
    repository.deleteBuildingByUserId(userId);
    repository.deleteIncomeAndExpensesByUserId(userId);
  }

  private boolean isProjectIdConsistent(RealestateDetail request) {
    int projectId = request.getProject().getId();
    return projectId == request.getParcel().getProjectId() &&
        projectId == request.getBuilding().getProjectId() &&
        projectId == request.getIncomeAndExpenses().getProjectId();
  }

  private boolean isUserIdConsistent(int userId, RealestateDetail request) {
    return userId == request.getProject().getUserId() &&
        userId == request.getParcel().getUserId() &&
        userId == request.getBuilding().getUserId() &&
        userId == request.getIncomeAndExpenses().getUserId();
  }

}
