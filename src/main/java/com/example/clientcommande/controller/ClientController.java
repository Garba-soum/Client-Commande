package com.example.clientcommande.controller;


import com.example.clientcommande.dto.ClientDTO;
import com.example.clientcommande.model.Client;
import com.example.clientcommande.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private  final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    //Ajouter un client
    @PostMapping
    public Client ajouterClient(@RequestBody @Valid ClientDTO clientDTO){
        //Transformer du DTO en entité Client
        Client client = new Client();
        client.setNom(clientDTO.getNom());
        client.setEmail(clientDTO.getEmail());
        client.setTelephone(clientDTO.getTelephone());

        return clientService.creerClient(client);

    }

    //Lister tous les clients
    @GetMapping
    public List<Client> obtenirTousLesClients(){
        return clientService.obtenirTousLesClients();
    }

    //Récupérer un client par ID
    @GetMapping("/{id}")
    public Client obtenirClientParId(@PathVariable Long id){
        return clientService.obtenirClientParId(id);
    }

    @PutMapping("/{id}")
    public Client modifierClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO){
        return clientService.mettreAjourClient(id, clientDTO);
    }


}
