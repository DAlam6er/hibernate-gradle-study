package com.dmdev.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "public")
@TypeDef(name = "dmdev", typeClass = JsonBinaryType.class)
public class User {

    // вариант с автогенерируемым первичным ключом
    /*
    @Id // первичный ключ БД
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hibernate не должен вставлять это поле в БД, т.к. ключ синтетический и автогенерируемый
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
//    @SequenceGenerator(name = "user_gen", sequenceName = "users_id_seq", allocationSize = 1) // sequence по умолчанию: hibernate_sequence
    @TableGenerator(name = "user_gen", table = "all_sequence",
        pkColumnName = "table_name", valueColumnName = "pk_value",
        allocationSize = 1)
    @GeneratedValue(generator = "user_gen", strategy = GenerationType.TABLE)
    private Long id;

    // аннотация для наглядности, чтобы не лезть в SQL скрипт
    // а также для возможности генерировать автоматически DDL
    @Column(unique = true)
    private String username;

    @Embedded
    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;
     */

    // вариант с составным первичным ключом
    @EmbeddedId
    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

    @Column(unique = true)
    private String username;

    //    @Type(type = "io.hypersistence.utils.hibernate.type.json.JsonBinaryType")
//    @Type(type = "jsonb")
    @Type(type = "dmdev")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;
//    private LocalDate birthDate;
//    private Integer age;
}
