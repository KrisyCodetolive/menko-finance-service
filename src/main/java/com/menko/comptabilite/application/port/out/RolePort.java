package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolePort {
    List<Role> findAll();
    Optional<Role> findById(UUID id);
    Optional<Role> findByNom(String nom);
}
