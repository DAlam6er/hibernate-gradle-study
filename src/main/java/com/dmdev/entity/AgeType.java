package com.dmdev.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record AgeType(LocalDate birthDate) {
    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
}
