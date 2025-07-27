package com.katok09.realestate.management.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Building {

  private int id;
  private int projectId;
  private int buildingPrice;
  private String buildingAddress;
  private String buildingAddressNumber;
  private String buildingType;
  private String buildingStructure;
  private double buildingSize;
  private LocalDate buildingDate;
  private String buildingRemark;
  private boolean isDeleted;

}
