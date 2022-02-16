package com.openenglish.pp.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "active")
    private boolean active;
    @Column(name = "numliverequired")
    private int numLiveRequired;
    @Column(name = "numimmersionrequired")
    private int numImmersionRequired;
    @Column(name = "sequence")
    private int sequence;
    @Column(name = "lowscoreboundary")
    private BigDecimal lowScoreBoundary;
    @Column(name = "highscoreboundary")
    private BigDecimal highScoreBoundary;
    @Column(name = "levelnum")
    private String levelNum;

}
