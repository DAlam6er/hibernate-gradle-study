package com.dmdev.entity;

import com.dmdev.converter.AgeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
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

    @Convert(converter = AgeConverter.class)
    @Column(name = "birth_date")
    private AgeType birthDate;
}
