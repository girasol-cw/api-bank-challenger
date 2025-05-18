package com_apibancaria.services;

import com_apibancaria.dtos.AccountDto;
import com_apibancaria.model.Account;
import com_apibancaria.model.Address;
import com_apibancaria.model.Transaction;
import com_apibancaria.repositories.AccountRepository;
import com_apibancaria.repositories.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account getAccountOrThrow(UUID accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }

    // 1. Criar Conta
    @Transactional
    public Account createAccount(AccountDto accountDto) {
        Account account = new Account();
        account.setFirstName(accountDto.firstName());
        account.setLastName(accountDto.lastName());
        account.setNickName(accountDto.nickName());
        account.setDob(accountDto.dob());
        account.setAge(accountDto.age());
        account.setIsMale(accountDto.isMale());
        account.setDocument(accountDto.document());
        account.setPhone(accountDto.phone());
        account.setEmail(accountDto.email());

        if (accountDto.address() != null) {
            Address address = new Address();
            address.setCountry(accountDto.address().country());
            address.setState(accountDto.address().state());
            address.setCity(accountDto.address().city());
            account.setAddress(address);
        }

        return accountRepository.save(account);
    }

    // 2. Atualizar Conta
    @Transactional
    public Account updateAccount(UUID accountId, AccountDto accountDto) throws AccountNotFoundException {
        Account existingAccount = getAccountOrThrow(accountId);
        existingAccount.setFirstName(accountDto.firstName());
        existingAccount.setLastName(accountDto.lastName());
        existingAccount.setNickName(accountDto.nickName());
        existingAccount.setDob(accountDto.dob());
        existingAccount.setAge(accountDto.age());
        existingAccount.setIsMale(accountDto.isMale());
        existingAccount.setDocument(accountDto.document());
        existingAccount.setPhone(accountDto.phone());
        existingAccount.setEmail(accountDto.email());
        if (accountDto.address() != null && existingAccount.getAddress() != null) {
            existingAccount.getAddress().setCountry(accountDto.address().country());
            existingAccount.getAddress().setState(accountDto.address().state());
            existingAccount.getAddress().setCity(accountDto.address().city());
        } else if (accountDto.address() != null) {
            Address address = new Address();
            address.setCountry(accountDto.address().country());
            address.setState(accountDto.address().state());
            address.setCity(accountDto.address().city());
            existingAccount.setAddress(address);
        }
        return accountRepository.save(existingAccount);
    }

    // 3. Listar Todas Contas
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // 4. Consultar Conta por ID
    public Account getAccountById(UUID accountId) throws AccountNotFoundException {
        return getAccountOrThrow(accountId);
    }

    // 5. Depositar
    @Transactional
    public void deposit(UUID accountId, double amount, String currencyCode) throws AccountNotFoundException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor de depósito inválido");
        }

        Account account = getAccountOrThrow(accountId);
        BigDecimal amountBD = BigDecimal.valueOf(amount);
        account.setBalance(account.getBalance().add(amountBD));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amountBD);
        transaction.setOperation("DEPOSITO");
        transaction.setDatetime(LocalDateTime.now());
        transaction.setCurrencyCode(currencyCode);
        transactionRepository.save(transaction);
    }

    // 6. Sacar
    @Transactional
    public void withdraw(UUID accountId, double amount, String currencyCode) throws AccountNotFoundException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor de saque inválido");
        }

        Account account = getAccountOrThrow(accountId);
        BigDecimal amountBD = BigDecimal.valueOf(amount);

        if (account.getBalance().compareTo(amountBD) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        account.setBalance(account.getBalance().subtract(amountBD));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amountBD.negate());
        transaction.setOperation("SAQUE");
        transaction.setDatetime(LocalDateTime.now());
        transaction.setCurrencyCode(currencyCode);
        transactionRepository.save(transaction);
    }

    // 7. Transferência
    @Transactional
    public void transfer(UUID senderId, UUID receiverId, double amount, String currencyCode)
            throws AccountNotFoundException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor de transferência inválido");
        }

        Account sender = getAccountOrThrow(senderId);
        Account receiver = getAccountOrThrow(receiverId);
        BigDecimal amountBD = BigDecimal.valueOf(amount);

        if (sender.getBalance().compareTo(amountBD) < 0) {
            throw new RuntimeException("Saldo insuficiente para transferência");
        }

        // Saldos
        sender.setBalance(sender.getBalance().subtract(amountBD));
        receiver.setBalance(receiver.getBalance().add(amountBD));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Tramsação
        Transaction outgoing = new Transaction();
        outgoing.setAccount(sender);
        outgoing.setAmount(amountBD.negate());
        outgoing.setOperation("TRANSFERENCIA_SAIDA");
        outgoing.setDatetime(LocalDateTime.now());
        outgoing.setCurrencyCode(currencyCode);
        outgoing.setExtraInfo("Para: " + receiver.getId());
        transactionRepository.save(outgoing);

        // Cria transação de entrada
        Transaction incoming = new Transaction();
        incoming.setAccount(receiver);
        incoming.setAmount(amountBD);
        incoming.setOperation("TRANSFERENCIA_ENTRADA");
        incoming.setDatetime(LocalDateTime.now());
        incoming.setCurrencyCode(currencyCode);
        incoming.setExtraInfo("De: " + sender.getId());
        transactionRepository.save(incoming);
    }

    // 8. Consultar Extrato
    public Map<String, Object> getAccountReport(UUID accountId) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        List<Map<String, Object>> formattedTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            Map<String, Object> txMap = new HashMap<>();
            txMap.put("valor", t.getAmount());
            txMap.put("operacao", t.getOperation());
            txMap.put("data", t.getDatetime().toString());
            txMap.put("moeda", t.getCurrencyCode());
            if (t.getExtraInfo() != null) {
                txMap.put("info", t.getExtraInfo());
            }
            formattedTransactions.add(txMap);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("contaId", accountId.toString());
        report.put("saldoAtual", account.getBalance());
        report.put("transacoes", formattedTransactions);

        return report;
    }

    // 9. Ativar/Desativar Conta
    @Transactional
    public void toggleAccountStatus(UUID accountId, boolean active) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountId);
        account.setIsActive(active);
        accountRepository.save(account);
    }

    // 10. Deletar Conta
    @Transactional
    public void deleteAccount(UUID accountId) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountId);
        accountRepository.delete(account);
    }
}