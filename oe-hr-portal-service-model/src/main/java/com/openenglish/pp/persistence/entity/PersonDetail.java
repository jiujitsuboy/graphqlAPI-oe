package com.openenglish.pp.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person.detail_id")
    private Long detailsId;

    @Column(name = "salesforce_purchaser_id")
    private Long salesforcePurchaserId;
}
