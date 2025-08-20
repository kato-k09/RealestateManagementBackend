package com.katok09.realestate.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.katok09.realestate.management.config.JwtRequestFilter;
import com.katok09.realestate.management.domain.RealestateDetail;
import com.katok09.realestate.management.dto.SearchParams;
import com.katok09.realestate.management.service.RealestateService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RealestateController.class)
public class RealestateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RealestateService service;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtRequestFilter jwtRequestFilter;

  @Test
  void 空の不動産詳細情報を取得できること() throws Exception {

    when(service.searchRealestate(any(SearchParams.class), any(HttpServletRequest.class)))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/searchRealestate"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchRealestate(any(SearchParams.class),
        any(HttpServletRequest.class));

  }

  @Test
  void 不動産詳細情報の登録ができ登録成功メッセージを受け取れること() throws Exception {

    doNothing().when(service)
        .registerRealestate(any(RealestateDetail.class), any(HttpServletRequest.class));

    mockMvc.perform(post("/registerRealestate")
            .contentType("application/json").content(
                """
                        {
                            "project": {
                                "userId": "999",
                                "projectName": "東三条AP",
                                "deleted": false
                            },
                            "parcel": {
                                "userId": "999",
                                "parcelPrice": 10000000,
                                "parcelAddress": "新潟県三条市",
                                "parcelCategory": "宅地",
                                "parcelSize": 452.65,
                                "parcelRemark": "",
                                "deleted": false
                            },
                            "building": {
                                "userId": "999",
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
                                "userId": "999",
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

    verify(service, times(1)).registerRealestate(any(RealestateDetail.class),
        any(HttpServletRequest.class));
  }

  @Test
  void 不動産詳細情報の更新ができ更新成功メッセージを受け取れること() throws Exception {

    doNothing().when(service)
        .updateRealestate(any(RealestateDetail.class), any(HttpServletRequest.class));

    mockMvc.perform(put("/updateRealestate")
            .contentType("application/json").content(
                """
                        {
                            "project": {
                                "id": 11,
                                "userId": "999",
                                "projectName": "東三条AP",
                                "deleted": false
                            },
                            "parcel": {
                                "id": 6,
                                "projectId": 11,
                                "userId": "999",
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
                                "userId": "999",
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
                                "userId": "999",
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

    verify(service, times(1)).updateRealestate(any(RealestateDetail.class),
        any(HttpServletRequest.class));
  }

  @Test
  void 不動産詳細情報の削除ができ削除成功メッセージを受け取れること() throws Exception {

    doNothing().when(service).deleteRealestate(eq(99), any(HttpServletRequest.class));

    mockMvc.perform(delete("/deleteRealestate/99"))
        .andExpect(status().isOk())
        .andExpect(content().string("削除成功"));

    verify(service, times(1)).deleteRealestate(eq(99), any(HttpServletRequest.class));
  }

}
