package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IPassword;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class ChangePasswordForm implements IPassword, Serializable {
    private String email = "";
    private String question1 = "";
    private String answer1 = "";
    private String question2 = "";
    private String answer2 = "";
    private String password = "";
    private String confirmPassword = "";
}
