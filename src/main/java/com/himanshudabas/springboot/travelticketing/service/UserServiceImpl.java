package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.constant.UserImplConstant;
import com.himanshudabas.springboot.travelticketing.enumeration.Role;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.domain.UserPrincipal;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import com.himanshudabas.springboot.travelticketing.model.User;
import com.himanshudabas.springboot.travelticketing.repository.UserRepository;
import com.himanshudabas.springboot.travelticketing.service.email.EmailService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;


    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            LOGGER.error(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
            throw new UserNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("[loadUserByUsername]: " + UserImplConstant.FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public User register(User user) throws UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException {
        validateNewUsernameAndEmail(user.getUsername(), user.getEmail());
        User newUser = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setActive(true);
        newUser.setNotLocked(true);
        newUser.setRole(Role.ROLE_USER.name());
        newUser.setAuthorities(Role.ROLE_USER.getAuthorities());
        newUser.setBusinessUnit(user.getBusinessUnit());
        newUser.setTelephone(user.getTelephone());
        newUser.setTitle(user.getTitle());
        newUser.setAddress(user.getAddress());
        userRepository.save(newUser);
        LOGGER.info("New user password: " + password);
        emailService.send(user.getEmail(), password);
        return newUser;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User updateUser(User user, String username) throws UserNotFoundException {
        User existingUser = validateExistingUser(username);
        if (isNotBlank(user.getUsername())) {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getUsername());
        }
        if (isNotBlank(user.getEmail())) {
            existingUser.setUsername(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }
        if (isNotBlank(user.getFirstName()))
            existingUser.setFirstName(user.getFirstName());
        if (isNotBlank(user.getLastName()))
            existingUser.setLastName(user.getLastName());
        if (isNotBlank(user.getBusinessUnit()))
            existingUser.setBusinessUnit(user.getBusinessUnit());
        if (isNotBlank(user.getTelephone()))
            existingUser.setTelephone(user.getTelephone());
        if (isNotBlank(user.getTitle()))
            existingUser.setTitle(user.getTitle());
        if (user.getAddress() != null)
            existingUser.setAddress(user.getAddress());

        userRepository.save(existingUser);
        return existingUser;
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, SendEmailFailException {
        User user = validateExistingEmail(email);
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.send(email, password);
    }

    private User validateExistingUser(String username) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
        }
        return user;
    }

    private User validateExistingEmail(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(UserImplConstant.NO_USER_FOUND_BY_EMAIL + email);
        }
        return user;
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private void validateNewUsernameAndEmail(String newUsername, String newEmail) throws EmailExistException, UsernameExistException, UsernameEmailMismatchException {
        User newUserByUsername = findUserByUsername(newUsername);
        User newUserByEmail = findUserByEmail(newEmail);

        if (newUserByUsername != null) {
            throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXISTS);
        }
        if (newUserByEmail != null) {
            throw new EmailExistException(UserImplConstant.EMAIL_ALREADY_EXISTS);
        }
        if (!newUsername.equals(newEmail)) {

            throw new UsernameEmailMismatchException("");
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
