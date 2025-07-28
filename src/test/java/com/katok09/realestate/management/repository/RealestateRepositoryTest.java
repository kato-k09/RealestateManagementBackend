package com.katok09.realestate.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
public class RealestateRepositoryTest {

  @Autowired
  private RealestateRepository sut;

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

    sut.registerProject(new Project());

    assertThat(sut.getProjects().size()).isEqualTo(9);
  }

  @Test
  void 不動産土地情報が登録できること() {

    sut.registerParcel(new Parcel());

    assertThat(sut.getParcels().size()).isEqualTo(9);
  }

  @Test
  void 不動産建物情報が登録できること() {

    sut.registerBuilding(new Building());

    assertThat(sut.getBuildings().size()).isEqualTo(9);
  }

  @Test
  void 不動産収支情報が登録できること() {

    sut.registerIncomeAndExpenses(new IncomeAndExpenses());

    assertThat(sut.getIncomeAndExpenses().size()).isEqualTo(9);
  }

  @Test
  void 不動産プロジェクト情報が更新できること() {

    Project project = new Project();
    project.setId(1);
    project.setProjectName("テストプロジェクト");

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

    sut.deleteProject(1);

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

    sut.deleteParcel(1);

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

    sut.deleteBuilding(1);

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

    sut.deleteIncomeAndExpenses(1);

    assertThat(sut.getIncomeAndExpenses().stream()
        .noneMatch(i -> i.getProjectId() == 1))
        .isTrue();

    assertThat(sut.getIncomeAndExpenses().size()).isEqualTo(7);
  }

}
