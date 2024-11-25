package com.dmdev.v5.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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
@Entity(name = "user-with-custom-type")
@Table(schema = "public", name = "users")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private Birthday birthDate;

    // вместо того, чтобы самостоятельно реализовывать UserType или Type
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    // либо
    // @Type(type = "jsonb")
    private String info;

    @Enumerated(STRING)
    private Role role;
}
