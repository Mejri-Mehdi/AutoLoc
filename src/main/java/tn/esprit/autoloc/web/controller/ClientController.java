package tn.esprit.autoloc.web.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequestDto;
import tn.esprit.autoloc.dto.ClientResponseDto;
import tn.esprit.autoloc.service.IClientService;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Client Controller", description = "Gestion des clients")
public class ClientController {

    private final IClientService clientService;

    // FIND ALL
    @GetMapping
    @Operation(summary = "Afficher tous les clients")
    public List<Client> findAll() {

        return clientService.findAll();
    }

    // FIND BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Afficher un client par ID")
    public Client findById(@PathVariable Long id) {

        return clientService.findById(id);
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un client")
    public Client create(@RequestBody Client client) {

        return clientService.create(client);
    }

    // CREATE USING DTO
    @PostMapping("/dto")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "ajouterDto",
            description = "Ajouter un client en utilisant ClientRequestDto"
    )
    public ClientResponseDto ajouterDto(
            @Valid @RequestBody ClientRequestDto dto) {

        return clientService.ajouterClientDto(dto);
    }

    // DISPLAY USING DTO
    @GetMapping("/dto/{id}")
    @Operation(
            summary = "afficherDto",
            description = "Afficher un client avec ClientResponseDto"
    )
    public ClientResponseDto afficherDto(@PathVariable Long id) {

        return clientService.afficherClientDto(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un client")
    public Client update(
            @PathVariable Long id,
            @RequestBody Client client) {

        return clientService.update(id, client);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un client")
    public void deleteById(@PathVariable Long id) {

        clientService.deleteById(id);
    }
}