package com.openenglish.hr.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "personcourseaudit")
public class PersonCourseAudit implements Serializable {

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

  @Column(name = "datecompleted", insertable = false, updatable = false)
  private LocalDateTime dateCompleted;

  @Column(name = "datestarted", insertable = false, updatable = false)
  private LocalDateTime dateStarted;

  @Column(name = "timeontask")
  private int timeontask;

  @Column(name = "loggedinuserinfo_uuid")
  private String loggedInUserInfoUUID;
}