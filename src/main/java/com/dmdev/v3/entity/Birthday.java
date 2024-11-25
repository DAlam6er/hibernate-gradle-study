package com.dmdev.v3.entity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public record Birthday(LocalDate birthDate) {
    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now(ZoneOffset.UTC));
    }
}
