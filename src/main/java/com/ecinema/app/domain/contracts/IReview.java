package com.ecinema.app.domain.contracts;

/**
 * Defines the contract of the review.
 */
public interface IReview {

    /**
     * Sets the review text.
     *
     * @param review the review text
     */
    void setReview(String review);

    /**
     * Gets the review text.
     *
     * @return the review text
     */
    String getReview();

    /**
     * Sets the rating.
     *
     * @param rating the rating
     */
    void setRating(Integer rating);

    /**
     * Gets the rating.
     *
     * @return the rating
     */
    Integer getRating();

    /**
     * Sets the fields of this Review to those of the provided Review.
     *
     * @param review the other Review to fetch fields from
     */
    default void setToIReview(IReview review) {
        setReview(review.getReview());
        setRating(review.getRating());
    }

}
