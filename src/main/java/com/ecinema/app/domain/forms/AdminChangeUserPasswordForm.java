package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IPassword;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminChangeUserPasswordForm implements Serializable, IPassword {
    private String password;
    private String confirmPassword;
    private String emailOrUsername;
}
