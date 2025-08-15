package com.katok09.realestate.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "不動産検索パラメーター")
public class SearchParams {

  @Schema(description = "ユーザーID", example = "99")
  private int userId;
  @Schema(description = "プロジェクト名", example = "ABCアパート計画")
  @Size(max = 100, message = "プロジェクト名は100字以内で入力してください。")
  private String searchProjectName;
  @Schema(description = "住所", example = "東京都")
  @Size(max = 100, message = "住所は100字以内で入力してください。")
  private String searchParcelAddress;
  @Schema(description = "建物種別", example = "アパート")
  @Pattern(regexp = "^(|マンション|アパート|戸建て|店舗|事務所|その他)$", message = "建物種別は指定された選択肢から選んでください。")
  private String searchBuildingType;
  @Schema(description = "建物構造", example = "鉄筋コンクリート")
  @Pattern(regexp = "^(|鉄筋コンクリート造|鉄骨造|木造|軽量鉄骨造|その他)$", message = "建物構造は指定された選択肢から選んでください。")
  private String searchBuildingStructure;
  @Schema(description = "融資の有無", example = "true")
  private Boolean searchFinancing;

}
