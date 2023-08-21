package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.Vote;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * The type Review vote.
 */
@Entity
@Getter
@Setter
@ToString
public class ReviewVote extends AbstractEntity {

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer voter;

    @Column
    @Enumerated(EnumType.STRING)
    private Vote vote;

}
