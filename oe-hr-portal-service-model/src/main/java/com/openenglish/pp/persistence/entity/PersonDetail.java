package com.openenglish.pp.persistence.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_detail")
@Data
@Immutable
public class PersonDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person.detail_id")
    private Long detailsId;

    @Column(name = "purchaser_id")
    private Long purchaserId;
}
