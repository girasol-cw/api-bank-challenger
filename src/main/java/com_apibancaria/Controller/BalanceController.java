package com_apibancaria.Controller;

import com_apibancaria.model.Account;
import com_apibancaria.model.Transaction;
import com_apibancaria.repositories.TransactionRepository;
import com_apibancaria.services.AccountService;
import com_apibancaria.services.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts/balance")
public class BalanceController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    // 1. Consultar Saldo
    @GetMapping("/{accountId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable UUID accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            Map<String, Object> response = new HashMap<>();
            response.put("balance", account.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erro ao consultar saldo")
            );
        }
    }

    // 2. Extrato
    @GetMapping("/{accountId}/report")
    public ResponseEntity<?> getAccountReport(@PathVariable UUID accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            List<Transaction> transactionList = transactionRepository.findByAccountId(accountId);

            List<Map<String, String>> formattedTransactions = transactionList.stream()
                    .map(transaction -> {
                        Map<String, String> txMap = new HashMap<>();
                        txMap.put("amount", transaction.getAmount().toString());
                        txMap.put("operation", transaction.getOperation());
                        txMap.put("datetime", transaction.getDatetime().toString().replace("T", " "));
                        if (transaction.getExtraInfo() != null) {
                            txMap.put("extraInfo", transaction.getExtraInfo());
                        }
                        if (transaction.getCurrencyCode() != null) {
                            txMap.put("currencyCode", transaction.getCurrencyCode());
                        }
                        return txMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("accountId", accountId.toString());
            response.put("currentBalance", account.getBalance());
            response.put("transactions", formattedTransactions);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Erro ao gerar extrato")
            );
        }
    }
}