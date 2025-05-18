package com_apibancaria.Controller;

import com_apibancaria.dtos.AccountDto;
import com_apibancaria.model.Account;
import com_apibancaria.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 1. Criação de conta
    @PostMapping
    public ResponseEntity<Map<String, String>> criarConta(@RequestBody @Valid AccountDto accountDto) {
        try {
            Account conta = accountService.createAccount(accountDto);
            Map<String, String> resposta = new HashMap<>();
            resposta.put("id", conta.getId().toString());
            resposta.put("nomeCompleto", conta.getFullName());
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF ou email já cadastrado!");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR" + e.getMessage());
        }
    }

    // 2. Atualizar conta
    @PutMapping("/{idConta}")
    public ResponseEntity<Void> atualizarConta(@PathVariable UUID idConta, @RequestBody @Valid AccountDto accountDto) {
        try {
            accountService.updateAccount(idConta, accountDto);
            return ResponseEntity.noContent().build(); // Retorna 204 sem conteúdo
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar conta", e);
        }
    }

    // 3. Deletar conta
    @DeleteMapping("/{idConta}")
    public ResponseEntity<Void> deletarConta(@PathVariable UUID idConta) {
        try {
            accountService.deleteAccount(idConta);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao deletar conta", e);
        }
    }

    // 4. Listar todas as contas
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarTodasContas() {
        try {
            List<Account> contas = accountService.getAllAccounts();
            List<Map<String, Object>> resposta = new ArrayList<>();

            contas.forEach(conta -> {
                Map<String, Object> dadosConta = new HashMap<>();
                dadosConta.put("id", conta.getId().toString());
                dadosConta.put("nome", conta.getFullName());
                dadosConta.put("saldo", conta.getBalance());
                resposta.add(dadosConta);
            });

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar contas", e);
        }
    }
}