package com.dmdev.v2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "user-with-role")
@Table(schema = "public", name = "users_v2")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private LocalDate birthDate;
    private Integer age;
    @Enumerated(STRING) // по умолчанию используется ORDINAL
    private Role role;
}
