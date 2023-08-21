package com.ecinema.app.domain.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Customer dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomerDto extends UserAuthorityDto {
    private Long censorId = 0L;
    private Boolean isCensored = false;
}
