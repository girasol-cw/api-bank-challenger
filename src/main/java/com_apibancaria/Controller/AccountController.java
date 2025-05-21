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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;




@RestController
@RequestMapping("/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    private AccountService accountService;

    // 1. Criação de conta
    @PostMapping
    public ResponseEntity<Map<String, String>> criarConta(@RequestBody @Valid AccountDto accountDto) {
        try {
            Account conta = accountService.createAccount(accountDto);
            Map<String, String> resposta = new LinkedHashMap<>();
            resposta.put("account", conta.getId().toString());
            resposta.put("fullName", conta.getFullName());
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> erroResposta = new HashMap<>();
            erroResposta.put("mensagem", "CPF ou email já cadastrado!");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erroResposta);
        } catch (Exception e) {
            Map<String, String> erroResposta = new HashMap<>();
            erroResposta.put("erro", "Erro interno no servidor.");
            erroResposta.put("mensagem", "Ocorreu um erro ao criar a conta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroResposta);
        }
    }

    // 2. Atualizar conta
    @PutMapping("/{idConta}")
    public ResponseEntity<Void> atualizarConta(@PathVariable UUID idConta, @RequestBody @Valid AccountDto accountDto) {
        try {
            accountService.updateAccount(idConta, accountDto);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content para sucesso
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar conta: " + e.getMessage(), e);
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
                Map<String, Object> dadosConta = new LinkedHashMap<>();
                dadosConta.put("balance", conta.getBalance());
                dadosConta.put("fullname", conta.getFullName());
                dadosConta.put("account", conta.getId().toString());
                resposta.add(dadosConta);
            });

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar contas", e);
        }
    }
}