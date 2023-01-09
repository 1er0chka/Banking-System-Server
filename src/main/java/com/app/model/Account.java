package com.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@EqualsAndHashCode
@Builder
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue()
    private int id;  // ID банковского счета
    @Column(name = "accountName")
    private String accountName;  // название банковского счета
    @Column(name = "accountNumber")
    private String accountNumber;  // номер банковского счета
    @Column(name = "amount")
    private double amount;  // сумма на банковском счету
    @Column(name = "dateOfCreate")
    private Date dateOfCreate;  // дата открытия банковского счета
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;  // пользователь-владелец счета
    @JsonIgnore
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private List<Card> cards;  // банковские карты счета
}
