package com.openenglish.pp.persistence.entity;


import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "person")
@Data
@Immutable
public class Person implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "contactid")
  private String contactId;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  private Set<PersonDetail> details;
}
