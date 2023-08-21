package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IScreening;
import com.ecinema.app.domain.contracts.ISeat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The type Screening.
 */
@Getter
@Setter
@Entity
@ToString
public class Screening extends AbstractEntity implements IScreening {

    @Column
    private LocalDateTime showDateTime;

    @Column
    private LocalDateTime endDateTime;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Showroom showroom;

    @ToString.Exclude
    @SortComparator(ISeat.SeatComparator.class)
    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SortedSet<ScreeningSeat> screeningSeats = new TreeSet<>(ISeat.SeatComparator.getInstance());

}
