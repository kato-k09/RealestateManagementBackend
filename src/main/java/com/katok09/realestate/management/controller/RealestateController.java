package com.katok09.realestate.management.controller;

import com.katok09.realestate.management.data.Building;
import com.katok09.realestate.management.data.IncomeAndExpenses;
import com.katok09.realestate.management.data.Parcel;
import com.katok09.realestate.management.data.Project;
import com.katok09.realestate.management.domain.ProjectRequest;
import com.katok09.realestate.management.service.RealestateService;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Reactのポートに合わせる
public class RealestateController {

  private RealestateService service;

  @Autowired
  public RealestateController(RealestateService service) {
    this.service = service;
  }

  @GetMapping("/searchRealestate")
  public ResponseEntity<List<ProjectRequest>> searchRealestate(
      @ModelAttribute ProjectRequest searchParam) {

    List<ProjectRequest> result = service.searchRealestate(searchParam);

    return ResponseEntity.ok(result);
  }

  @PostMapping("/registerRealestate")
  public ResponseEntity<String> createProject(@RequestBody ProjectRequest request) {
    // ここでそれぞれのエンティティにアクセスできます
    service.registerRealestate(request);

    // 保存処理などを行う
    return ResponseEntity.ok("受信成功");
  }

  @PutMapping("/updateRealestate")
  public ResponseEntity<String> updateRealestate(@RequestBody ProjectRequest request) {

    service.updateRealestate(request);

    return ResponseEntity.ok("更新成功");
  }

  @DeleteMapping("/deleteRealestate/{id}")
  public ResponseEntity<String> deleteRealestate(@PathVariable int id) {

    service.deleteRealestate(id);

    return ResponseEntity.ok("削除成功");
  }
}
