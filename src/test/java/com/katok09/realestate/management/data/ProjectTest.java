package com.katok09.realestate.management.data;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ProjectTest {

  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void setterとgetterが正常に動作すること() {
    Project actual = new Project();
    actual.setId(999);
    actual.setUserId(999);
    actual.setProjectName("DummyProject");
    actual.setDeleted(false);

    assertThat(actual.getId()).isEqualTo(999);
    assertThat(actual.getUserId()).isEqualTo(999);
    assertThat(actual.getProjectName()).isEqualTo("DummyProject");
    assertThat(actual.isDeleted()).isFalse();
  }

  @Test
  void プロジェクト名が100文字の時入力チェックに異常が発生しないこと() {
    Project project = new Project();
    project.setId(999);
    project.setUserId(999);
    project.setProjectName("a".repeat(100));
    project.setDeleted(false);

    Set<ConstraintViolation<Project>> actual = validator.validate(project);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  void プロジェクト名が101文字の時入力チェックに異常が発生すること() {
    Project project = new Project();
    project.setId(999);
    project.setUserId(999);
    project.setProjectName("a".repeat(101));
    project.setDeleted(false);

    Set<ConstraintViolation<Project>> actual = validator.validate(project);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("プロジェクト名は100字以内で入力してください。");
  }

  @Test
  void プロジェクト名がnullの時入力チェックに異常が発生すること() {
    Project project = new Project();
    project.setId(999);
    project.setUserId(999);
    project.setProjectName(null);
    project.setDeleted(false);

    Set<ConstraintViolation<Project>> actual = validator.validate(project);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("プロジェクト名を入力してください。");
  }

  @Test
  void プロジェクト名が空の時入力チェックに異常が発生すること() {
    Project project = new Project();
    project.setId(999);
    project.setUserId(999);
    project.setProjectName("");
    project.setDeleted(false);

    Set<ConstraintViolation<Project>> actual = validator.validate(project);

    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual).extracting("message")
        .containsOnly("プロジェクト名を入力してください。");
  }

}
