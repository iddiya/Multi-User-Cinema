package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.ISeat;
import com.ecinema.app.domain.contracts.IShowroom;
import com.ecinema.app.domain.enums.Letter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Showroom.
 */
@Getter
@Setter
@Entity
@ToString
public class Showroom extends AbstractEntity implements IShowroom {

    @Column
    @Enumerated(EnumType.STRING)
    private Letter showroomLetter;

    @Column
    private Integer numberOfRows;

    @Column
    private Integer numberOfSeatsPerRow;

    @ToString.Exclude
    @OneToMany(mappedBy = "showroom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Screening> screenings = new HashSet<>();

    @ToString.Exclude
    @SortComparator(ISeat.SeatComparator.class)
    @OneToMany(mappedBy = "showroom", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SortedSet<ShowroomSeat> showroomSeats = new TreeSet<>(ISeat.SeatComparator.getInstance());

    @Override
    public boolean equals(Object o) {
        return o instanceof Showroom showroom &&
                showroom.getShowroomLetter().equals(showroomLetter);
    }

    @Override
    public int hashCode() {
        return showroomLetter.hashCode();
    }

}

