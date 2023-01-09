package com.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Builder
@Table(name = "card")
public class Card {
    @Id
    @Column(name = "id")
    @GeneratedValue()
    private int id;  // id карты
    @Column(name = "cardNumber")
    private String cardNumber;  // номер карты
    @Column(name = "validity")
    private String validity;  // срок действия карты
    @Column(name = "securityCode")
    private int securityCode;  // код карты
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "accountId", referencedColumnName = "id")
    private Account account;  // банковский счет
    @JsonIgnore
    @OneToMany(mappedBy = "card", fetch = FetchType.EAGER)
    private List<Operation> operations;  // совершенные операции пользователя
}
