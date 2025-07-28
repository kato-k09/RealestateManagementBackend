package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.service.RealestateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Reactのポートに合わせる
public class RealestateController {

  private RealestateService service;

  @Autowired
  public RealestateController(RealestateService service) {
    this.service = service;
  }

  /**
   * 不動産情報の一覧表示・検索を行います。
   *
   * @param searchParam
   * @return エラーが発生しなければ200 OKとともに不動産情報のリストを返します。
   */
  @GetMapping("/searchRealestate")
  public ResponseEntity<List<RealestateDetail>> searchRealestate(
      @ModelAttribute RealestateDetail searchParam) {

    List<RealestateDetail> result = service.searchRealestate(searchParam);

    return ResponseEntity.ok(result);
  }

  /**
   * 不動産情報の登録を行います。
   *
   * @param request 不動産登録情報
   * @return 登録成功のメッセージ
   */
  @PostMapping("/registerRealestate")
  public ResponseEntity<String> createProject(@RequestBody RealestateDetail request) {
    // ここでそれぞれのエンティティにアクセスできます
    service.registerRealestate(request);

    // 保存処理などを行う
    return ResponseEntity.ok("受信成功");
  }

  /**
   * 不動産登録の更新を行います。
   *
   * @param request 不動産更新情報
   * @return 更新成功のメッセージ
   */
  @PutMapping("/updateRealestate")
  public ResponseEntity<String> updateRealestate(@RequestBody RealestateDetail request) {

    service.updateRealestate(request);

    return ResponseEntity.ok("更新成功");
  }

  /**
   * 不動産情報の削除を行います。
   *
   * @param id 不動産情報のID
   * @return 削除成功のメッセージ
   */
  @DeleteMapping("/deleteRealestate/{id}")
  public ResponseEntity<String> deleteRealestate(@PathVariable int id) {

    service.deleteRealestate(id);

    return ResponseEntity.ok("削除成功");
  }
}
