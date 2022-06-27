package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LicenseAssigneeDto {
  private String firstName;
  private String lastName;
  private String email;

}
