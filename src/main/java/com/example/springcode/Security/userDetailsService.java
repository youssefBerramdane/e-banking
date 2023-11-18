package com.example.springcode.Security;

import com.example.springcode.Entities.Profile;
import com.example.springcode.Exceptions.UserNotFound;
import com.example.springcode.Repositotry.ProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class userDetailsService implements UserDetailsService {
    private ProfileRepository profileRepository;

    public userDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Profile> profile=profileRepository.findByEmail(email);
        if(profile.isPresent()){
            return new userdetails(profile.get());
        }else{
            throw new UserNotFound("Email ou mote de pass est incorrect");
        }
    }
}
