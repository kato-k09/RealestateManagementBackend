package com.katok09.realestate.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.service.RealestateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RealestateController.class)
public class RealestateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RealestateService service;

  @Test
  void 空の不動産詳細情報を取得できること() throws Exception {

    mockMvc.perform(get("/searchRealestate"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchRealestate(any(RealestateDetail.class));

  }

  @Test
  void 不動産詳細情報の登録ができ登録成功メッセージを受け取れること() throws Exception {

    mockMvc.perform(post("/registerRealestate")
            .contentType("application/json").content(
                """
                        {
                            "project": {
                                "id": 11,
                                "projectName": "東三条AP",
                                "deleted": false
                            },
                            "parcel": {
                                "id": 6,
                                "projectId": 11,
                                "parcelPrice": 10000000,
                                "parcelAddress": "新潟県三条市",
                                "parcelCategory": "宅地",
                                "parcelSize": 452.65,
                                "parcelRemark": "",
                                "deleted": false
                            },
                            "building": {
                                "id": 2,
                                "projectId": 11,
                                "buildingPrice": 5000000,
                                "buildingAddress": "三条市東三条AP",
                                "buildingType": "アパート",
                                "buildingStructure": "木造",
                                "buildingSize": 150.32,
                                "buildingDate": "1988-06-09",
                                "buildingRemark": "",
                                "deleted": false
                            },
                            "incomeAndExpenses": {
                                "id": 1,
                                "projectId": 11,
                                "rent": 160000,
                                "maintenanceCost": 8000,
                                "repairFund": 0,
                                "managementFee": 0,
                                "principal": 85000,
                                "interest": 25000,
                                "tax": 0,
                                "waterBill": 0,
                                "electricBill": 5000,
                                "gasBill": 0,
                                "fireInsurance": 5000,
                                "other": "",
                                "deleted": false
                            }
                        }
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(content().string("登録成功"));

    verify(service, times(1)).registerRealestate(any(RealestateDetail.class));
  }

  @Test
  void 不動産詳細情報の更新ができ更新成功メッセージを受け取れること() throws Exception {
    mockMvc.perform(put("/updateRealestate")
            .contentType("application/json").content(
                """
                        {
                            "project": {
                                "id": 11,
                                "projectName": "東三条AP",
                                "deleted": false
                            },
                            "parcel": {
                                "id": 6,
                                "projectId": 11,
                                "parcelPrice": 10000000,
                                "parcelAddress": "新潟県三条市",
                                "parcelCategory": "宅地",
                                "parcelSize": 452.65,
                                "parcelRemark": "",
                                "deleted": false
                            },
                            "building": {
                                "id": 2,
                                "projectId": 11,
                                "buildingPrice": 5000000,
                                "buildingAddress": "三条市東三条AP",
                                "buildingType": "アパート",
                                "buildingStructure": "木造",
                                "buildingSize": 150.32,
                                "buildingDate": "1988-06-09",
                                "buildingRemark": "",
                                "deleted": false
                            },
                            "incomeAndExpenses": {
                                "id": 1,
                                "projectId": 11,
                                "rent": 160000,
                                "maintenanceCost": 8000,
                                "repairFund": 0,
                                "managementFee": 0,
                                "principal": 85000,
                                "interest": 25000,
                                "tax": 0,
                                "waterBill": 0,
                                "electricBill": 5000,
                                "gasBill": 0,
                                "fireInsurance": 5000,
                                "other": "",
                                "deleted": false
                            }
                        }
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新成功"));

    verify(service, times(1)).updateRealestate(any(RealestateDetail.class));
  }

  @Test
  void 不動産詳細情報の削除ができ削除成功メッセージを受け取れること() throws Exception {
    mockMvc.perform(delete("/deleteRealestate/11"))
        .andExpect(status().isOk())
        .andExpect(content().string("削除成功"));

    verify(service, times(1)).deleteRealestate(11);
  }

}
