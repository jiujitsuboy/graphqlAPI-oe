package com.openenglish.hr.persistence.entity;


import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "coursetype")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class CourseType implements Serializable {

  private static long serialVersionUID = -14345342887212L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "type")
  private String type;
}
