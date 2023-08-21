package com.ecinema.app.domain.enums;

import com.ecinema.app.domain.entities.AbstractUserAuthority;
import com.ecinema.app.domain.entities.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of each of the roles assignable to {@link User}. There is a one-to-one association between
 * each UserAuthority enum value and a child class of {@link AbstractUserAuthority}.
 */
public enum UserAuthority implements GrantedAuthority {

    /**
     * The Admin.
     */
    ADMIN {
        @Override
        public Admin instantiate() {
            return new Admin();
        }

        @Override
        public Admin cast(Object o) {
            return (Admin) o;
        }

    },

    /**
     * The Moderator.
     */
    MODERATOR {
        @Override
        public Moderator instantiate() {
            return new Moderator();
        }

        @Override
        public Moderator cast(Object o) {
            return (Moderator) o;
        }

    },

    /**
     * The Customer.
     */
    CUSTOMER {
        @Override
        public Customer instantiate() {
            return new Customer();
        }

        @Override
        public Customer cast(Object o) {
            return (Customer) o;
        }

    };

    private static final Map<Class<? extends AbstractUserAuthority>, UserAuthority> DEF_CLASS_TO_USER_ROLE_MAP =
            new HashMap<>() {{
                put(Admin.class, ADMIN);
                put(Customer.class, CUSTOMER);
                put(Moderator.class, MODERATOR);
            }};

    /**
     * Fetches the UserAuthority enum value associated with the provided {@link AbstractUserAuthority} child class.
     *
     * @param AbstractUserAuthorityClass the user role def class associated with the
     *                                   UserAuthority enum value to be fetched.
     * @return the user role associated with the provided AbstractUserAuthority child class.
     */
    public static UserAuthority defClassToUserRole(Class<? extends AbstractUserAuthority> AbstractUserAuthorityClass) {
        return DEF_CLASS_TO_USER_ROLE_MAP.get(AbstractUserAuthorityClass);
    }

    /**
     * Instantiates a new {@link AbstractUserAuthority} child instance associated with the enum value.
     *
     * @param <T> the AbstractUserAuthority child type to be instantiated
     * @return the AbstractUserAuthority child type to be instantiated
     */
    public abstract <T extends AbstractUserAuthority> T instantiate();

    /**
     * Casts the provided object to the {@link AbstractUserAuthority} child class associated with the UserAuthority
     * enum value. The cast is unchecked, and it is assumed the caller knows for sure the provided object is an
     * instance of the class it is being cast to.
     *
     * @param <T> the AbstractUserAuthority child type to cast the object to
     * @param o   the object to be cast
     * @return the AbstractUserAuthority child type
     */
    public abstract <T extends AbstractUserAuthority> T cast(Object o);

    @Override
    public String getAuthority() {
        return name();
    }

}
