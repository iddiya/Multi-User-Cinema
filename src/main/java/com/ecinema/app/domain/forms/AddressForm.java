package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IAddress;
import com.ecinema.app.domain.enums.UsState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class AddressForm implements IAddress, Serializable {
    private String street;
    private String city;
    private UsState usState;
    private String zipcode;
}
