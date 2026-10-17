package tn.esprit.autoloc.web.controller;

import java.time.LocalDateTime;

public record ErrorResponse(int statut, String message, LocalDateTime horodatage) {
}
