package com_apibancaria.Controller;

import com_apibancaria.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/accounts/accountstatus")
public class AccountStatusController {

    @Autowired
    private AccountService accountService;

    // 1. Habilitar ou Desabilitar Conta
    @PatchMapping("/{accountId}/status")
    public ResponseEntity<Void> updateAccountStatus(
            @PathVariable UUID accountId,
            @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400
        }
        try {
            accountService.toggleAccountStatus(accountId, enabled); // Chama o método  AccountService
            return ResponseEntity.noContent().build(); // Retorna 204
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 se a conta não for encontrada
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao atualizar o status da conta", e);
        }
    }
}
