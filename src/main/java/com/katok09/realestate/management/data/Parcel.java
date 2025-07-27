package com.katok09.realestate.management.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parcel {

  private int id;
  private int projectId;
  private int parcelPrice;
  private String parcelAddress;
  private String parcelCategory;
  private double parcelSize;
  private String parcelRemark;
  private boolean isDeleted;

}
