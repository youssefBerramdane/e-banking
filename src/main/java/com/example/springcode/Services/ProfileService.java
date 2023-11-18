package com.example.springcode.Services;

import com.example.springcode.DTOS.ClientDto;
import com.example.springcode.Entities.Account;
import com.example.springcode.Entities.Profile;
import com.example.springcode.Entities.Transfere;
import com.example.springcode.Enums.ProfileStatus;
import com.example.springcode.Exceptions.UserAlreadyExiste;
import com.example.springcode.Exceptions.UserNotFound;
import com.example.springcode.Repositotry.AccountRepository;
import com.example.springcode.Repositotry.ProfileRepository;
import com.example.springcode.Repositotry.TransfereRepository;
import com.example.springcode.Security.userdetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service @AllArgsConstructor
@Transactional
public class ProfileService {
    private ProfileRepository profileRepository;
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private TransfereRepository transfereRepository;
    private EmailService emailService;

    public void LoadData(){
        profileRepository.save(new Profile(null,"youssef","youssef@email.com","ADMIN", passwordEncoder.encode("1234"),ProfileStatus.Validated ));
    }
    public Profile Registre(String email,String password,String name){
        Optional<Profile> profile=profileRepository.findByEmail(email);
        if(profile.isPresent()){
            throw new UserAlreadyExiste("Email deja exsite ");
        }else{
            Profile p =new Profile(null,name,email,"USER",passwordEncoder.encode(password),ProfileStatus.Waiting);
            profileRepository.save(p);
            accountRepository.save(new Account(null,0,p));
            return p;
        }
    }
    public ResponseEntity<?>addNewClient(String name,String email,String password,double solde){
        if(profileRepository.findByEmail(email).isPresent()){
            return ResponseEntity.status(404).body("Email deja Existe");
        }else{
            accountRepository.save(new Account(null,solde,profileRepository.save(new Profile(null,name,email,"USER",passwordEncoder.encode(password),ProfileStatus.Validated))));

            Map<String,String> map=new HashMap<>();
            return ResponseEntity.status(200).body(map.put("result","client est enregistré"));
        }
    }
    public void deleteClient(Long id){
        List<Transfere> transferes=transfereRepository.findByProfilFromIdOrProfileTo(id,id);
        transferes.forEach(trans->transfereRepository.delete(trans));
        accountRepository.delete(accountRepository.findByProfileId(id));
        profileRepository.deleteById(id);
    }
    public ClientDto getMyData(Authentication authentication){

        Jwt jwt = (Jwt) authentication.getPrincipal();

        Account account=accountRepository.findByProfileId((Long) jwt.getClaims().get("id"));

        ClientDto clientDto=new ClientDto((Long) jwt.getClaims().get("id"), (String) jwt.getClaims().get("sub"), account.getSolde(),null,transfereRepository.findByProfilFromIdOrProfileTo(account.getId(),account.getId()));
        return clientDto;
    }
    public ResponseEntity<?>transfere(Authentication authentication,Long id,double montant){
        Optional<Account> account=accountRepository.findById(id);
        Map<String,String> map=new HashMap<>();
        if(!account.isPresent()){
            map.put("message","Ce compte n'existe pas");
            return ResponseEntity.status(404).body(map);
        }else{
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Account accountFrom=accountRepository.findByProfileId((Long) jwt.getClaims().get("id"));
            if(accountFrom.getSolde()<montant){
                map.put("message","Solde insuffesant");
                return ResponseEntity.status(403).body(map);
            }else{
                Account from=accountFrom;
                Account to=account.get();
                transfereRepository.save(new Transfere(null,montant,from,id));
                accountFrom.setSolde(accountFrom.getSolde()-montant);
                account.get().setSolde(account.get().getSolde()+montant);
                map.put("message","Transfere est effecutué");
                System.out.println(from.getProfile().getEmail());
                System.out.println(to.getProfile().getEmail());
                emailService.sendmail(from.getProfile().getEmail(),"Transfere","Voici Votre Transfere de "+montant+" dhs a client"+id);
                emailService.sendmail(to.getProfile().getEmail(),"Transfer","Voici Votre transfere de "+montant+" dhs debuis client "+from.getId());
                return ResponseEntity.ok(map);
            }
        }


    }
    public ClientDto getclientData(Long id){
        Account account=accountRepository.findByProfileId(id);
        ClientDto clientDto=new ClientDto(id,account.getProfile().getName(),account.getSolde(),account.getProfile().getEmail(),null);
        return clientDto;
    }
    public void EditClient(Long id,String name,String email,String password,double solde){
        Profile profile=profileRepository.findById(id).get();
        profile.setName(name!=""?name:profile.getName());
        profile.setEmail(email!=""?email:profile.getEmail());
        profile.setPasssword(password!=""?name:profile.getPasssword());
        Account account=accountRepository.findByProfileId(id);
        account.setSolde(solde>0?solde:account.getSolde());
        profileRepository.save(profile);
        accountRepository.save(account);
    }
    public void validClient(Long id){
        Profile profile=profileRepository.findById(id).orElseThrow(()->new UserNotFound("User not found"));
        profile.setProfileStatus(ProfileStatus.Validated);
        profileRepository.save(profile);
    }
    public List<Transfere> getOperationslist(Long id){
        Account account=accountRepository.findByProfileId(id);
        List<Transfere> transferes=transfereRepository.findByProfilFromId(account.getId());
        return transferes;
    }
}
