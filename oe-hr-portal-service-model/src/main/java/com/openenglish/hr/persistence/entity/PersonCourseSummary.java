package com.openenglish.hr.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Data
@Table(name = "personcoursesummary")
public class PersonCourseSummary implements Serializable {

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
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
  private LocalDateTime lastDateCompleted;
}