package com.openenglish.hr.persistence.entity;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_detail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class PersonDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_detail_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @Column(name = "salesforce_purchaser_id")
    private Long salesforcePurchaserId;
}
