package com.katok09.realestate.management.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.LoginRequest;
import com.katok09.realestate.management.dto.LoginResponse;
import com.katok09.realestate.management.util.JwtUtil;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RealestateControllerIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private JwtUtil jwtUtil;

  private int userId;
  private int projectId;

  @BeforeEach
  void before() {
    jdbcTemplate.execute("DROP ALL OBJECTS");
    jdbcTemplate.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
    jdbcTemplate.execute("RUNSCRIPT FROM 'classpath:data.sql'");
  }

  @Test
  void 自身のユーザーIDに紐づいた不動産情報が取得できること() {

    List<RealestateDetail> detailList = getRealestateDetails(
        "user1", "password123", "");

    verifyUserIdMatch(detailList);
  }

  @Test
  void トークンなしで不動産情報を取得した時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);

    HttpHeaders headers = new HttpHeaders();
    // headers.setBearerAuth(token); 認証トークンをヘッダーに付与しない
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/searchRealestate", HttpMethod.GET, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void 無効なトークンで不動産情報を取得した時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/searchRealestate", HttpMethod.GET, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains(
        "JWTトークンが無効または期限切れです");
  }

  @Test
  void 検索パラメーターに一致した不動産情報のみ取得できること() {

    List<RealestateDetail> detailList = getRealestateDetails(
        "user1", "password123", "searchProjectName=村上市");

    verifyRealestateDetail(detailList, "村上市ボロ戸建", 100000L, 50000L, 30000);
  }

  @Test
  void 不動産登録が正常に行われること() {

    String token = performLogin("emptyProjectUser", "password123");
    userId = jwtUtil.getUserIdFromToken(token);

    RealestateDetail detail = simpleCreateRealestateDetails(
        "RegisterProject", 99999L, 999999L, 9999);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/registerRealestate", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 不動産情報をAPIから取得し変更が反映されているかを検証
    List<RealestateDetail> detailList = getRealestateDetails("emptyProjectUser", "password123", "");
    assertThat(detailList.size()).isEqualTo(1);
    List<RealestateDetail> filteredDetail = filterByProjectName(detailList, "RegisterProject");
    verifyRealestateDetail(filteredDetail, "RegisterProject", 99999L, 999999L, 9999);

  }

  @Test
  void 無効な不動産情報で登録した時400エラーが返り全データがロールバックされること() {

    String token = performLogin("emptyProjectUser", "password123");
    userId = jwtUtil.getUserIdFromToken(token);

    // プロジェクト名は必須だが空に設定
    RealestateDetail detail = simpleCreateRealestateDetails(
        "", 99999L, 99999L, 9999);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/registerRealestate", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("プロジェクト名を入力してください");

    // 不動産情報をAPIから取得し変更が反映されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("emptyProjectUser", "password123", "");
    assertThat(detailList).isEmpty();
  }

  @Test
  void トークンなしで不動産登録をした時に401エラーが返ること() {
    RealestateDetail detail = simpleCreateRealestateDetails(
        "DummyProject", 99999L, 999999L, 9999);

    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/registerRealestate", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");
  }

  @Test
  void 無効なトークンで不動産登録をした時に401エラーが返ること() {
    RealestateDetail detail = simpleCreateRealestateDetails(
        "DummyProject", 99999L, 999999L, 9999);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/registerRealestate", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");
  }

  @Test
  void 不動産更新が正常に行われること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    RealestateDetail detail = simpleCreateRealestateDetails(
        "ChangedProject", 99999L, 999999L, 9999);

    // 更新対象のユーザーIDとプロジェクトIDを設定
    detail.getProject().setUserId(userId);
    detail.getParcel().setUserId(userId);
    detail.getBuilding().setUserId(userId);
    detail.getIncomeAndExpenses().setUserId(userId);
    detail.getProject().setId(projectId);
    detail.getParcel().setProjectId(projectId);
    detail.getBuilding().setProjectId(projectId);
    detail.getIncomeAndExpenses().setProjectId(projectId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/updateRealestate", HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 不動産情報をAPIから取得し変更が反映されているかを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    verifyRealestateDetail(filteredDetail, "ChangedProject", 99999L, 999999L, 9999);
  }

  @Test
  void トークンなしで不動産更新をした時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    RealestateDetail detail = simpleCreateRealestateDetails(
        "ChangedProject", 99999L, 999999L, 9999);

    // 更新対象のユーザーIDとプロジェクトIDを設定
    detail.getProject().setUserId(userId);
    detail.getParcel().setUserId(userId);
    detail.getBuilding().setUserId(userId);
    detail.getIncomeAndExpenses().setUserId(userId);
    detail.getProject().setId(projectId);
    detail.getParcel().setProjectId(projectId);
    detail.getBuilding().setProjectId(projectId);
    detail.getIncomeAndExpenses().setProjectId(projectId);

    HttpHeaders headers = new HttpHeaders();
    // headers.setBearerAuth(token); 認証トークンをヘッダーに付与しない
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/updateRealestate", HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");

    // 不動産情報をAPIから取得し変更が反映されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    verifyRealestateDetail(filteredDetail, "村上市ボロ戸建", 100000L, 50000L, 30000);
  }

  @Test
  void 無効なトークンで不動産更新をした時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    RealestateDetail detail = simpleCreateRealestateDetails(
        "ChangedProject", 99999L, 999999L, 9999);

    // 更新対象のユーザーIDとプロジェクトIDを設定
    detail.getProject().setUserId(userId);
    detail.getParcel().setUserId(userId);
    detail.getBuilding().setUserId(userId);
    detail.getIncomeAndExpenses().setUserId(userId);
    detail.getProject().setId(projectId);
    detail.getParcel().setProjectId(projectId);
    detail.getBuilding().setProjectId(projectId);
    detail.getIncomeAndExpenses().setProjectId(projectId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/updateRealestate", HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");

    // 不動産情報をAPIから取得し変更が反映されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    verifyRealestateDetail(filteredDetail, "村上市ボロ戸建", 100000L, 50000L, 30000);
  }

  @Test
  void 他のユーザーの不動産更新をした時に404エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 4; // 別ユーザー、adminのプロジェクトID

    RealestateDetail detail = simpleCreateRealestateDetails(
        "ChangedProject", 99999L, 999999L, 9999);

    // 更新対象のユーザーIDとプロジェクトIDを設定
    detail.getProject().setUserId(userId);
    detail.getParcel().setUserId(userId);
    detail.getBuilding().setUserId(userId);
    detail.getIncomeAndExpenses().setUserId(userId);
    detail.getProject().setId(projectId);
    detail.getParcel().setProjectId(projectId);
    detail.getBuilding().setProjectId(projectId);
    detail.getIncomeAndExpenses().setProjectId(projectId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/updateRealestate", HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains("更新対象のプロジェクトが存在しません");

    // adminの不動産情報をAPIから取得し変更が反映されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("admin", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    verifyRealestateDetail(filteredDetail, "南四日町戸建", 1000000L, 500000L, 50000);
  }

  @Test
  void 各不動産詳細情報プロジェクトIDが一致せずに不動産更新をした時に400エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;
    int differentProjectId = 6;

    RealestateDetail detail = simpleCreateRealestateDetails(
        "ChangedProject", 99999L, 999999L, 9999);

    // 更新対象のユーザーIDとプロジェクトIDを設定
    detail.getProject().setUserId(userId);
    detail.getParcel().setUserId(userId);
    detail.getBuilding().setUserId(userId);
    detail.getIncomeAndExpenses().setUserId(userId);
    detail.getProject().setId(projectId);
    detail.getParcel().setProjectId(projectId);
    detail.getBuilding().setProjectId(differentProjectId); // user1のプロジェクトIDだが更新対象と一致しないプロジェクトIDを設定
    detail.getIncomeAndExpenses().setProjectId(projectId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(detail, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/updateRealestate", HttpMethod.PUT, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("プロジェクトIDが一致していません。");

    // 不動産情報をAPIから取得し変更が反映されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail1 = filterByProjectId(detailList, projectId);
    verifyRealestateDetail(filteredDetail1, "村上市ボロ戸建", 100000L, 50000L, 30000);

    // 建物情報に設定したプロジェクトIDの不動産情報も変更が反映されていないことを検証
    List<RealestateDetail> filteredDetail2 = filterByProjectId(detailList, differentProjectId);
    verifyRealestateDetail(filteredDetail2, "日興パレス長岡", 0L, 1700000L, 30000);
  }

  @Test
  void 不動産削除が正常に行えること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/deleteRealestate/" + projectId, HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // 不動産情報をAPIから取得し対象プロジェクトが削除されていることを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    assertThat(filteredDetail).isEmpty();
  }

  @Test
  void トークンなしで不動産削除をした時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    HttpHeaders headers = new HttpHeaders();
    // headers.setBearerAuth(token); 認証トークンをヘッダーに付与しない
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/deleteRealestate/" + projectId, HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("Authorizationヘッダーが存在しません");

    // 不動産情報をAPIから取得し対象プロジェクトが削除されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    assertThat(filteredDetail).isNotEmpty();
  }

  @Test
  void 無効なトークンで不動産削除をした時に401エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 3;

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("DummyToken");
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/deleteRealestate/" + projectId, HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).contains("JWTトークンが無効または期限切れです");

    // 不動産情報をAPIから取得し対象プロジェクトが削除されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("user1", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    assertThat(filteredDetail).isNotEmpty();
  }

  @Test
  void 他のユーザーの不動産削除をした時に404エラーが返ること() {

    String token = performLogin("user1", "password123");
    userId = jwtUtil.getUserIdFromToken(token);
    projectId = 4; // 別ユーザー、adminのプロジェクトID

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        "/deleteRealestate/" + projectId, HttpMethod.DELETE, entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).contains("削除対象のプロジェクトが存在しません。");

    // 不動産情報をAPIから取得し対象プロジェクトが削除されていないことを検証
    List<RealestateDetail> detailList = getRealestateDetails("admin", "password123", "");
    List<RealestateDetail> filteredDetail = filterByProjectId(detailList, projectId);
    assertThat(filteredDetail).isNotEmpty();
  }

  /**
   * ユーザー名とパスワードから認証しトークンを取得
   *
   * @param username ユーザー名
   * @param password パスワード
   * @return トークン
   */
  private String performLogin(String username, String password) {
    LoginRequest loginRequest = new LoginRequest(username, password);
    ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/auth/login",
        loginRequest, LoginResponse.class);
    return loginResponse.getBody().getToken();
  }

  /**
   * 指定ユーザーでログイン後不動産詳細情報リストの取得
   *
   * @param username ユーザー名
   * @param password パスワード
   * @param params   検索パラメーター
   * @return 不動産詳細情報リスト
   */
  private List<RealestateDetail> getRealestateDetails(String username, String password,
      String params) {
    String token = performLogin(username, password);
    userId = jwtUtil.getUserIdFromToken(token);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<RealestateDetail> entity = new HttpEntity<>(headers);

    ResponseEntity<List<RealestateDetail>> response = restTemplate.exchange(
        "/searchRealestate?" + params, HttpMethod.GET, entity,
        new ParameterizedTypeReference<List<RealestateDetail>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<RealestateDetail> detailList = response.getBody();
    return detailList;
  }

  /**
   * 不動産詳細情報リストを指定のプロジェクト名でフィルタリング
   *
   * @param detailList  不動産詳細情報リスト
   * @param projectName フィルタリングするプロジェクト名
   * @return フィルタリングされた不動産詳細情報リスト
   */
  private List<RealestateDetail> filterByProjectName(List<RealestateDetail> detailList,
      String projectName) {
    return detailList.stream()
        .filter(detail -> projectName.equals(detail.getProject().getProjectName()))
        .collect(Collectors.toList());
  }

  /**
   * 不動産詳細情報リストを指定のプロジェクトIDでフィルタリング
   *
   * @param detailList 不動産詳細情報リスト
   * @param projectId  フィルタリングするプロジェクトID
   * @return フィルタリングされた不動産詳細情報リスト
   */
  private List<RealestateDetail> filterByProjectId(List<RealestateDetail> detailList,
      int projectId) {
    return detailList.stream()
        .filter(detail -> Objects.equals(detail.getProject().getId(), projectId))
        .collect(Collectors.toList());
  }

  /**
   * 不動産詳細情報リスト内に指定した値が入っているかを検証
   *
   * @param detailList    検証する不動産詳細情報対象
   * @param projectName   プロジェクト名
   * @param parcelPrice   土地価格
   * @param buildingPrice 建物価格
   * @param rent          月収入
   */
  private static void verifyRealestateDetail(List<RealestateDetail> detailList, String projectName,
      long parcelPrice, long buildingPrice, int rent) {
    assertThat(detailList).isNotNull();
    assertThat(detailList).extracting(actual -> actual.getProject().getProjectName())
        .containsOnly(projectName);
    assertThat(detailList).extracting(actual -> actual.getParcel().getParcelPrice())
        .containsOnly(parcelPrice);
    assertThat(detailList).extracting(actual -> actual.getBuilding().getBuildingPrice())
        .containsOnly(buildingPrice);
    assertThat(detailList).extracting(actual -> actual.getIncomeAndExpenses().getRent())
        .containsOnly(rent);
  }

  /**
   * 取得した不動産詳細情報が全て自身のユーザーIDであるかを検証
   *
   * @param detailList 不動産詳細情報リスト
   */
  private void verifyUserIdMatch(List<RealestateDetail> detailList) {

    assertThat(detailList).isNotNull();

    assertThat(detailList).extracting(actual -> actual.getProject().getUserId())
        .containsOnly(userId);
    assertThat(detailList).extracting(actual -> actual.getParcel().getUserId())
        .containsOnly(userId);
    assertThat(detailList).extracting(actual -> actual.getBuilding().getUserId())
        .containsOnly(userId);
    assertThat(detailList).extracting(actual -> actual.getIncomeAndExpenses().getUserId())
        .containsOnly(userId);
  }

  /**
   * 不動産詳細情報を簡単に生成
   *
   * @param projectName   プロジェクト名
   * @param parcelPrice   土地価格
   * @param buildingPrice 建物価格
   * @param rent          月収入
   * @return 不動産詳細情報
   */
  private static RealestateDetail simpleCreateRealestateDetails(String projectName,
      long parcelPrice, long buildingPrice, int rent) {

    RealestateDetail detail = new RealestateDetail(
        new Project(), new Parcel(), new Building(), new IncomeAndExpenses());

    detail.getProject().setProjectName(projectName);
    detail.getParcel().setParcelPrice(parcelPrice);
    detail.getBuilding().setBuildingPrice(buildingPrice);
    detail.getIncomeAndExpenses().setRent(rent);
    return detail;
  }

}

