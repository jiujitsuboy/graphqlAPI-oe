package com.openenglish.pp.persistence.entity;


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

    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "email")
    private String email;

    @Column(name = "contactid")
    private String contactId;

    @OneToOne(mappedBy = "person")
    private PersonDetail details;
}
