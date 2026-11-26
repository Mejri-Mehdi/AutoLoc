package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.dto.ClientRequestDto;
import tn.esprit.autoloc.dto.ClientResponseDto;

public interface IClientService {
    Client create(Client client);
    Client findById(Long id);
    List<Client> findAll();
    Client update(Long id, Client client);
    void deleteById(Long id);
    ClientResponseDto ajouterClientDto(ClientRequestDto dto);
    ClientResponseDto afficherClientDto(Long id);

}
