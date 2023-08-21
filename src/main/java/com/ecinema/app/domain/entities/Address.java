package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IAddress;
import com.ecinema.app.domain.enums.UsState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;

/**
 * The type Address.
 */
@Getter
@Setter
@Embeddable
@ToString(callSuper = true)
public class Address implements IAddress {
    private String street;
    private String city;
    private UsState usState;
    private String zipcode;
}
