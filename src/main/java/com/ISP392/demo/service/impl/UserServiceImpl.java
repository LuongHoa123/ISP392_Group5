package com.ISP392.demo.service.impl;

import com.ISP392.demo.config.SecurityUser;
import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.exception.UserBlockedException;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        return userRepository.existsByEmailAndPassword(username, passwordEncoder.encode(password));
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }


    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<UserEntity> userByUsername = userRepository.findByEmail(username);
            if (userByUsername.isEmpty()) {
                throw new UsernameNotFoundException("Invalid credentials!");
            }
            UserEntity user = userByUsername.get();

            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new UserBlockedException("Tài khoản của bạn đã bị khóa!");
            }

            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));

            return new SecurityUser(user.getEmail(), user.getPassword(), true, true, true, true, grantedAuthorities,
                    user.getEmail());
        } catch (UserBlockedException e) {
            System.out.println("Lỗi: " + e.getMessage());
            throw e;
        } catch (UsernameNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Lỗi không xác định: " + e.getMessage());
            throw new UsernameNotFoundException("Invalid credentials!");
        }
    }

}
