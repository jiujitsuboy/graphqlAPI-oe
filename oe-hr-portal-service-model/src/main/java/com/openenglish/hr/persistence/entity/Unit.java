package com.openenglish.hr.persistence.entity;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "unit")
@Getter
public class Unit implements Serializable {

  private static final long serialVersionUID = -3551443645957159733L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "unitnumber")
  private String unitNumber;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "level_id")
  private Level level;
}