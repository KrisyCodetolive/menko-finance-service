package com.menko.comptabilite.domain.exception;

import java.util.UUID;

public class EntiteIntrouvableException extends RuntimeException {

    public EntiteIntrouvableException(String entite, UUID identifiant) {
        super(entite + " introuvable : " + identifiant);
    }

    public EntiteIntrouvableException(String message) {
        super(message);
    }
}
