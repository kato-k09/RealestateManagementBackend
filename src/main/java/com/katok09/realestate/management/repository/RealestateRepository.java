package com.katok09.realestate.management.repository;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.ProjectRequest;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RealestateRepository {

  @Select("SELECT * FROM projects")
  public List<Project> getProjects();

  @Select("SELECT * FROM parcels")
  public List<Parcel> getParcels();

  @Select("SELECT * FROM buildings")
  public List<Building> getBuildings();

  @Select("SELECT * FROM income_and_expenses")
  public List<IncomeAndExpenses> getIncomeAndExpenses();

  @Insert("INSERT INTO projects(project_name, is_deleted) VALUES(#{projectName}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerProject(Project project);

  @Insert("INSERT INTO parcels(project_id, parcel_price, parcel_address, parcel_category, parcel_size, parcel_remark, is_deleted) "
      + "VALUES(#{projectId}, #{parcelPrice}, #{parcelAddress}, #{parcelCategory}, #{parcelSize}, #{parcelRemark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerParcel(Parcel parcel);

  @Insert("INSERT INTO buildings(project_id, building_price, building_address, building_address_number, building_type, "
      + "building_structure, building_size, building_date, building_remark, is_deleted) "
      + "VALUES(#{projectId}, #{buildingPrice}, #{buildingAddress}, #{buildingAddressNumber}, #{buildingType}, "
      + "#{buildingStructure}, #{buildingSize}, #{buildingDate}, #{buildingRemark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerBuilding(Building building);

  @Insert("INSERT INTO income_and_expenses(project_id, rent, maintenance_cost, repair_fund, management_fee, principal, interest, "
      + "tax, water_bill, electric_bill, gas_bill, fire_insurance, other, is_deleted)"
      + "VALUES(#{projectId}, #{rent}, #{maintenanceCost}, #{repairFund}, #{managementFee}, "
      + "#{principal}, #{interest}, #{tax}, #{waterBill}, #{electricBill}, #{gasBill}, #{fireInsurance}, #{other}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  public void registerIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  @Update(
      "UPDATE projects SET project_name=#{projectName}, is_deleted=#{isDeleted} WHERE id=#{id}")
  public void updateProject(Project project);

  @Update(
      "UPDATE parcels SET parcel_price=#{parcelPrice}, parcel_address=#{parcelAddress}, parcel_category=#{parcelCategory}, "
          + "parcel_size=#{parcelSize}, parcel_remark=#{parcelRemark}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateParcel(Parcel parcel);

  @Update(
      "UPDATE buildings SET building_price=#{buildingPrice}, building_address=#{buildingAddress}, "
          + "building_address_number=#{buildingAddressNumber}, building_type=#{buildingType}, "
          + "building_structure=#{buildingStructure}, building_size=#{buildingSize}, building_date=#{buildingDate}, "
          + "building_remark=#{buildingRemark}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateBuilding(Building building);

  @Update(
      "UPDATE income_and_expenses SET rent=#{rent}, maintenance_cost=#{maintenanceCost}, "
          + "repair_fund=#{repairFund}, management_fee=#{managementFee}, "
          + "principal=#{principal}, interest=#{interest}, tax=#{tax}, "
          + "water_bill=#{waterBill}, electric_bill=#{electricBill}, gas_bill=#{gasBill}, "
          + "fire_insurance=#{fireInsurance}, other=#{other}, is_deleted=#{isDeleted} WHERE project_id=#{projectId}")
  public void updateIncomeAndExpenses(IncomeAndExpenses incomeAndExpenses);

  @Delete("DELETE FROM projects WHERE id=#{id}")
  public void deleteProject(int id);

  @Delete("DELETE FROM parcels WHERE project_id=#{projectId}")
  public void deleteParcel(int projectId);

  @Delete("DELETE FROM buildings WHERE project_id=#{projectId}")
  public void deleteBuilding(int projectId);

  @Delete("DELETE FROM income_and_expenses WHERE project_id=#{projectId}")
  public void deleteIncomeAndExpenses(int projectId);
}
