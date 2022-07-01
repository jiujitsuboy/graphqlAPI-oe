package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonOldestActivityDto {
    private long personId;
    private String firstname;
    private String lastname;
    private String contactId;
    private String earliestActivity;
}
