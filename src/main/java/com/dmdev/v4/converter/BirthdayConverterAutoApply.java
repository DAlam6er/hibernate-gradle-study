package com.dmdev.v4.converter;

import com.dmdev.v3.entity.Birthday;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.util.Optional;

@Converter(autoApply = true)
public class BirthdayConverterAutoApply implements AttributeConverter<Birthday, Date> {
    @Override
    public Date convertToDatabaseColumn(Birthday attribute) {
        return Optional.ofNullable(attribute)
                .map(Birthday::birthDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public Birthday convertToEntityAttribute(Date dbDate) {
        return Optional.ofNullable(dbDate)
                .map(Date::toLocalDate)
                .map(Birthday::new)
                .orElse(null);
    }
}
