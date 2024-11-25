package com.dmdev.converter;

import com.dmdev.entity.AgeType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.util.Optional;

@Converter(autoApply = true)
public class AgeConverter implements AttributeConverter<AgeType, Date> {
    @Override
    public Date convertToDatabaseColumn(AgeType attribute) { // Converts AgeType into SQL column
        return Optional.ofNullable(attribute)
                .map(AgeType::birthDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public AgeType convertToEntityAttribute(Date dbData) { // Convert SQL column into our Class
        return Optional.ofNullable(dbData)
                .map(Date::toLocalDate)
                .map(AgeType::new)
                .orElse(null);
    }
}
