package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDto {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactId;
    private String salesforcePurchaserId;
    private LevelDto workingLevel;
}
