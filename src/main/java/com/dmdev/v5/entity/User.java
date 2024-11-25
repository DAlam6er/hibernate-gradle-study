package com.dmdev.v5.entity;

import com.dmdev.v3.entity.Birthday;
import com.dmdev.v2.entity.Role;
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
@Table(schema = "public", name = "users_v4")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private Birthday birthDate;

    // вместо того чтобы самостоятельно реализовывать UserType или Type
    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
    private String info;

    @Enumerated(STRING)
    private Role role;

    public User withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public User withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public User withBirthDate(Birthday birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public User withInfo(String info) {
        this.info = info;
        return this;
    }

    public User withRole(Role role) {
        this.role = role;
        return this;
    }
}
