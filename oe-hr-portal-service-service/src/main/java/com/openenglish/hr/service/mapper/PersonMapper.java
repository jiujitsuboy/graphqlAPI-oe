package com.openenglish.hr.service.mapper;

import com.google.common.base.Preconditions;
import com.openenglish.hr.common.dto.LevelDto;
import com.openenglish.hr.common.dto.PersonDetailDto;
import com.openenglish.hr.common.dto.PersonDto;
import com.openenglish.hr.persistence.entity.Person;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PersonMapper {

    public PersonDto toModel(Person person) {

        PersonDto personDto = null;
        LevelDto levelDto = null;

        if (person != null) {

            if (person.getWorkingLevel() != null) {
                levelDto = LevelDto.builder()
                        .id(person.getWorkingLevel().getId())
                        .name(person.getWorkingLevel().getName())
                        .description(person.getWorkingLevel().getDescription())
                        .active(person.getWorkingLevel().isActive())
                        .numLiveRequired(person.getWorkingLevel().getNumLiveRequired())
                        .numImmersionRequired(person.getWorkingLevel().getNumImmersionRequired())
                        .sequence(person.getWorkingLevel().getSequence())
                        .lowScoreBoundary(person.getWorkingLevel().getLowScoreBoundary())
                        .highScoreBoundary(person.getWorkingLevel().getHighScoreBoundary())
                        .levelNum(person.getWorkingLevel().getLevelNum())
                        .build();
            }

            personDto = PersonDto.builder()
                    .id(person.getId())
                    .contactId(person.getContactId())
                    .details(person.getDetails().stream().map(detail -> PersonDetailDto.builder()
                            .detailsId(detail.getDetailsId())
                            .purchaserId(detail.getSalesforcePurchaserId())
                            .build()).collect(Collectors.toSet()))
                    .workingLevel(levelDto)
                    .build();
        }

        return personDto;
    }
}
