package com.ecinema.app.domain.contracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * A dto class is a POJO that relays information from the persistence layer to the view layer
 * without having to relay the persistent entity objects themselves. This results in segregation
 * of the view layer and business layer, thereby not having one coupled to the changes of the other.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class AbstractDto implements Serializable {
    private Long id = null;
}
