package com.openenglish.hr.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Data
public class Course implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name="coursetype_id")
  private CourseType courseType;

  @ManyToOne
  @JoinColumn(name="level_id")
  private Level level;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "thumbnail")
  private String thumbnail;

  @Column(name = "required")
  private boolean required;

  @Column(name = "mediadata")
  private String mediadata;

  @Column(name = "source_id")
  private Long sourceId;

  @Column(name = "contenttype_id")
  private Long contentTypeId;

  @Column(name = "featured")
  private boolean featured;

  @Column(name = "largethumbnail")
  private String largeThumbnail;

  @Column(name = "url")
  private String url;

  @Column(name = "transcript")
  private String transcript;

  @Column(name="lessonnumbertext")
  private String lessonNumber;

  @Column(name = "sequence")
  private int sequence;

  @ManyToOne
  private Unit unit;

  @Column(name = "published")
  private boolean published;

  @Column(name = "is_new")
  private boolean isNew;

  @Column(name = "original_course_id")
  private Long originalCourseId;
}