package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IReview;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Data
public class ReviewForm implements IReview, Serializable {
    private Long userId;
    private Long movieId;
    private String review;
    private Integer rating;
}
