package com.dmdev.v1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Эта сущность отсутствует в теге mapping аттрибута class файла hibernate.cfg.xml
 * для демонстрации динамического добавления сущности для отслеживания
 * <p>
 * Здесь также отсутствует аннотация @Column(name = "birth_date") над полем birthDate
 * для демонстрации установки глобальной стратегию именования колонок таблицы (CamelCaseToUnderscoresNamingStrategy)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "public", name = "users_v1")
public class UserNotInConfigFileAndWithoutColumnAnnotation {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    private LocalDate birthDate;
    private Integer age;
}
