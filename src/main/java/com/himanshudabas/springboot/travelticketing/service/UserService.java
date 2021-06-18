package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.User;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;

import java.util.List;

public interface UserService {

    User register(User user) throws UserNotFoundException, UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User updateUser(User user, String username) throws UserNotFoundException;

    void resetPassword(String email) throws EmailNotFoundException, SendEmailFailException;

}
