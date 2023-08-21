package com.ecinema.app.domain.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * The type Change password.
 */
@Entity
@Getter
@Setter
@ToString
public class ChangePassword extends AbstractEntity {

    @Column
    private Long userId;

    @Column
    private String email;

    @Column
    private String token;

    @Column
    private String password;

    @Column
    private LocalDateTime creationDateTime;

    @Column
    private LocalDateTime expirationDateTime;

}
