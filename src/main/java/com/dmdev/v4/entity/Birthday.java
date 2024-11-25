package com.dmdev.v4.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Birthday(LocalDate birthDate) {
    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
}
