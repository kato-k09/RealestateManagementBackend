package com.katok09.realestate.management.service;

import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import com.katok09.realestate.management.repository.RealestateRepository;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealestateService {

  private final RealestateRepository repository;
  private final JwtUtil jwtUtil;

  @Autowired
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
    Long userId = jwtUtil.getUserIdFromToken(token);
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
    Long userId = jwtUtil.getUserIdFromToken(token);
    request.getProject().setUserId(userId);
    request.getParcel().setUserId(userId);
    request.getBuilding().setUserId(userId);
    request.getIncomeAndExpenses().setUserId(userId);

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

    // requestにはuserIdが入っているが改ざん防止の為に改めてトークンからuserIdを取得しセット
    String token = jwtUtil.extractTokenFromRequest(requestToken);
    Long userId = jwtUtil.getUserIdFromToken(token);
    request.getProject().setUserId(userId);
    request.getParcel().setUserId(userId);
    request.getBuilding().setUserId(userId);
    request.getIncomeAndExpenses().setUserId(userId);

    repository.updateProject(request.getProject());
    repository.updateParcel(request.getParcel());
    repository.updateBuilding(request.getBuilding());
    repository.updateIncomeAndExpenses(request.getIncomeAndExpenses());

  }

  /**
   * 不動産情報の削除を行います。
   *
   * @param projectId 不動産情報のID
   */
  @Transactional
  public void deleteRealestate(int projectId, HttpServletRequest requestToken) {

    String token = jwtUtil.extractTokenFromRequest(requestToken);
    Long userId = jwtUtil.getUserIdFromToken(token);

    repository.deleteProject(projectId, userId);
    repository.deleteParcel(projectId, userId);
    repository.deleteBuilding(projectId, userId);
    repository.deleteIncomeAndExpenses(projectId, userId);
  }

}
