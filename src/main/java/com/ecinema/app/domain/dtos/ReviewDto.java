package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.IReview;
import com.ecinema.app.domain.entities.ReviewVote;
import com.ecinema.app.domain.enums.Vote;
import com.ecinema.app.util.UtilMethods;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The type Review dto.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReviewDto extends AbstractDto implements IReview {

    private String writer = "";
    private String review = "";
    private Integer rating = 0;
    private Long userId = null;
    private Long customerId = null;
    private Boolean isCensored = false;
    private LocalDateTime creationDateTime;
    private List<Long> upvoteUserIds = new ArrayList<>();
    private List<Long> downvoteUserIds = new ArrayList<>();

    /**
     * Creation date time formatted string.
     *
     * @return the string
     */
    public String creationDateTimeFormatted() {
        return UtilMethods.localDateTimeFormatted(creationDateTime);
    }

}
