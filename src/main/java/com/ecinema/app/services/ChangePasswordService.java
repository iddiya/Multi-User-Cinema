package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ChangePasswordDto;
import com.ecinema.app.domain.entities.ChangePassword;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.forms.ChangePasswordForm;
import com.ecinema.app.validators.PasswordValidator;
import com.ecinema.app.exceptions.*;
import com.ecinema.app.repositories.ChangePasswordRepository;
import com.ecinema.app.repositories.UserRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChangePasswordService extends AbstractEntityService<ChangePassword, ChangePasswordRepository, ChangePasswordDto> {

    public static final long EXPIRATION_MINUTES = 30L;
    public static final int MAX_TOKEN_INSTANTATIONS = 5;

    private final EmailService emailService;
    private final EncoderService encoderService;
    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;

    public ChangePasswordService(ChangePasswordRepository repository, EmailService emailService,
                                 EncoderService encoderService, UserRepository userRepository,
                                 PasswordValidator passwordValidator) {
        super(repository);
        this.emailService = emailService;
        this.encoderService = encoderService;
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
    }

    @Override
    protected void onDelete(ChangePassword entity) {}

    public ChangePasswordDto convertToDto(ChangePassword changePassword) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setUserId(changePassword.getUserId());
        changePasswordDto.setEmail(changePassword.getEmail());
        changePasswordDto.setToken(changePassword.getToken());
        changePasswordDto.setCreationDateTime(changePassword.getCreationDateTime());
        changePasswordDto.setExpirationDateTime(changePassword.getExpirationDateTime());
        return changePasswordDto;
    }

    public ChangePasswordForm getChangePasswordForm(String email)
            throws NoEntityFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NoEntityFoundException("user", "email", email));
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        changePasswordForm.setEmail(email);
        changePasswordForm.setQuestion1(user.getSecurityQuestion1());
        changePasswordForm.setQuestion2(user.getSecurityQuestion2());
        return changePasswordForm;
    }

    public void submitChangePasswordForm(ChangePasswordForm changePasswordForm)
            throws NoEntityFoundException, InvalidArgumentException, EmailException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Submit change password form");
        List<String> errors = new ArrayList<>();
        passwordValidator.validate(changePasswordForm, errors);
        logger.debug("Change password form passed validation checks");
        User user = userRepository.findByEmail(changePasswordForm.getEmail()).orElseThrow(
                () -> new NoEntityFoundException("user", "email", changePasswordForm.getEmail()));
        logger.debug("Found user id by email: " + changePasswordForm.getEmail());
        if (!encoderService.matches(changePasswordForm.getAnswer1(), user.getSecurityAnswer1())) {
            errors.add("Answer to security question 1 is incorrect");
        }
        if (!encoderService.matches(changePasswordForm.getAnswer2(), user.getSecurityAnswer2())) {
            errors.add("Answer to security question 2 is incorrect");
        }
        if (encoderService.matches(
                UtilMethods.removeWhitespace(changePasswordForm.getPassword()), user.getPassword())) {
            errors.add("New password cannot match old password");
        }
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        ChangePassword changePassword = new ChangePassword();
        String encodedPassword = encoderService.encode(changePasswordForm.getPassword());
        changePassword.setPassword(encodedPassword);
        changePassword.setUserId(user.getId());
        changePassword.setEmail(user.getEmail());
        LocalDateTime creationDateTime = LocalDateTime.now();
        changePassword.setCreationDateTime(creationDateTime);
        LocalDateTime expirationDateTime = creationDateTime.plusMinutes(EXPIRATION_MINUTES);
        changePassword.setExpirationDateTime(expirationDateTime);
        String token;
        int tokenInstantiationAttempts = 0;
        while (true) {
            token = UUID.randomUUID().toString();
            if (existsByToken(token)) {
                tokenInstantiationAttempts++;
            } else {
                break;
            }
            if (tokenInstantiationAttempts >= MAX_TOKEN_INSTANTATIONS) {
                throw new BadInstantiationException(
                        "Failed to instantiate token due to too many clashes with existing tokens");
            }
        }
        changePassword.setToken(token);
        logger.debug("Set token: " + token);
        sendRequestEmail(changePasswordForm.getEmail(), token,
                         creationDateTime, expirationDateTime);
        repository.save(changePassword);
        logger.debug("Sent email, instantiated and saved new change password: " + changePassword);
    }

    public void confirmChangePassword(String token)
            throws NoEntityFoundException, ExpirationException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Confirm change password");
        ChangePassword changePassword = repository.findByToken(token).orElseThrow(
                () -> new NoEntityFoundException("change password request", "token", token));
        logger.debug("Found change password by token: " + changePassword);
        if (changePassword.getExpirationDateTime().isBefore(LocalDateTime.now())) {
            throw new ExpirationException("The request for password change has already expired");
        }
        User user = userRepository.findById(changePassword.getUserId()).orElseThrow(
                () -> new NoEntityFoundException("user", "id", changePassword.getUserId()));
        logger.debug("Found user by id: " + user);
        logger.debug("Changing user password from " + user.getPassword() + " to " + changePassword.getPassword());
        user.setPassword(changePassword.getPassword());
        userRepository.save(user);
        repository.deleteAllByUserId(user.getId());
        sendConfirmationEmail(user.getEmail());
        logger.debug("Deleted all change password requests associated with user id: " + user.getId());
        logger.debug("Saved new user password");
    }

    public List<ChangePasswordDto> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public boolean existsByToken(String token) {
        return repository.existsByToken(token);
    }

    private void sendRequestEmail(String email, String token, LocalDateTime creationDateTime,
                                  LocalDateTime expirationDateTime)
            throws EmailException {
        String emailBody = "A request has been made to change your password.\n" +
                "If you would like to confirm this request, then click the link below:\n" +
                "https//:localhost:8080/change-password-confirm/" + token + "\n\n" +
                "This request was made at " + UtilMethods.localDateTimeFormatted(creationDateTime) + "\n" +
                "and expires at " + UtilMethods.localDateTimeFormatted(expirationDateTime);
        emailService.sendFromBusinessEmail(email, emailBody, "Change Password Request");
    }

    private void sendConfirmationEmail(String email)
            throws EmailException {
        String emailBody = "The password for your account has successfully been changed.";
        emailService.sendFromBusinessEmail(email, emailBody, "Change Password Confirmation");
    }

}
