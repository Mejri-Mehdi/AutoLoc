package tn.esprit.autoloc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDto {

    private Long idClient;

    private String nom;

    private String prenom;

    private String telephone;
}