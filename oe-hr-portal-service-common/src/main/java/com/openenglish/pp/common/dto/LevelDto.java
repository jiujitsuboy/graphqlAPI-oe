package com.openenglish.pp.common.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LevelDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private int numLiveRequired;
    private int numImmersionRequired;
    private int sequence;
    private BigDecimal lowScoreBoundary;
    private BigDecimal highScoreBoundary;
    private String levelNum;
}

