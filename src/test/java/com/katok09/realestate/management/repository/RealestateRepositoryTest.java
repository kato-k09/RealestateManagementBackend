package com.katok09.realestate.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
public class RealestateRepositoryTest {

  @Autowired
  private RealestateRepository sut;

  @Test
  void 不動産詳細情報リストが全件取得できること() {

    SearchParams searchParams = new SearchParams();
    searchParams.setUserId(1L);

    List<RealestateDetail> actual = sut.searchRealestate(searchParams);

    assertThat(actual.size()).isEqualTo(4);

  }

  @Test
  void 不動産詳細情報リストが検索条件で取得できること() {

    SearchParams searchParams = new SearchParams(1L, null, "三条", null,
        null, null);

    List<RealestateDetail> actual = sut.searchRealestate(searchParams);

    assertThat(actual.size()).isEqualTo(3);
  }

  @Test
  void 不動産プロジェクト情報リストが取得できること() {

    List<Project> actual = sut.getProjects();

    assertThat(actual.size()).isEqualTo(8);
  }

  @Test
  void 不動産土地情報リストが取得できること() {

    List<Parcel> actual = sut.getParcels();

    assertThat(actual.size()).isEqualTo(8);
  }

  @Test
  void 不動産建物情報リストが取得できること() {

    List<Building> actual = sut.getBuildings();

    assertThat(actual.size()).isEqualTo(8);
  }

  @Test
  void 不動産収支情報リストが取得できること() {

    List<IncomeAndExpenses> actual = sut.getIncomeAndExpenses();

    assertThat(actual.size()).isEqualTo(8);
  }

  @Test
  void 不動産プロジェクト情報が登録できること() {

    Project project = new Project();
    project.setUserId(1L);

    sut.registerProject(project);

    assertThat(sut.getProjects().size()).isEqualTo(9);
  }

  @Test
  void 不動産土地情報が登録できること() {

    Parcel parcel = new Parcel();
    parcel.setUserId(1L);

    sut.registerParcel(parcel);

    assertThat(sut.getParcels().size()).isEqualTo(9);
  }

  @Test
  void 不動産建物情報が登録できること() {

    Building building = new Building();
    building.setUserId(1L);

    sut.registerBuilding(building);

    assertThat(sut.getBuildings().size()).isEqualTo(9);
  }

  @Test
  void 不動産収支情報が登録できること() {

    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setUserId(1L);

    sut.registerIncomeAndExpenses(incomeAndExpenses);

    assertThat(sut.getIncomeAndExpenses().size()).isEqualTo(9);
  }

  @Test
  void 不動産プロジェクト情報が更新できること() {

    Project project = new Project();
    project.setId(1);
    project.setUserId(1L);
    project.setProjectName("テストプロジェクト");

    assertThat(sut.getProjects().stream()
        .filter(p -> p.getId() == 1) // プロジェクトリストからid=1のものを抽出
        .findFirst()
        .orElseThrow()
        .getProjectName()).isEqualTo("東三条AP");

    sut.updateProject(project);

    assertThat(sut.getProjects().stream()
        .filter(p -> p.getId() == 1) // プロジェクトリストからid=1のものを抽出
        .findFirst()
        .orElseThrow()
        .getProjectName()).isEqualTo("テストプロジェクト");
  }

  @Test
  void 不動産土地情報が更新できること() {

    Parcel parcel = new Parcel();
    parcel.setProjectId(1);
    parcel.setUserId(1L);
    parcel.setParcelPrice(99999999);

    assertThat(sut.getParcels().stream()
        .filter(p -> p.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getParcelPrice()).isEqualTo(10000000);

    sut.updateParcel(parcel);

    assertThat(sut.getParcels().stream()
        .filter(p -> p.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getParcelPrice()).isEqualTo(99999999);
  }

  @Test
  void 不動産建物情報が更新できること() {

    Building building = new Building();
    building.setProjectId(1);
    building.setUserId(1L);
    building.setBuildingPrice(99999999);

    assertThat(sut.getBuildings().stream()
        .filter(b -> b.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getBuildingPrice()).isEqualTo(5000000);

    sut.updateBuilding(building);

    assertThat(sut.getBuildings().stream()
        .filter(b -> b.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getBuildingPrice()).isEqualTo(99999999);
  }

  @Test
  void 不動産収支情報が更新できること() {

    IncomeAndExpenses incomeAndExpenses = new IncomeAndExpenses();
    incomeAndExpenses.setProjectId(1);
    incomeAndExpenses.setUserId(1L);
    incomeAndExpenses.setRent(99999999);

    assertThat(sut.getIncomeAndExpenses().stream()
        .filter(i -> i.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getRent()).isEqualTo(160000);

    sut.updateIncomeAndExpenses(incomeAndExpenses);

    assertThat(sut.getIncomeAndExpenses().stream()
        .filter(i -> i.getProjectId() == 1)
        .findFirst()
        .orElseThrow()
        .getRent()).isEqualTo(99999999);
  }

  @Test
  void 不動産プロジェクト情報が削除できること() {

    assertThat(sut.getProjects().stream()
        .anyMatch(p -> p.getId() == 1))
        .isTrue();

    sut.deleteProject(1, 1L);

    assertThat(sut.getProjects().stream()
        .noneMatch(p -> p.getId() == 1))
        .isTrue();

    assertThat(sut.getProjects().size()).isEqualTo(7);
  }

  @Test
  void 不動産土地情報が削除できること() {

    assertThat(sut.getParcels().stream()
        .anyMatch(p -> p.getProjectId() == 1))
        .isTrue();

    sut.deleteParcel(1, 1L);

    assertThat(sut.getParcels().stream()
        .noneMatch(p -> p.getProjectId() == 1))
        .isTrue();

    assertThat(sut.getParcels().size()).isEqualTo(7);
  }

  @Test
  void 不動産建物情報が削除できること() {

    assertThat(sut.getBuildings().stream()
        .anyMatch(b -> b.getProjectId() == 1))
        .isTrue();

    sut.deleteBuilding(1, 1L);

    assertThat(sut.getBuildings().stream()
        .noneMatch(b -> b.getProjectId() == 1))
        .isTrue();

    assertThat(sut.getBuildings().size()).isEqualTo(7);
  }

  @Test
  void 不動産収支情報が削除できること() {

    assertThat(sut.getIncomeAndExpenses().stream()
        .anyMatch(i -> i.getProjectId() == 1))
        .isTrue();

    sut.deleteIncomeAndExpenses(1, 1L);

    assertThat(sut.getIncomeAndExpenses().stream()
        .noneMatch(i -> i.getProjectId() == 1))
        .isTrue();

    assertThat(sut.getIncomeAndExpenses().size()).isEqualTo(7);
  }

}
