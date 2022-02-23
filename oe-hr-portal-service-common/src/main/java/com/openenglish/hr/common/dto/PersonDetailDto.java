package com.openenglish.hr.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDetailDto {
    private Long detailsId;
    private Long purchaserId;
}
