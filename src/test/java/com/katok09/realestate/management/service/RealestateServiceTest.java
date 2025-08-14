package com.katok09.realestate.management.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import com.katok09.realestate.management.repository.RealestateRepository;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RealestateServiceTest {

  @Mock
  private RealestateRepository repository;

  @Mock
  private JwtUtil jwtUtil;

  private RealestateService sut;


  @BeforeEach
  void before() {
    sut = new RealestateService(repository, jwtUtil);
  }

  @Test
  void 不動産一覧_検索_リポジトリが適切に呼び出されていること() {

    SearchParams dummySearchParams = new SearchParams();
    HttpServletRequest dummyRequestToken = mock(HttpServletRequest.class);

    when(jwtUtil.extractTokenFromRequest(dummyRequestToken)).thenReturn("DummyToken");
    when(jwtUtil.getUserIdFromToken("DummyToken")).thenReturn(999);

    sut.searchRealestate(dummySearchParams, dummyRequestToken);

    verify(repository, times(1)).searchRealestate(dummySearchParams);
  }

  @Test
  void 不動産登録_リポジトリが適切に呼び出されていること() {

    RealestateDetail dummyRequest = new RealestateDetail(
        new Project(), new Parcel(), new Building(), new IncomeAndExpenses());
    HttpServletRequest dummyRequestToken = mock(HttpServletRequest.class);

    when(jwtUtil.extractTokenFromRequest(dummyRequestToken)).thenReturn("DummyToken");
    when(jwtUtil.getUserIdFromToken("DummyToken")).thenReturn(999);

    sut.registerRealestate(dummyRequest, dummyRequestToken);

    verify(repository, times(1)).registerProject(dummyRequest.getProject());
    verify(repository, times(1)).registerParcel(dummyRequest.getParcel());
    verify(repository, times(1)).registerBuilding(dummyRequest.getBuilding());
    verify(repository, times(1)).registerIncomeAndExpenses(dummyRequest.getIncomeAndExpenses());
  }

  @Test
  void 不動産更新_リポジトリが適切に呼び出されていること() {

    RealestateDetail dummyRequest = new RealestateDetail(
        new Project(), new Parcel(), new Building(), new IncomeAndExpenses());
    HttpServletRequest dummyRequestToken = mock(HttpServletRequest.class);

    when(jwtUtil.extractTokenFromRequest(dummyRequestToken)).thenReturn("DummyToken");
    when(jwtUtil.getUserIdFromToken("DummyToken")).thenReturn(999);

    sut.updateRealestate(dummyRequest, dummyRequestToken);

    verify(repository, times(1)).updateProject(dummyRequest.getProject());
    verify(repository, times(1)).updateParcel(dummyRequest.getParcel());
    verify(repository, times(1)).updateBuilding(dummyRequest.getBuilding());
    verify(repository, times(1)).updateIncomeAndExpenses(dummyRequest.getIncomeAndExpenses());
  }

  @Test
  void 不動産削除_リポジトリが適切に呼び出されていること() {

    int projectId = 999;
    HttpServletRequest dummyRequestToken = mock(HttpServletRequest.class);

    when(jwtUtil.extractTokenFromRequest(dummyRequestToken)).thenReturn("DummyToken");
    when(jwtUtil.getUserIdFromToken("DummyToken")).thenReturn(999);

    sut.deleteRealestate(projectId, dummyRequestToken);

    verify(repository, times(1)).deleteProject(projectId, 999);
    verify(repository, times(1)).deleteParcel(projectId, 999);
    verify(repository, times(1)).deleteBuilding(projectId, 999);
    verify(repository, times(1)).deleteIncomeAndExpenses(projectId, 999);
  }

}
