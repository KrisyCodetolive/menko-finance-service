package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Filiale;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FilialePort {
    List<Filiale> findAll();
    Optional<Filiale> findById(UUID id);
    Filiale save(Filiale filiale);
}
