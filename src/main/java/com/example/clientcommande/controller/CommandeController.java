package com.example.clientcommande.controller;

import com.example.clientcommande.dto.CommandeDTO;
import com.example.clientcommande.model.Commande;
import com.example.clientcommande.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    public Commande ajouterCommande(@RequestBody @Valid CommandeDTO commandeDTO){
        Commande commande = new Commande();
        commande.setDescription(commandeDTO.getDescription());
        commande.setMontant(commandeDTO.getMontant());
        commande.setDateCommande(commandeDTO.getDateCommande());

        return commandeService.creerCommande(commande, commandeDTO.getClientId());
    }

    //Lister toutes les commandes
    @GetMapping
    public List<Commande> obtenirToutesLesCommandes(){
        return commandeService.obtenirToutesLesCommandes();
    }

    @GetMapping("/{id}")
    public Commande obtenirCommandeParId(@PathVariable Long id){
        return commandeService.obtenirCommandeParId(id);
    }

    @PutMapping("/{id}")
    public Commande modifierCommande(@PathVariable Long id,
                                     @Valid @RequestBody CommandeDTO dto) {
        return commandeService.mettreAJourCommande(id, dto);
    }
}
