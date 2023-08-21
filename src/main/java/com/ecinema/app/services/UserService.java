package com.ecinema.app.services;

import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.contracts.IPassword;
import com.ecinema.app.domain.contracts.IRegistration;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.UserProfileForm;
import com.ecinema.app.validators.RegistrationValidator;
import com.ecinema.app.validators.UserProfileValidator;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.UserRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService extends AbstractEntityService<User, UserRepository, UserDto> implements UserDetailsService {

    private final AdminService adminService;
    private final EncoderService encoderService;
    private final CustomerService customerService;
    private final ModeratorService moderatorService;
    private final UserProfileValidator userProfileValidator;
    private final RegistrationValidator registrationValidator;

    /**
     * Instantiates a new User service.
     *
     * @param repository       the repository
     * @param customerService  the customer role def service
     * @param moderatorService the moderator role def service
     * @param adminService     the admin service
     */
    @Autowired
    public UserService(UserRepository repository, CustomerService customerService,
                       ModeratorService moderatorService, AdminService adminService,
                       EncoderService encoderService, UserProfileValidator userProfileValidator,
                       RegistrationValidator registrationValidator) {
        super(repository);
        this.adminService = adminService;
        this.encoderService = encoderService;
        this.customerService = customerService;
        this.moderatorService = moderatorService;
        this.userProfileValidator = userProfileValidator;
        this.registrationValidator = registrationValidator;
    }

    @Override
    protected void onDelete(User user) {
        logger.debug("User Service on delete");
        Set<UserAuthority> userAuthorities = user.getAuthorities();
        logger.debug("User roles: " + userAuthorities);
        for (UserAuthority userAuthority : userAuthorities) {
            logger.debug("Deleting user role: " + userAuthority);
            switch (userAuthority) {
                case ADMIN -> adminService.delete(
                        (Admin) user.getUserAuthorities().get(userAuthority));
                case CUSTOMER -> customerService.delete(
                        (Customer) user.getUserAuthorities().get(userAuthority));
                case MODERATOR -> moderatorService.delete(
                        (Moderator) user.getUserAuthorities().get(userAuthority));
            }
        }
    }

    @Override
    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setCreationDateTime(user.getCreationDateTime());
        userDto.setLastActivityDateTime(user.getLastActivityDateTime());
        userDto.getUserAuthorities().addAll(user.getUserAuthorities().keySet());
        logger.debug("Converting user to DTO: " + userDto);
        logger.debug("User: " + user);
        return userDto;
    }

    /**
     * Edit profile.
     *
     * @param userProfileForm the profile form
     * @throws NoEntityFoundException the no entity found exception
     * @throws InvalidArgumentException   the invalid args exception
     */
    public void editUserProfile(UserProfileForm userProfileForm)
            throws NoEntityFoundException, InvalidArgumentException {
        User user = repository.findById(userProfileForm.getUserId()).orElseThrow(
                () -> new NoEntityFoundException("user", "id", userProfileForm.getUserId()));
        List<String> errors = new ArrayList<>();
        userProfileValidator.validate(userProfileForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        user.setFirstName(userProfileForm.getFirstName());
        user.setLastName(userProfileForm.getLastName());
        user.setBirthDate(userProfileForm.getBirthDate());
        repository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String s)
            throws UsernameNotFoundException {
        return repository.findByUsernameOrEmail(s).orElseThrow(
                () -> new UsernameNotFoundException("No user found with username or email " + s));
    }

    public void updateLastActivityDateTimeOfUserWithId(Long userId)
            throws NoEntityFoundException {
        User user = repository.findById(userId).orElseThrow(
                () -> new NoEntityFoundException("user", "id", userId));
        user.setLastActivityDateTime(LocalDateTime.now());
        repository.save(user);
    }

    public UserDto register(IRegistration registration, boolean passwordEncoded,
                            boolean securityAnswer1Encoded, boolean securityAnswer2Encoded)
            throws InvalidArgumentException, ClashException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("User registration");
        if (registration.getAuthorities().isEmpty()) {
            throw new InvalidArgumentException("Registration authorities cannot be empty");
        }
        List<String> errors = new ArrayList<>();
        registrationValidator.validate(registration, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        if (existsByEmail(registration.getEmail())) {
            throw new ClashException("User with email " + registration.getEmail() + " already exists");
        }
        if (existsByUsername(registration.getUsername())) {
            throw new ClashException("User with username " + registration.getUsername() + " already exists");
        }
        User user = new User();
        user.setUsername(registration.getUsername());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoded ? registration.getPassword() :
                                 encoderService.encode(registration.getPassword()));
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setBirthDate(registration.getBirthDate());
        user.setSecurityQuestion1(registration.getSecurityQuestion1());
        user.setSecurityAnswer1(securityAnswer1Encoded ? registration.getSecurityAnswer1() :
                                        encoderService.encode(registration.getSecurityAnswer1()));
        user.setSecurityQuestion2(registration.getSecurityQuestion2());
        user.setSecurityAnswer2(securityAnswer2Encoded ? registration.getSecurityAnswer2() :
                                        encoderService.encode(registration.getSecurityAnswer2()));
        user.setCreationDateTime(LocalDateTime.now());
        user.setLastActivityDateTime(LocalDateTime.now());
        user.setIsAccountEnabled(true);
        user.setIsAccountLocked(false);
        user.setIsAccountExpired(false);
        user.setIsCredentialsExpired(false);
        repository.save(user);
        logger.debug("Saved user: " + user);
        logger.debug("Register new authorities for user: " + registration.getAuthorities());
        addUserAuthorityToUser(user, registration.getAuthorities());
        logger.debug("Instantiated and saved new user: " + user);
        UserDto userDto = convertToDto(user);
        logger.debug("Returning new user DTO: " + userDto);
        return userDto;
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public boolean existsByUsernameOrEmail(String s) {
        return repository.existsByUsernameOrEmail(s);
    }

    public UserDto findByUsername(String username) {
        User user = repository.findByUsername(username).orElseThrow(
                () -> new NoEntityFoundException("user", "username", username));
        return convertToDto(user);
    }

    public UserDto findByUsernameOrEmail(String s) {
        User user = repository.findByUsernameOrEmail(s).orElseThrow(
                () -> new NoEntityFoundException("user", "email or username", s));
        return convertToDto(user);
    }

    public Optional<Long> findIdByUsername(String username) {
        return repository.findIdByUsername(username);
    }

    public Optional<Long> findIdByEmail(String email) {
        return repository.findIdByEmail(email);
    }

    public UserDto findByEmail(String email)
            throws NoEntityFoundException {
        User user = repository.findByEmail(email).orElseThrow(
                () -> new NoEntityFoundException("user", "email", email));
        return convertToDto(user);
    }

    public <T extends AbstractUserAuthority> Optional<T> getUserAuthorityOf(Long userId, Class<T> userAuthorityClass)
            throws InvalidArgumentException, NoEntityFoundException {
        UserAuthority userAuthority = UserAuthority.defClassToUserRole(userAuthorityClass);
        if (userAuthority == null) {
            throw new InvalidArgumentException("The provided class " + userAuthorityClass.getName() +
                                                   " is not mapped to a user role value");
        }
        User user = repository.findById(userId).orElseThrow(
                () -> new NoEntityFoundException("User", "id", userId));
        AbstractUserAuthority userAuthorityDef = user.getUserAuthorities().get(userAuthority);
        return Optional.ofNullable(userAuthorityClass.cast(userAuthorityDef));
    }

    public Optional<Long> findIdByUsernameOrEmail(String s) {
        return repository.findIdByUsernameOrEmail(s);
    }

    public List<UserDto> findAllByIsAccountLocked(boolean isAccountLocked) {
        List<User> users = repository.findAllByIsAccountLocked(isAccountLocked);
        return convertToDto(users);
    }

    public List<UserDto> findAllByIsAccountEnabled(boolean isAccountEnabled) {
        List<User> users = repository.findAllByIsAccountEnabled(isAccountEnabled);
        return convertToDto(users);
    }

    public List<UserDto> findAllByIsAccountExpired(boolean isAccountExpired) {
        List<User> users = repository.findAllByIsAccountExpired(isAccountExpired);
        return convertToDto(users);
    }

    public List<UserDto> findAllByIsCredentialsExpired(boolean isCredentialsExpired) {
        List<User> users = repository.findAllByIsCredentialsExpired(isCredentialsExpired);
        return convertToDto(users);
    }

    public List<UserDto> findAllByCreationDateTimeBefore(LocalDateTime localDateTime) {
        List<User> users = repository.findAllByCreationDateTimeBefore(localDateTime);
        return convertToDto(users);
    }

    public List<UserDto> findAllByCreationDateTimeAfter(LocalDateTime localDateTime) {
        List<User> users = repository.findAllByCreationDateTimeAfter(localDateTime);
        return convertToDto(users);
    }

    public List<UserDto> findAllByLastActivityDateTimeBefore(LocalDateTime localDateTime) {
        List<User> users = repository.findAllByLastActivityDateTimeBefore(localDateTime);
        return convertToDto(users);
    }

    public List<UserDto> findAllByLastActivityDateTimeAfter(LocalDateTime localDateTime) {
        List<User> users = repository.findAllByLastActivityDateTimeAfter(localDateTime);
        return convertToDto(users);
    }

    public Set<UserAuthority> userAuthorities(Long userId)
            throws NoEntityFoundException {
        User user = repository.findById(userId).orElseThrow(
                () -> new NoEntityFoundException("user", "id", userId));
        return new HashSet<>(user.getUserAuthorities().keySet());
    }

    public List<String> userAuthoritiesAsListOfStrings(Long userId)
            throws NoEntityFoundException {
        return userAuthorities(userId)
                .stream().map(UserAuthority::name)
                .collect(Collectors.toList());
    }

    protected void addUserAuthorityToUser(User user, Set<UserAuthority> userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException, ClashException {
        logger.debug("Adding "+ userAuthorities + " authorities to user " + user);
        List<UserAuthority> authoritiesAlreadyInstantiated = UtilMethods.findAllKeysThatMapContainsIfAny(
                user.getUserAuthorities(), userAuthorities);
        if (!authoritiesAlreadyInstantiated.isEmpty()) {
            List<String> errors = new ArrayList<>();
            for (UserAuthority userAuthority : authoritiesAlreadyInstantiated) {
                errors.add("FAILURE: User already has " + userAuthority + " role definition");
            }
            throw new ClashException(errors);
        }
        for (UserAuthority userAuthority : userAuthorities) {
            AbstractUserAuthority abstractUserAuthority = userAuthority.instantiate();
            abstractUserAuthority.setUser(user);
            user.getUserAuthorities().put(userAuthority, abstractUserAuthority);
            abstractUserAuthority.setIsAuthorityValid(true);
            switch (userAuthority) {
                case ADMIN -> adminService.save((Admin) abstractUserAuthority);
                case CUSTOMER -> customerService.save((Customer) abstractUserAuthority);
                case MODERATOR -> moderatorService.save((Moderator) abstractUserAuthority);
            }
        }
    }

    public void addUserAuthorityToUser(Long userId, UserAuthority... userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException, ClashException {
        addUserAuthorityToUser(userId, Set.of(userAuthorities));
    }

    public void addUserAuthorityToUser(Long userId, Set<UserAuthority> userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException, ClashException {
        User user = repository.findById(userId).orElseThrow(
                () -> new NoEntityFoundException("user", "id", userId));
        addUserAuthorityToUser(user, userAuthorities);
    }

    public void removeUserAuthorityFromUser(User user, UserAuthority... userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException {
        removeUserAuthorityFromUser(user.getId(), userAuthorities);
    }

    public void removeUserAuthorityFromUser(User user, Set<UserAuthority> userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException {
        removeUserAuthorityFromUser(user.getId(), userAuthorities);
    }

    public void removeUserAuthorityFromUser(Long userId, UserAuthority... userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException {
        removeUserAuthorityFromUser(userId, Set.of(userAuthorities));
    }

    public void removeUserAuthorityFromUser(Long userId, Set<UserAuthority> userAuthorities)
            throws NoEntityFoundException, InvalidArgumentException {
        User user = repository.findById(userId).orElseThrow(
                () -> new NoEntityFoundException("User", "id", userId));
        for (UserAuthority userAuthority : userAuthorities) {
            AbstractUserAuthority iUserAuthority = user.getUserAuthorities().get(userAuthority);
            if (iUserAuthority != null) {
                switch (userAuthority) {
                    case ADMIN -> adminService.delete((Admin) iUserAuthority);
                    case CUSTOMER -> customerService.delete((Customer) iUserAuthority);
                    case MODERATOR -> moderatorService.delete((Moderator) iUserAuthority);
                }
            }
        }
    }

    public void requestPasswordChange(IPassword iPassword)
            throws InvalidArgumentException {


    }

}
