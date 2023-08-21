package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IProfile;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserProfileForm implements IProfile, Serializable {
    private Long userId = 0L;
    private String firstName = "";
    private String lastName = "";
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate = LocalDate.now();
}
