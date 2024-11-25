package com.dmdev.v4.entity;

import com.dmdev.v3.entity.Birthday;
import com.dmdev.v2.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "user-with-birthday-converter-in-configuration")
@Table(schema = "public", name = "users_v3")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private Birthday birthDate;
    @Enumerated(STRING)
    private Role role;
}
