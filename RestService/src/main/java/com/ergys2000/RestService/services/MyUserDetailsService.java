package com.ergys2000.RestService.services;

import java.util.ArrayList;
import java.util.Optional;

import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService  {
	@Autowired
	private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(username);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("The username was not found!");
		}
		return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(),
					new ArrayList<>());
    }
	
} 
