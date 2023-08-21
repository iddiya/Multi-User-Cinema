package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.IAddress;
import com.ecinema.app.domain.contracts.IPaymentCard;
import com.ecinema.app.domain.entities.Address;
import com.ecinema.app.domain.enums.PaymentCardType;
import com.ecinema.app.util.UtilMethods;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * The type Payment card dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentCardDto extends AbstractDto implements IPaymentCard {

    private Long userId = null;
    private String cardNumber = null;
    private String firstName = "";
    private String lastName = "";
    private IAddress billingAddress = null;
    private PaymentCardType paymentCardType = null;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate = LocalDate.now();

    /**
     * Expiration date formatted string.
     *
     * @return the string
     */
    public String expirationDateFormatted() {
        return UtilMethods.localDateFormatted(expirationDate);
    }

    /**
     * Is expired boolean.
     *
     * @return the boolean
     */
    public Boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }

}
