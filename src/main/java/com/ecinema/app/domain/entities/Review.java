package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IReview;
import com.ecinema.app.domain.enums.Vote;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Review.
 */
@Getter
@Setter
@Entity
@ToString
public class Review extends AbstractEntity implements IReview {

    @Column(length = 2000)
    private String review;

    @Column
    private Integer rating;

    @Column
    private Boolean isCensored;

    @Column
    private LocalDateTime creationDateTime;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer writer;

    @ToString.Exclude
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReviewVote> reviewVotes = new HashSet<>();

}
