package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.enums.Vote;
import lombok.*;

import javax.persistence.*;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Customer.
 */
@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
public class Customer extends AbstractUserAuthority {

    @Column
    private Integer tokens = 0;

    @ToString.Exclude
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "ticketOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Ticket> tickets = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "cardOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PaymentCard> paymentCards = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReviewVote> reviewVotes = new HashSet<>();

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Moderator censoredBy = null;

    @Override
    protected final UserAuthority defineUserRole() {
        return UserAuthority.CUSTOMER;
    }

    /**
     * Add tokens.
     *
     * @param tokens the tokens
     */
    public void addTokens(Integer tokens) {
        this.tokens += tokens;
    }

    /**
     * Subtract tokens.
     *
     * @param tokens the tokens
     */
    public void subtractTokens(Integer tokens) {
        this.tokens = Math.max(0, this.tokens - tokens);
    }

}
