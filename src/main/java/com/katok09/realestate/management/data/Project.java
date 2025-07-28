package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "不動産プロジェクト情報")
@Getter
@Setter
public class Project {
  @Schema(description = "プロジェクトID DB登録時に自動採番されます。", example = "99")
  private int id;
  @Schema(description = "プロジェクト名", example = "○○アパート")
  private String projectName;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

}
