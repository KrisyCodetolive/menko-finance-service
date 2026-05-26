package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Tiers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TiersPort {
    Page<Tiers> findByFilialeId(UUID filialeId, Pageable pageable);
    Page<Tiers> findByFilialeIdAndType(UUID filialeId, String type, Pageable pageable);
    Optional<Tiers> findById(UUID id);
    Tiers save(Tiers tiers);
    void deleteById(UUID id);
}
