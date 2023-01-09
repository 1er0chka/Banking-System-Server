package com.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue()
    @Column(name = "id")
    private int id;  // ID пользователя
    @Column(name = "login")
    private String login;  // логин пользователя
    @Column(name = "password")
    private String password;  // пароль пользователя
    @Column(name = "role")
    private String role;  // роль пользователя
    @Column(name = "surname")
    private String surname;  // фамилия пользователя
    @Column(name = "name")
    private String name;  // имя пользователя
    @Column(name = "secondName")
    private String secondName;  // отчество пользователя
    @Column(name = "dateOfBirth")
    private Date dateOfBirth;  // дата рождения пользователя
    @Column(name = "image")
    private String image;  // персональное изображение пользователя
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Account> accounts;  // банковские счета пользователя
}
