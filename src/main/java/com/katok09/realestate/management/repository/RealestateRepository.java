package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RealestateRepository {

  /**
   * 不動産プロジェクト情報のリスト取得を行います。
   *
   * @return 不動産プロジェクト情報リスト
   */
  @Select("SELECT * FROM projects")
  public List<Project> getProjects();

  /**
   * 不動産土地情報のリスト取得を行います。
   *
   * @return 不動産土地情報リスト
   */
  @Select("SELECT * FROM parcels")
  public List<Parcel> getParcels();

  /**
   * 不動産建物情報のリスト取得を行います。
   *
   * @return 不動産建物情報リスト
   */
  @Select("SELECT * FROM buildings")
  public List<Building> getBuildings();

  /**
   * 不動産収支情報のリスト取得を行います。
   *
   * @return 不動産収支情報リスト
   */
  @Select("SELECT * FROM income_and_expenses")
  public List<IncomeAndExpenses> getIncomeAndExpenses();

  /**
   * 不動産プロジェクト情報の登録を行います。
   *
   * @param project 不動産プロジェクト情報
   */
  @Insert("INSERT INTO projects(project_name, is_deleted) VALUES(#{projectName}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerProject(Project project);

  /**
   * 不動産土地情報の登録を行います。
   *
   * @param parcel 不動産土地情報
   */
  @Insert(
      "INSERT INTO parcels(project_id, parcel_price, parcel_address, parcel_category, parcel_size, parcel_remark, is_deleted) "
          + "VALUES(#{projectId}, #{parcelPrice}, #{parcelAddress}, #{parcelCategory}, #{parcelSize}, #{parcelRemark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerParcel(Parcel parcel);

  /**
   * 不動産建物情報の登録を行います。
   *
   * @param building 不動産建物情報
   */
  @Insert("INSERT INTO buildings(project_id, building_price, building_address, building_type, "
      + "building_structure, building_size, building_date, building_remark, is_deleted) "
      + "VALUES(#{projectId}, #{buildingPrice}, #{buildingAddress}, #{buildingType}, "
      + "#{buildingStructure}, #{buildingSize}, #{buildingDate}, #{buildingRemark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerBuilding(Building building);

  /**
   * 不動産収支情報の登録を行います。
   *
   * @param incomeAndExpenses 不動産収支情報
   */
  @Insert(
      "INSERT INTO income_and_expenses(project_id, rent, maintenance_cost, repair_fund, management_fee, principal, interest, "
          + "tax, water_bill, electric_bill, gas_bill, fire_insurance, other, is_deleted)"
          + "VALUES(#{projectId}, #{rent}, #{maintenanceCost}, #{repairFund}, #{managementFee}, "
          + "#{principal}, #{interest}, #{tax}, #{waterBill}, #{electricBill}, #{gasBill}, #{fireInsurance}, #{other}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  /**
   * 不動産プロジェクト情報の更新を行います。
   *
   * @param project 不動産プロジェクト情報
   */
  @Update(
      "UPDATE projects SET project_name=#{projectName}, is_deleted=#{isDeleted} WHERE id=#{id}")
  public void updateProject(Project project);

  /**
   * 不動産土地情報の更新を行います。
   *
   * @param parcel 不動産土地情報
   */
  @Update(
      "UPDATE parcels SET parcel_price=#{parcelPrice}, parcel_address=#{parcelAddress}, parcel_category=#{parcelCategory}, "
          + "parcel_size=#{parcelSize}, parcel_remark=#{parcelRemark}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateParcel(Parcel parcel);

  /**
   * 不動産建物情報の更新を行います。
   *
   * @param building 不動産建物情報
   */
  @Update(
      "UPDATE buildings SET building_price=#{buildingPrice}, building_address=#{buildingAddress}, "
          + "building_type=#{buildingType}, building_structure=#{buildingStructure}, "
          + "building_size=#{buildingSize}, building_date=#{buildingDate}, "
          + "building_remark=#{buildingRemark}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateBuilding(Building building);

  /**
   * 不動産収支情報の更新を行います。
   *
   * @param incomeAndExpenses 不動産収支情報
   */
  @Update(
      "UPDATE income_and_expenses SET rent=#{rent}, maintenance_cost=#{maintenanceCost}, "
          + "repair_fund=#{repairFund}, management_fee=#{managementFee}, "
          + "principal=#{principal}, interest=#{interest}, tax=#{tax}, "
          + "water_bill=#{waterBill}, electric_bill=#{electricBill}, gas_bill=#{gasBill}, "
          + "fire_insurance=#{fireInsurance}, other=#{other}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  /**
   * 不動産プロジェクト情報の削除を行います。
   *
   * @param id 不動産プロジェクト情報のID
   */
  @Delete("DELETE FROM projects WHERE id=#{id}")
  public void deleteProject(int id);

  /**
   * 不動産土地情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  @Delete("DELETE FROM parcels WHERE project_id=#{projectId}")
  public void deleteParcel(int projectId);

  /**
   * 不動産建物情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  @Delete("DELETE FROM buildings WHERE project_id=#{projectId}")
  public void deleteBuilding(int projectId);

  /**
   * 不動産収支情報の削除を行います。
   *
   * @param projectId 不動産プロジェクト情報のID
   */
  @Delete("DELETE FROM income_and_expenses WHERE project_id=#{projectId}")
  public void deleteIncomeAndExpenses(int projectId);
}
