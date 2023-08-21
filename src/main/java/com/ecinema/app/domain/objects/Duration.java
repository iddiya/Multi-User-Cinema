package com.ecinema.app.domain.objects;

import com.ecinema.app.exceptions.InvalidDurationException;
import com.ecinema.app.util.UtilMethods;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents time duration in hours and minutes.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Duration implements Comparable<Duration>, Serializable {

    private Integer hours;
    private Integer minutes;

    private Duration(Integer hours, Integer minutes)
            throws InvalidDurationException {
        setHours(hours);
        setMinutes(minutes);
    }

    /**
     * Returns a new Duration with {@link #hours} and {@link #minutes} set to zero.
     *
     * @return the new Duration
     */
    public static Duration zero() {
        return Duration.of(0, 0);
    }

    /**
     * Returns a new Duration with {@link #hours} and {@link #minutes} set to the provided args.
     *
     * @param hours   the hours
     * @param minutes the minutes
     * @return the new Duration
     * @throws InvalidDurationException if either of the provided integer args are less than zero
     */
    public static Duration of(Integer hours, Integer minutes)
            throws InvalidDurationException {
        Duration duration = new Duration();
        duration.set(hours, minutes);
        return duration;
    }

    /**
     * Return a new Duration with {@link #hours} and {@link #minutes} values set to those
     * of the provided Duration.
     *
     * @param duration the Duration whose values are to be copied into the return Duration
     * @return new Duration with values equal to those of the provided Duration
     */
    public static Duration of(Duration duration) {
        return of(duration.getHours(), duration.getMinutes());
    }

    /**
     * Convert string representation of Duration into new Duration instance. String representation
     * is hours:minutes. See {@link #toString()}.
     *
     * @param str the String to convertToDto to a new Duration instance.
     * @return the new Duration instance.
     * @throws InvalidDurationException see {@link #setHours(Integer)} and {@link #setMinutes(Integer)}.
     */
    public static Duration strToDuration(String str)
            throws InvalidDurationException {
        String[] tokens = str.split(":");
        if (tokens.length != 2 || !UtilMethods.isDigitsOnly(tokens[0]) || !UtilMethods.isDigitsOnly(tokens[1])) {
            throw new InvalidDurationException("String cannot be converted to Duration");
        }
        Integer hours = Integer.parseInt(tokens[0]);
        Integer minutes = Integer.parseInt(tokens[1]);
        return new Duration(hours, minutes);
    }

    /**
     * Returns a {@link Duration} with a random value of {@link #hours} between 0 and 4,
     * and a random value of {@link #minutes} between 0 and 59.
     *
     * @return new {@link Duration} instance.
     */
    public static Duration randomDuration() {
        int hours = UtilMethods.randomIntBetween(0, 4);
        int minutes = UtilMethods.randomIntBetween(0, 59);
        return new Duration(hours, minutes);
    }

    /**
     * Sets the value of hours.
     *
     * @param hours the hours to assign to {@link #hours}.
     * @throws InvalidDurationException thrown if the value of hours is less than zero.
     */
    public void setHours(Integer hours)
            throws InvalidDurationException {
        if (hours < 0) {
            throw new InvalidDurationException("Hours cannot be less than 0");
        }
        this.hours = hours;
    }

    /**
     * Sets minutes. While minutes is greater than or equal to 60, the following will be performed:
     * <p>
     * {@code
     * while (minutes >= 60) {
     * minutes -= 60;
     * hours++;
     * }
     * }*
     *
     * @param minutes the minutes to assign to this.minutes.
     * @throws InvalidDurationException the invalid duration exception
     */
    public void setMinutes(Integer minutes)
            throws InvalidDurationException {
        if (minutes < 0) {
            throw new InvalidDurationException("Minutes cannot be less than 0");
        }
        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }
        this.minutes = minutes;
    }

    /**
     * Sets the values of {@link #hours} and {@link #minutes}.
     *
     * @param hours   the hours
     * @param minutes the minutes
     * @throws InvalidDurationException the invalid duration exception
     */
    public void set(Integer hours, Integer minutes)
            throws InvalidDurationException {
        setHours(hours);
        setMinutes(minutes);
    }

    /**
     * Sets the values of {@link #hours} and {@link #minutes} to those of the provided Duration.
     *
     * @param duration the Duration whose values are to be copied
     */
    public void set(Duration duration) {
        set(duration.getHours(), duration.getMinutes());
    }

    @Override
    public String toString() {
        if (minutes < 10) {
            return hours + ":0" + minutes;
        } else {
            return hours + ":" + minutes;
        }
    }

    @Override
    public int compareTo(Duration o) {
        int comparison = hours.compareTo(o.getHours());
        if (comparison == 0) {
            comparison = minutes.compareTo(o.getMinutes());
        }
        return comparison;
    }

    @Override
    public int hashCode() {
        int hash = 21;
        hash += hours * 7;
        hash += minutes * 7;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Duration duration &&
                hours.equals(duration.getHours()) &&
                minutes.equals(duration.getMinutes());
    }

}
