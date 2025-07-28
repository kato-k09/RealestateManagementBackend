package com.katok09.realestate.management.service;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.repository.RealestateRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RealestateService {

  private RealestateRepository repository;

  @Autowired
  public RealestateService(RealestateRepository repository) {
    this.repository = repository;
  }

  /**
   * 不動産情報の一覧表示・検索を行います。
   *
   * @param realestateDetail 不動産情報の検索パラメーター
   * @return 検索結果の不動産情報リスト
   */
  public List<RealestateDetail> searchRealestate(RealestateDetail realestateDetail) {
    List<RealestateDetail> result = new ArrayList<>();
    List<Project> project = repository.getProjects();
    List<Parcel> parcels = repository.getParcels();
    List<Building> building = repository.getBuildings();
    List<IncomeAndExpenses> incomeAndExpenses = repository.getIncomeAndExpenses();

    for (int i = 0; i < project.size(); i++) {
      RealestateDetail resultTemp = new RealestateDetail();
      resultTemp.setProject(project.get(i));
      resultTemp.setParcel(parcels.get(i));
      resultTemp.setBuilding(building.get(i));
      resultTemp.setIncomeAndExpenses(incomeAndExpenses.get(i));
      result.add(resultTemp);
    }

    return result;
  }

  /**
   * 不動産情報の登録を行います。
   *
   * @param request 不動産登録情報
   */
  @Transactional
  public void registerRealestate(RealestateDetail request) {
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
  public void updateRealestate(RealestateDetail request) {

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
  public void deleteRealestate(int projectId) {

    repository.deleteProject(projectId);
    repository.deleteParcel(projectId);
    repository.deleteBuilding(projectId);
    repository.deleteIncomeAndExpenses(projectId);
  }

}

