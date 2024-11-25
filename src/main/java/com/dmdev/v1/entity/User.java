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
 * <p>
 * POJO:
 * <ul>
 *     <li>все поля сущности должны быть закрытыми (private)</li>
 *     <li>должны существовать геттеры/сеттеры к полям</li>
 *     <li>сущность должна быть изменяемой (mutable)</li>
 *     <li>класс должен быть изменяемым (mutable) - CGLIB</li>
 *     <li>должен существовать конструктор без параметров</li>
 *     <li>должен существовать полный конструктор</li>
 *     <li>должны существовать методы toString(), equals() и hashCode()</li>
 * </ul>
 * <p>
 * Наличие JPA-аннотации @Entity:
 * <ul>
 *     <li>данный POJO является Hibernate entity</li>
 * </ul>
 * <p>
 * Наличие JPA-аннотации @Id
 * <ul>
 *     <li>обязательность первичного ключа</li>
 *     <li>класс поля, отмеченного аннотацией, обязан реализовывать интерфейс Serializable</li>
 * </ul>
 */
@Data // @Getter + @Setter + @RequiredArgsConstructor + @ToString + @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder // для красивого и удобного создания и инициализации сущностей
@Entity
// По умолчанию Hibernate берёт название класса/полей в качестве названия таблицы/колонок в БД (SQL не чувствителен к регистру)
@Table(schema = "public", name = "users_v1")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date") // позволяет передать большое количество метаинформации
    private LocalDate birthDate;
    private Integer age;
}
