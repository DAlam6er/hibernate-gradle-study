package com.dmdev.entity;

import com.dmdev.converter.BirthdayConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class PersonalInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 9154080960028288028L;
    private String firstname;
    private String lastname;

    @Convert(converter = BirthdayConverter.class)
    @Column(name = "birth_date")
    private Birthday birthDate;
}
