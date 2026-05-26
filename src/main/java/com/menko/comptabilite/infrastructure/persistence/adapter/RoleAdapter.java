package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.RolePort;
import com.menko.comptabilite.domain.model.Role;
import com.menko.comptabilite.infrastructure.persistence.entity.RoleJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleAdapter implements RolePort {

    private final RoleJpaRepository repository;

    @Override
    public List<Role> findAll() {
        return repository.findAll().stream().map(RoleAdapter::toDomain).toList();
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return repository.findById(id).map(RoleAdapter::toDomain);
    }

    @Override
    public Optional<Role> findByNom(String nom) {
        return repository.findByNom(nom).map(RoleAdapter::toDomain);
    }

    static Role toDomain(RoleJpa jpa) {
        return Role.builder()
                .identifiant(jpa.getIdentifiant())
                .nom(jpa.getNom())
                .build();
    }
}
