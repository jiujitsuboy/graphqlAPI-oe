package com.openenglish.pp.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PersonDto {

    private Long id;
    private String contactId;
    private Set<PersonDetailDto> details;
    private LevelDto workingLevel;
}
