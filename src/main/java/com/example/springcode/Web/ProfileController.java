package com.example.springcode.Web;

import com.example.springcode.DTOS.ClientDto;
import com.example.springcode.Entities.Account;
import com.example.springcode.Entities.Profile;
import com.example.springcode.Entities.Transfere;
import com.example.springcode.Exceptions.UserNotFound;
import com.example.springcode.Repositotry.AccountRepository;
import com.example.springcode.Repositotry.ProfileRepository;
import com.example.springcode.Repositotry.TransfereRepository;
import com.example.springcode.Security.userdetails;
import com.example.springcode.Services.AccountService;
import com.example.springcode.Services.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@AllArgsConstructor @Slf4j
public class ProfileController {
    private ProfileService profileService;
    private ProfileRepository profileRepository;
    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private AccountService accountService;
    private TransfereRepository transfereRepository;
    private AccountRepository accountRepository;
    @PostMapping("/login")
    public Map<String,String> Login( String email,  String password){
        Map<String,String> map=new HashMap<>();
        try {
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
            Instant instant=Instant.now();
            String username=((userdetails)authentication.getPrincipal()).getUsername();
            Long id=((userdetails) authentication.getPrincipal()).getId();
            JwtClaimsSet jwtClaimsSet= JwtClaimsSet.builder()
                    .issuer("springSecurity")
                    .issuedAt(instant)
                    .subject(username)
                    .claim("id",id)
                    .expiresAt(instant.plus(5, ChronoUnit.MINUTES))
                    .claim("scope",authentication.getAuthorities().stream().map(auth->auth.getAuthority()).collect(Collectors.joining(" ")))
                    .build();
            JwtEncoderParameters jwtEncoderParameters=JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(),jwtClaimsSet);
            String token=jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

            map.put("token",token);
            return map;
        }catch (Exception ex){
            throw ex;
        }

    }
    @PostMapping("/registre")
    public Profile Registre(String email, String password, String name){
        return profileService.Registre(email,password,name);
    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/listclinets")
    public List<Account> getListClients(Authentication authentication){
        return accountService.getListClients();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/newclient")
    public ResponseEntity<?> AddNewClient(String name,String email,String password,double solde){
        return profileService.addNewClient(name,email,password,solde);
    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteClient(@PathVariable Long id){
        profileService.deleteClient(id);
    }

    @GetMapping("/mydata")
    public ClientDto getMyData(Authentication authentication){
        return profileService.getMyData(authentication);
    }

    @PostMapping("/transfere")
    public ResponseEntity<?> transfere(Authentication authentication,Long id,double montant){
        return profileService.transfere(authentication,id,montant);
    }
    @GetMapping("/clientdata/{id}")
    public ClientDto getclientData(@PathVariable Long id){
        return profileService.getclientData(id);
    }
    @PutMapping("/editclient/{id}")
    public void EditClient(@PathVariable Long id,String name,String email,String password,double solde){
         profileService.EditClient(id,name,email,password,solde);
    }
    @PutMapping("/validclient/{id}")
    public void validClient(@PathVariable Long id){

        profileService.validClient(id);
    }
    @GetMapping("/operaionslist/{id}")
    public List<Transfere> getOperationsList(@PathVariable Long id){
        return profileService.getOperationslist(id);
    }
}
