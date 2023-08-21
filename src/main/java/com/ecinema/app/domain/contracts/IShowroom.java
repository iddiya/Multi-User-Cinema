package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.Letter;

/**
 * The interface Showroom.
 */
public interface IShowroom {

    /**
     * Gets showroom letter.
     *
     * @return the showroom letter
     */
    Letter getShowroomLetter();

    /**
     * Sets showroom letter.
     *
     * @param showroomLetter the showroom letter
     */
    void setShowroomLetter(Letter showroomLetter);

    /**
     * Gets number of rows.
     *
     * @return the number of rows
     */
    Integer getNumberOfRows();

    /**
     * Sets number of rows.
     *
     * @param numberOfRows the number of rows
     */
    void setNumberOfRows(Integer numberOfRows);

    /**
     * Gets number of seats per row.
     *
     * @return the number of seats per row
     */
    Integer getNumberOfSeatsPerRow();

    /**
     * Sets number of seats per row.
     *
     * @param numberOfSeatsPerRow the number of seats per row
     */
    void setNumberOfSeatsPerRow(Integer numberOfSeatsPerRow);

}
