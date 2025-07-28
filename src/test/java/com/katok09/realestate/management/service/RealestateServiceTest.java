package com.katok09.realestate.management.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.repository.RealestateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RealestateServiceTest {

  @Mock
  private RealestateRepository repository;

  private RealestateService sut;

  @BeforeEach
  void before() {
    sut = new RealestateService(repository);
  }

  @Test
  void 不動産一覧_検索_リポジトリが適切に呼び出されていること() {

    RealestateDetail dummy = new RealestateDetail();

    sut.searchRealestate(dummy);

    verify(repository, times(1)).getProjects();
    verify(repository, times(1)).getParcels();
    verify(repository, times(1)).getBuildings();
    verify(repository, times(1)).getIncomeAndExpenses();
  }

  @Test
  void 不動産登録_リポジトリが適切に呼び出されていること() {

    RealestateDetail dummy = new RealestateDetail(
        new Project(), new Parcel(), new Building(), new IncomeAndExpenses());

    sut.registerRealestate(dummy);

    verify(repository, times(1)).registerProject(dummy.getProject());
    verify(repository, times(1)).registerParcel(dummy.getParcel());
    verify(repository, times(1)).registerBuilding(dummy.getBuilding());
    verify(repository, times(1)).registerIncomeAndExpenses(dummy.getIncomeAndExpenses());
  }

  @Test
  void 不動産更新_リポジトリが適切に呼び出されていること() {

    RealestateDetail dummy = new RealestateDetail(
        new Project(), new Parcel(), new Building(), new IncomeAndExpenses());

    sut.updateRealestate(dummy);

    verify(repository, times(1)).updateProject(dummy.getProject());
    verify(repository, times(1)).updateParcel(dummy.getParcel());
    verify(repository, times(1)).updateBuilding(dummy.getBuilding());
    verify(repository, times(1)).updateIncomeAndExpenses(dummy.getIncomeAndExpenses());
  }

  @Test
  void 不動産削除_リポジトリが適切に呼び出されていること() {

    int projectId = 999;

    sut.deleteRealestate(projectId);

    verify(repository, times(1)).deleteProject(projectId);
    verify(repository, times(1)).deleteParcel(projectId);
    verify(repository, times(1)).deleteBuilding(projectId);
    verify(repository, times(1)).deleteIncomeAndExpenses(projectId);
  }

}
