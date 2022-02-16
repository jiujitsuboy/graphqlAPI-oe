package com.openenglish.pp.service.mapper;

import com.openenglish.pp.common.dto.PersonDetailDto;
import com.openenglish.pp.common.dto.PersonDto;
import com.openenglish.pp.persistence.entity.Person;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PersonMapper {

    public PersonDto toModel(Person person){
       return PersonDto.builder()
               .id(person.getId())
               .contactId(person.getContactId())
               .details(person.getDetails().stream().map(detail->PersonDetailDto.builder()
                       .detailsId(detail.getDetailsId())
                       .purchaserId(detail.getSalesforcePurchaserId())
                       .build()).collect(Collectors.toSet()))
               .build();
    }
}
