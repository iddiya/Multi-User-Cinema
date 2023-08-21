package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.ISeat;
import com.ecinema.app.domain.enums.Letter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Showroom seat.
 */
@Getter
@Setter
@Entity
@ToString
public class ShowroomSeat extends AbstractEntity implements ISeat {

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private Showroom showroom;

    @Column
    @Enumerated(EnumType.STRING)
    private Letter rowLetter;

    @Column
    private Integer seatNumber;

    @ToString.Exclude
    @OneToMany(mappedBy = "showroomSeat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScreeningSeat> screeningSeats = new HashSet<>();

}
