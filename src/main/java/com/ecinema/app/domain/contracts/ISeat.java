package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.Letter;

import java.util.Comparator;

/**
 * The interface Seat.
 */
public interface ISeat {

    /**
     * The type Seat comparator.
     */
    class SeatComparator implements Comparator<ISeat> {

        @Override
        public int compare(ISeat o1, ISeat o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            if (o1.getRowLetter() == null || o2.getRowLetter() == null) {
                return 0;
            }
            int comparison = o1.getRowLetter().compareTo(o2.getRowLetter());
            if (comparison == 0 && o1.getSeatNumber() != null && o2.getSeatNumber() != null) {
                comparison = o1.getSeatNumber().compareTo(o2.getSeatNumber());
            }
            return comparison;
        }

        /**
         * Gets instance.
         *
         * @return the instance
         */
        public static SeatComparator getInstance() {
            return new SeatComparator();
        }

    }

    /**
     * Gets row letter.
     *
     * @return the row letter
     */
    Letter getRowLetter();

    /**
     * Gets seat number.
     *
     * @return the seat number
     */
    Integer getSeatNumber();

    /**
     * Seat designation string.
     *
     * @return the string
     */
    default String seatDesignation() {
        return getRowLetter() + "-" + getSeatNumber();
    }

}
