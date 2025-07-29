package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "不動産検索パラメーター")
public class SearchParams {

  @Schema(description = "プロジェクト名", example = "ABCアパート計画")
  private String searchProjectName;
  @Schema(description = "住所", example = "東京都港南区")
  private String searchParcelAddress;
  @Schema(description = "建物種別", example = "アパート")
  private String searchBuildingType;
  @Schema(description = "建物構造", example = "鉄筋コンクリート")
  private String searchBuildingStructure;

}
