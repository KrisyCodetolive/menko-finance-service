package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.MouvementInterFiliale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MouvementInterFilialePort {
    Page<MouvementInterFiliale> findByFilialeId(UUID filialeId, Pageable pageable);
    Optional<MouvementInterFiliale> findById(UUID id);
    MouvementInterFiliale save(MouvementInterFiliale mouvement);
}
