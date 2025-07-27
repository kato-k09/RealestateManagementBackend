package com.katok09.realestate.management.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeAndExpenses {

  private int id;
  private int projectId;
  private int rent;
  private int maintenanceCost;
  private int repairFund;
  private int managementFee;
  private int principal;
  private int interest;
  private int tax;
  private int waterBill;
  private int electricBill;
  private int gasBill;
  private int fireInsurance;
  private String other;
  private boolean isDeleted;

}
