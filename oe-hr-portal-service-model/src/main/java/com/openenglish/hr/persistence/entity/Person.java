package com.openenglish.hr.persistence.entity;


import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
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

    @ManyToOne
    @JoinColumn(name = "workinglevel_id", referencedColumnName = "id")
    private Level workingLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(email, person.email) && Objects.equals(contactId, person.contactId) && Objects.equals(details, person.details) && Objects.equals(workingLevel, person.workingLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, contactId, details, workingLevel);
    }
}
