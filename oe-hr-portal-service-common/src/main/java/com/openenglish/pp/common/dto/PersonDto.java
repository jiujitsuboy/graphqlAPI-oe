package com.openenglish.pp.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PersonDto {

    private Long id;
    private String contactId;
    Set<PersonDetailDto> details;
}
