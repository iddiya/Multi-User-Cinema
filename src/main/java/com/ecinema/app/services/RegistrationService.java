package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.RegistrationDto;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.entities.Registration;
import com.ecinema.app.domain.forms.RegistrationForm;
import com.ecinema.app.validators.RegistrationValidator;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.EmailException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.RegistrationRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RegistrationService extends AbstractEntityService<Registration, RegistrationRepository, RegistrationDto> {

    private final UserService userService;
    private final EmailService emailService;
    private final EncoderService encoderService;
    private final RegistrationValidator registrationValidator;

    public RegistrationService(RegistrationRepository repository, UserService userService,
                               EmailService emailService, EncoderService encoderService,
                               RegistrationValidator registrationValidator) {
        super(repository);
        this.userService = userService;
        this.emailService = emailService;
        this.encoderService = encoderService;
        this.registrationValidator = registrationValidator;
    }

    @Override
    protected void onDelete(Registration entity) {}

    @Override
    public RegistrationDto convertToDto(Registration registration) {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setToIRegistration(registration);
        return registrationDto;
    }

    public void deleteAllByEmail(String email) {
        repository.deleteAllByEmail(email);
    }

    public void deleteAllByCreationDateTimeBefore(LocalDateTime localDateTime) {
        repository.deleteAllByCreationDateTimeBefore(localDateTime);
    }

    public void submitRegistrationForm(RegistrationForm registrationForm)
            throws ClashException, InvalidArgumentException, EmailException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Submit registration request and get token");
        if (registrationForm.getAuthorities().isEmpty()) {
            throw new InvalidArgumentException("User authorities cannot be empty");
        }
        if (userService.existsByEmail(registrationForm.getEmail())) {
            throw new ClashException(
                    "User with email " + registrationForm.getEmail() + " already exists");
        }
        if (userService.existsByUsername(registrationForm.getUsername())) {
            throw new ClashException(
                    "User with username " + registrationForm.getUsername() + " already exists");
        }
        List<String> errors = new ArrayList<>();
        registrationValidator.validate(registrationForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        logger.debug("Registration form passed validation checks");
        if (!registrationForm.getIsPasswordEncoded()) {
            String encodedPassword = encoderService.encode(registrationForm.getPassword());
            registrationForm.setPassword(encodedPassword);
            registrationForm.setConfirmPassword(encodedPassword);
            registrationForm.setIsPasswordEncoded(true);
        }
        if (!registrationForm.getIsSecurityAnswer1Encoded()) {
            String encodedAnswer1 = encoderService.encode(registrationForm.getSecurityAnswer1());
            registrationForm.setSecurityAnswer1(encodedAnswer1);
            registrationForm.setIsSecurityAnswer1Encoded(true);
        }
        if (!registrationForm.getIsSecurityAnswer2Encoded()) {
            String encodedAnswer2 = encoderService.encode(registrationForm.getSecurityAnswer2());
            registrationForm.setSecurityAnswer2(encodedAnswer2);
            registrationForm.setIsSecurityAnswer2Encoded(true);
        }
        Registration registration = new Registration();
        registration.setCreationDateTime(LocalDateTime.now());
        registration.setToIRegistration(registrationForm);
        String token = UUID.randomUUID().toString();
        registration.setToken(token);
        emailService.sendFromBusinessEmail(
                registrationForm.getEmail(), buildEmail(token), "Confirm Account");
        repository.save(registration);
        logger.debug("Instantiated and saved new registration: " + registration);
        logger.debug("Registration form: " + registrationForm);
    }

    public UserDto confirmRegistrationRequest(String token)
            throws NoEntityFoundException, ClashException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Confirm registration request");
        Registration registration = repository.findByToken(token).orElseThrow(
                () -> new NoEntityFoundException("registration request", "token", token));

        if (userService.existsByEmail(registration.getEmail())) {
            throw new ClashException("Someone else has already claimed the email you provided in the time " +
                                             "between now and when you requested registration.");
        }
        if (userService.existsByUsername(registration.getUsername())) {
            throw new ClashException("Someone else has already claimed the username you provided in the time " +
                                             "between now and when you requested registration.");
        }
        UserDto userDTO = userService.register(registration,
                                               registration.getIsPasswordEncoded(),
                                               registration.getIsSecurityAnswer1Encoded(),
                                               registration.getIsSecurityAnswer2Encoded());
        deleteAllByEmail(registration.getEmail());
        logger.debug("Registered new user and returned user dto: " + userDTO);
        return userDTO;
    }

    private String buildEmail(String token) {
        return "To confirm your ECinema registration, please click here: " +
                "http://localhost:8080/confirm-registration/" + token;
    }

}
