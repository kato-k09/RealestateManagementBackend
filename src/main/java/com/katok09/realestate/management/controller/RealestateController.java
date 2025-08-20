package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import com.katok09.realestate.management.service.RealestateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 不動産関係のREST APIエンドポイントを提供するコントローラー
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000") // Reactのポートに合わせる
@Validated
public class RealestateController {

  private final RealestateService service;

  public RealestateController(RealestateService service) {
    this.service = service;
  }

  /**
   * 不動産情報の一覧表示・検索を行います。
   *
   * @param searchParams 不動産検索パラメーターDTO。各フィールドがnullの場合はそのフィールドでの検索は行われません。
   * @return エラーが発生しなければ200 OKとともに不動産情報のリストを返します。
   */
  @GetMapping("/searchRealestate")
  public ResponseEntity<List<RealestateDetail>> searchRealestate(
      @Valid @ModelAttribute SearchParams searchParams, HttpServletRequest request) {

    List<RealestateDetail> result = service.searchRealestate(searchParams, request);

    return ResponseEntity.ok(result);
  }

  /**
   * 不動産情報の登録を行います。
   *
   * @param request 不動産登録情報
   * @return 登録成功のメッセージ
   */
  @PostMapping("/registerRealestate")
  public ResponseEntity<String> createProject(@Valid @RequestBody RealestateDetail request,
      HttpServletRequest request_token) {

    service.registerRealestate(request, request_token);

    return ResponseEntity.ok("登録成功");
  }

  /**
   * 不動産登録の更新を行います。
   *
   * @param request 不動産更新情報
   * @return 更新成功のメッセージ
   */
  @PutMapping("/updateRealestate")
  public ResponseEntity<String> updateRealestate(@Valid @RequestBody RealestateDetail request,
      HttpServletRequest request_token) {

    service.updateRealestate(request, request_token);

    return ResponseEntity.ok("更新成功");
  }

  /**
   * 不動産情報の削除を行います。
   *
   * @param id 不動産情報のID
   * @return 削除成功のメッセージ
   */
  @DeleteMapping("/deleteRealestate/{id}")
  public ResponseEntity<String> deleteRealestate(@PathVariable int id,
      HttpServletRequest request_token) {

    service.deleteRealestate(id, request_token);

    return ResponseEntity.ok("削除成功");
  }
}
