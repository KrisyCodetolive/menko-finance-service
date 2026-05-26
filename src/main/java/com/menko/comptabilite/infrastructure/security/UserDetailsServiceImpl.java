package com.menko.comptabilite.infrastructure.security;

import com.menko.comptabilite.infrastructure.persistence.entity.UtilisateurJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.UtilisateurJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurJpaRepository utilisateurRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UtilisateurJpa u = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));

        return new User(
                u.getEmail(),
                u.getMotDePasse(),
                u.isActif(),
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().getNom()))
        );
    }
}
