package com.openenglish.hr.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Data
@Table(name = "personcoursesummary")
public class PersonCourseSummary implements Serializable {

  private static long serialVersionUID = -23454265465462L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name="course_id")
  private Course course;

  @ManyToOne
  @JoinColumn(name="person_id")
  private Person person;

  @Column(name = "lastdatecompleted", insertable = false, updatable = false)
  private LocalDateTime lastDateCompleted;

  @Column(name = "timeontask")
  private int timeontask;
}