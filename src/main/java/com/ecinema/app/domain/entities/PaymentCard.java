package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IAddress;
import com.ecinema.app.domain.contracts.IPaymentCard;
import com.ecinema.app.domain.enums.PaymentCardType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Payment card.
 */
@Getter
@Setter
@Entity
@ToString
public class PaymentCard extends AbstractEntity implements IPaymentCard {

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentCardType paymentCardType;

    @Column
    private String last4Digits;

    @Column
    private String cardNumber;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private LocalDate expirationDate;

    @Embedded
    private Address billingAddress = new Address();

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer cardOwner;

    @ToString.Exclude
    @OneToMany(mappedBy = "paymentCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Ticket> purchasedTickets = new HashSet<>();

    @Override
    public void setBillingAddress(IAddress billingAddress) {
        this.billingAddress.set(billingAddress);
    }

}
