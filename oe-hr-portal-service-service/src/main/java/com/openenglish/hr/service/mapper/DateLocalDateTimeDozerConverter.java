package com.openenglish.hr.service.mapper;

import org.dozer.DozerConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateLocalDateTimeDozerConverter extends DozerConverter<LocalDateTime, Date> {

    public DateLocalDateTimeDozerConverter() {
        super(LocalDateTime.class, Date.class);
    }

    @Override
    public Date convertTo(LocalDateTime localDateTime, Date date) {
        if(localDateTime == null){
            return null;
        }
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public LocalDateTime convertFrom(Date date, LocalDateTime localDateTime) {
        if(date == null){
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
