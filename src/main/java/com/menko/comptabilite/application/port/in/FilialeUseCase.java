package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.FilialeRequest;
import com.menko.comptabilite.domain.model.Filiale;

import java.util.List;
import java.util.UUID;

/** Gestion des filiales du groupe. */
public interface FilialeUseCase {
    List<Filiale> listerToutes();
    Filiale trouverParId(UUID id);
    Filiale creer(FilialeRequest request);
    Filiale modifier(UUID id, FilialeRequest request);
}
