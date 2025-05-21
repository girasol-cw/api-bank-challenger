package com_apibancaria.Controller;

import com_apibancaria.dtos.DepositDto;
import com_apibancaria.dtos.TransferDto;
import com_apibancaria.dtos.WithdrawalDto;
import com_apibancaria.services.AccountService;
import com_apibancaria.services.AccountNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/accounts/transaction")
public class TransactionController {

    private final AccountService accountService;

    public TransactionController(AccountService accountService) {
        this.accountService = accountService;
    }



    // Comando para depositar
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Map<String, BigDecimal>> deposit(
            @PathVariable UUID accountId,
            @RequestBody @Valid DepositDto depositDto) { // Usando DepositDto
        try {
            accountService.deposit(accountId, depositDto.amount(), depositDto.currencyCode());
            BigDecimal balance = accountService.getAccountById(accountId).getBalance();
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (javax.security.auth.login.AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Comando para sacar
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Map<String, BigDecimal>> withdraw(
            @PathVariable UUID accountId,
            @RequestBody @Valid WithdrawalDto withdrawalDto) { // Usando WithdrawalDto
        try {
            accountService.withdraw(accountId, withdrawalDto.amount(), withdrawalDto.currencyCode());
            BigDecimal balance = accountService.getAccountById(accountId).getBalance();
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        } catch (javax.security.auth.login.AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Comando para transferir
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<Void> transfer(
            @PathVariable UUID accountId,
            @RequestBody @Valid TransferDto transferDto) {
        try {
            accountService.transfer(
                    accountId,
                    transferDto.accountReceiver(),
                    transferDto.amount(),
                    transferDto.currencyCode()
            );
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        } catch (javax.security.auth.login.AccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}