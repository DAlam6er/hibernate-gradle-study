package com.dmdev.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Hibernate Entity = POJO + @Entity + @Id
 */
@Data // @Getter + @Setter + @RequiredArgsConstructor + @ToString + @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder // для красивого и удобного создания и инициализации сущностей
@Entity // JPA
// По умолчанию Hibernate берёт название класса/полей в качестве названия таблицы/колонок в БД (SQL не чувствителен к регистру)
@Table(schema = "public", name = "users")
public class User {
    @Id // JPA
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date") // позволяет передать большое количество метаинформации
    private LocalDate birthDate;
    private Integer age;
}
