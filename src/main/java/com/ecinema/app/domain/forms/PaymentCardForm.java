package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IAddress;
import com.ecinema.app.domain.contracts.IPaymentCard;
import com.ecinema.app.domain.enums.PaymentCardType;
import com.ecinema.app.domain.enums.UsState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

@Data
public class PaymentCardForm implements IPaymentCard, Serializable {

    private Long userId = null;
    private Long paymentCardId = null;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate = LocalDate.now();
    private String cardNumber = "";
    private String firstName = "John";
    private String lastName = "Doe";
    private String street = "Street";
    private String city = "City";
    private String zipcode = "12345";
    private UsState usState = UsState.GEORGIA;
    private PaymentCardType paymentCardType = PaymentCardType.CREDIT;

    @Override
    public IAddress getBillingAddress() {
        AddressForm addressForm = new AddressForm();
        addressForm.setCity(city);
        addressForm.setStreet(street);
        addressForm.setUsState(usState);
        addressForm.setZipcode(zipcode);
        return addressForm;
    }

    @Override
    public void setBillingAddress(IAddress billingAddress) {
        setStreet(billingAddress.getStreet());
        setCity(billingAddress.getCity());
        setUsState(billingAddress.getUsState());
        setZipcode(billingAddress.getZipcode());
    }

}
