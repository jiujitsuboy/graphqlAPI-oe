package com.openenglish.pp.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDetailDto {
    private Long id;
    private Long salesforcePurchaserId;
}
