package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.UserAuthority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * The type Admin.
 */
@Getter
@Setter
@Entity
@ToString
public class Admin extends AbstractUserAuthority {

    @Override
    protected final UserAuthority defineUserRole() {
        return UserAuthority.ADMIN;
    }

}
