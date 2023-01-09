package com.app.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Builder
@Table(name = "operation")
public class Operation {
    @Id
    @Column(name = "id")
    @GeneratedValue()
    private int id;  // ID операции
    @Column(name = "date")
    private Date date;  // дата совершения операции
    @Column(name = "time")
    private Time time;  // время совершения операции
    @Column(name = "name")
    private String name;  // название операции
    @Column(name = "sum")
    private double sum;  // сумма операции
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cardId", referencedColumnName = "id")
    private Card card;  // карта, с которой совершались операции
}