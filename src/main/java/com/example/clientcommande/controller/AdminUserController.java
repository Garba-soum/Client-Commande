package com.example.clientcommande.controller;

import com.example.clientcommande.repository.AppUserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserRepository userRepository;

    public AdminUserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🗑️ Supprimer un utilisateur (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimerUtilisateur(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return "Utilisateur introuvable.";
        }

        userRepository.deleteById(id);
        return "Utilisateur supprimé avec succès.";
    }
}
