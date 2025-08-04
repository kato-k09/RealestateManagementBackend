package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RealestateRepository {

  /**
   * 不動産詳細情報のリスト取得を行います。検索パラメーターの値で絞り込みを行います。
   *
   * @param searchParams 検索パラメーター
   * @return 不動産詳細情報リスト
   */
  public List<RealestateDetail> searchRealestate(SearchParams searchParams);

  /**
   * 不動産プロジェクト情報のリスト取得を行います。
   *
   * @return 不動産プロジェクト情報リスト
   */
  public List<Project> getProjects();

  /**
   * 不動産土地情報のリスト取得を行います。
   *
   * @return 不動産土地情報リスト
   */
  public List<Parcel> getParcels();

  /**
   * 不動産建物情報のリスト取得を行います。
   *
   * @return 不動産建物情報リスト
   */
  public List<Building> getBuildings();

  /**
   * 不動産収支情報のリスト取得を行います。
   *
   * @return 不動産収支情報リスト
   */
  public List<IncomeAndExpenses> getIncomeAndExpenses();

  /**
   * 不動産プロジェクト情報の登録を行います。
   *
   * @param project 不動産プロジェクト情報
   */
  public void registerProject(Project project);

  /**
   * 不動産土地情報の登録を行います。
   *
   * @param parcel 不動産土地情報
   */
  public void registerParcel(Parcel parcel);

  /**
   * 不動産建物情報の登録を行います。
   *
   * @param building 不動産建物情報
   */
  public void registerBuilding(Building building);

  /**
   * 不動産収支情報の登録を行います。
   *
   * @param incomeAndExpenses 不動産収支情報
   */
  public void registerIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  /**
   * 不動産プロジェクト情報の更新を行います。
   *
   * @param project 不動産プロジェクト情報
   */
  public void updateProject(Project project);

  /**
   * 不動産土地情報の更新を行います。
   *
   * @param parcel 不動産土地情報
   */
  public void updateParcel(Parcel parcel);

  /**
   * 不動産建物情報の更新を行います。
   *
   * @param building 不動産建物情報
   */
  public void updateBuilding(Building building);

  /**
   * 不動産収支情報の更新を行います。
   *
   * @param incomeAndExpenses 不動産収支情報
   */
  public void updateIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  /**
   * 不動産プロジェクト情報の削除を行います。
   *
   * @param id 不動産プロジェクト情報のID
   */
  public void deleteProject(int id, Long userId);

  /**
   * 不動産土地情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  public void deleteParcel(int projectId, Long userId);

  /**
   * 不動産建物情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  public void deleteBuilding(int projectId, Long userId);

  /**
   * 不動産収支情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  public void deleteIncomeAndExpenses(int projectId, Long userId);

  /**
   * 指定されたユーザーの不動産プロジェクトを削除します。。
   *
   * @param userId
   * @return
   */
  public void deleteProjectByUserId(Long userId);

  /**
   * 指定されたユーザーの不動産土地情報を削除します。。
   *
   * @param userId
   * @return
   */
  public void deleteParcelByUserId(Long userId);

  /**
   * 指定されたユーザーの不動産建物情報を削除します。。
   *
   * @param userId
   * @return
   */
  public void deleteBuildingByUserId(Long userId);

  /**
   * 指定されたユーザーの不動産収支情報を削除します。。
   *
   * @param userId
   * @return
   */
  public void deleteIncomeAndExpensesByUserId(Long userId);

}
