package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.AdminDto;
import com.ecinema.app.domain.entities.Admin;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.forms.AdminChangeUserPasswordForm;
import com.ecinema.app.validators.PasswordValidator;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.AdminRepository;
import com.ecinema.app.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminService extends UserAuthorityService<Admin, AdminRepository, AdminDto> {

    private final UserRepository userRepository;
    private final EncoderService encoderService;
    private final PasswordValidator passwordValidator;

    public AdminService(AdminRepository repository, UserRepository userRepository,
                        PasswordValidator passwordValidator, EncoderService encoderService) {
        super(repository);
        this.userRepository = userRepository;
        this.encoderService = encoderService;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public AdminDto convertToDto(Admin admin) {
        AdminDto adminDto = new AdminDto();
        fillCommonUserAuthorityDtoFields(admin, adminDto);
        return adminDto;
    }

    public void changeUserPassword(AdminChangeUserPasswordForm adminChangeUserPasswordForm)
            throws NoEntityFoundException, InvalidArgumentException {
        List<String> errors = new ArrayList<>();
        passwordValidator.validate(adminChangeUserPasswordForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        User user = userRepository.findByUsernameOrEmail(adminChangeUserPasswordForm.getEmailOrUsername())
                                  .orElseThrow(() -> new NoEntityFoundException(
                                          "user", "email or username",
                                          adminChangeUserPasswordForm.getEmailOrUsername()));
        String encodedPassword = encoderService.encode(adminChangeUserPasswordForm.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

}
