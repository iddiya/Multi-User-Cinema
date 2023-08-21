package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.enums.Vote;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The type Review vote dto.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReviewVoteDto extends AbstractDto {
    private Vote vote = null;
    private Long userId = null;
    private Long voterId = null;
    private Long reviewId = null;
}
