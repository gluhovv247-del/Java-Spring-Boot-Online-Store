package com.springboot.online_store.services;

import com.springboot.online_store.dtos.user.AuthResponse;
import com.springboot.online_store.dtos.user.UserCreateDto;
import com.springboot.online_store.dtos.user.UserLogin;
import com.springboot.online_store.entities.Cart;
import com.springboot.online_store.entities.User;
import com.springboot.online_store.exceptions.custom.UserAlreadyExistsException;
import com.springboot.online_store.mappers.UserMapper;
import com.springboot.online_store.repositories.CartRepository;
import com.springboot.online_store.repositories.UserRepository;
import com.springboot.online_store.security.JwtUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CartRepository cartRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, CartRepository cartRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.cartRepository = cartRepository;
    }

    public AuthResponse registerUser(UserCreateDto userDto){
        if(userRepository.existsByEmail(userDto.email())){
            throw new UserAlreadyExistsException(userDto.email());
        }

        String encodedPassword = passwordEncoder.encode(userDto.password());
        User user = new User(
                userDto.firstName(),
                userDto.lastName(),
                userDto.email(),
                encodedPassword,
                userDto.phoneNumber(),
                userDto.birthDate(),
                userDto.role()
                );
        Cart cart = new Cart(user);

        userRepository.save(user);
        cartRepository.save(cart);

        return new AuthResponse(jwtUtil.generateToken(user));
    }

    public AuthResponse login(UserLogin userDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.email(),
                        userDto.password()
                )
        );
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        assert userDetails != null;
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        return new AuthResponse(
                jwtUtil.generateToken(user)
        );
    }
}
