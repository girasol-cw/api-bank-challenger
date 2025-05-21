package com_apibancaria.repositories;

import com_apibancaria.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(UUID accountId);
    void deleteByAccountId(UUID accountId);

}