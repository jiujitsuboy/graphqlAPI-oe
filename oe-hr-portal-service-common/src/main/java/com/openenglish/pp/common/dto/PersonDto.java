package com.openenglish.pp.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PersonDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactId;
    PersonDetailDto details;
}
