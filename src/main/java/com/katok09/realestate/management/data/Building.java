package com.katok09.realestate.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "不動産建物情報")
@Getter
@Setter
public class Building {

  @Schema(description = "建物ID DB登録時に自動採番されます。", example = "99")
  private int id;
  @Schema(description = "プロジェクトID プロジェクトごとに紐づけをします。", example = "99")
  private int projectId;
  @Schema(description = "ユーザーID", example = "99")
  private int userId;
  @Schema(description = "建物価格", example = "1000000")
  @Min(value = 0L, message = "建物価格は0円以上で入力してください。")
  @Max(value = 1000000000000000L, message = "建物価格は1,000兆円以下で入力してください。")
  private long buildingPrice;
  @Schema(description = "建物種別", example = "アパート")
  @Pattern(regexp = "^(|マンション|アパート|戸建て|店舗|事務所|その他)$", message = "建物種別は指定された選択肢から選んでください。")
  private String buildingType;
  @Schema(description = "建物構造", example = "鉄筋コンクリート")
  @Pattern(regexp = "^(|鉄筋コンクリート造|鉄骨造|木造|軽量鉄骨造|その他)$", message = "建物構造は指定された選択肢から選んでください。")
  private String buildingStructure;
  @Schema(description = "建物面積", example = "100.11")
  @Min(value = 0, message = "建物面積は0㎡以上で入力してください。")
  @Max(value = 1000000000, message = "建物面積は10億㎡以下で入力してください。")
  private double buildingSize;
  @Schema(description = "建物築年", example = "2020-01-01")
  private LocalDate buildingDate;
  @Schema(description = "建物備考", example = "特になし")
  @Size(max = 100, message = "建物備考は100文字以下で入力してください。")
  private String buildingRemark;
  @Schema(description = "削除フラグ", example = "false")
  private boolean isDeleted;

  /**
   * 築年月日の入力を1000年1月1日から2999年12月31日の範囲に限定します。 範囲外の場合異常値としてバリデーションエラーとなります。
   *
   * @return 範囲内ならtrue、範囲外ならfalseが返ります。
   */
  @AssertTrue(message = "築年月日は1000年から2999年までの範囲で入力してください。")
  private boolean isBuildingDateValid() {
    if (buildingDate == null) {
      return true;
    }
    LocalDate minDate = LocalDate.of(1000, 1, 1);
    LocalDate maxDate = LocalDate.of(2999, 12, 31);
    return !buildingDate.isBefore(minDate) && !buildingDate.isAfter(maxDate);
  }

}
