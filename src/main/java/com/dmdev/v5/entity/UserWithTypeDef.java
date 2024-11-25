package com.dmdev.v5.entity;

import com.dmdev.v2.entity.Role;
import com.dmdev.v3.entity.Birthday;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

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
@TypeDef(name = "myCustomTypeName", typeClass = JsonBinaryType.class) // если класс не предоставил лаконичного названия (тут в качестве примера)
public class UserWithTypeDef {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private Birthday birthDate;

    // вместо того чтобы самостоятельно реализовывать UserType или Type
    @Type(type = "myCustomTypeName")
    private String info;

    @Enumerated(STRING)
    private Role role;
}
