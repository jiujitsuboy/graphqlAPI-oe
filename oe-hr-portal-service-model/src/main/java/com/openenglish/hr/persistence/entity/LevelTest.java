package com.openenglish.hr.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Builder
public class LevelTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_test_id")
    private Long levelTestId;

    @ManyToOne
    private Level level;

    @ManyToOne
    private Person person;

    @Column(name = "status")
    private String status;
    @Column(name = "test_session_id")
    private String testSessionId;
    @Column(name = "test_score_percentage")
    private BigDecimal testScorePercentage;
    @Column(name = "updated_date", insertable = false, updatable = false)
    private LocalDateTime updatedDate;
    @Column(name = "created_date", insertable = false, updatable = false)
    private LocalDateTime createdDate;
    @Column(name = "test_type")
    private String testType;

}
