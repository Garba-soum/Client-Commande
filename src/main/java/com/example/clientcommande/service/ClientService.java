package com.example.clientcommande.service;

import com.example.clientcommande.model.Client;
import com.example.clientcommande.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    // Injection par constructeur
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //Créer un client
    public Client creerClient(Client client){
        return clientRepository.save(client);
    }

    //Lister tous les clients
    public List<Client> obtenirTousLesClients(){
        return clientRepository.findAll();
    }

    //Récupérer un client par son id ou erreur si introuvable
    public Client obtenirClientParId(Long id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec id :" + id));
    }
}
