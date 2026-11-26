package tn.esprit.autoloc.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequestDto;
import tn.esprit.autoloc.dto.ClientResponseDto;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.mapper.ClientMapper;
import tn.esprit.autoloc.repository.IClientRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    // CREATE
    @Override
    public Client create(Client client) {

        if (client.getIdClient() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau client ne doit pas avoir d'identifiant"
            );
        }

        validerClient(client);

        return clientRepository.save(client);
    }

    // FIND BY ID
    @Override
    public Client findById(Long id) {

        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client", id)
                );
    }

    // FIND ALL
    @Override
    public List<Client> findAll() {

        return clientRepository.findAll();
    }

    // UPDATE
    @Override
    public Client update(Long id, Client client) {

        validerClient(client);

        Client existant = findById(id);

        existant.setNom(client.getNom());
        existant.setPrenom(client.getPrenom());
        existant.setEmail(client.getEmail());
        existant.setTelephone(client.getTelephone());
        existant.setNumPermis(client.getNumPermis());
        existant.setDateInscription(client.getDateInscription());

        return clientRepository.save(existant);
    }

    // DELETE
    @Override
    public void deleteById(Long id) {

        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }

        clientRepository.deleteById(id);
    }

    // CREATE USING DTO
    @Override
    public ClientResponseDto ajouterClientDto(ClientRequestDto dto) {

        // 1. Convert Request DTO into Entity
        Client client = clientMapper.toEntity(dto);

        // 2. Save Entity into database
        Client savedClient = clientRepository.save(client);

        // 3. Convert saved Entity into Response DTO
        return clientMapper.toResponseDto(savedClient);
    }

    // DISPLAY USING DTO
    @Override
    public ClientResponseDto afficherClientDto(Long id) {

        // 1. Find client in database
        Client client = clientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client", id)
                );

        // 2. Convert Entity into Response DTO
        return clientMapper.toResponseDto(client);
    }

    // VALIDATION
    private void validerClient(Client client) {

        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le nom du client est obligatoire"
            );
        }

        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "L'e-mail du client est obligatoire"
            );
        }
    }
}