package com.openenglish.hr.persistence.entity;


import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "person")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne
    @JoinColumn(name = "workinglevel_id", referencedColumnName = "id")
    private Level workingLevel;
}
