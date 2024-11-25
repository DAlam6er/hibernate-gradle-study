package com.dmdev.v4.converter;

import com.dmdev.v3.entity.Birthday;

import javax.persistence.AttributeConverter;
import java.sql.Date;
import java.util.Optional;

/**
 * Такие конвертеры на практике используют чаще всего, чтобы не реализовывать интерфейс Type или UserType
 * в кастомных пользовательских типах данных при преобразовании Java type <-> SQL type
 */
public class BirthdayConverter implements AttributeConverter<Birthday, Date> {
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
