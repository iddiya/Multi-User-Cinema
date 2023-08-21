package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.TicketStatus;
import com.ecinema.app.domain.enums.TicketType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * The type Ticket.
 */
@Getter
@Setter
@Entity
@ToString
public class Ticket extends AbstractEntity {

    @Column
    private LocalDateTime creationDateTime;

    @Column
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @Column
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer ticketOwner;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentCard paymentCard;

    @JoinColumn
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ScreeningSeat screeningSeat;

}
