package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.DepositSumIsZero;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.TransactionRepository;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        transactionRepository.save(new Transaction(cbu,-sum));
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }
        if (sum >= 2000)
            sum = sum + Math.min(500,sum/10);
        Account account = accountRepository.findAccountByCbu(cbu);
        account.setBalance(account.getBalance() + sum);
        transactionRepository.save(new Transaction(cbu,sum));
        accountRepository.save(account);

        return account;
    }

    public Account transaction(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);
        if (sum == 0)
            throw new DepositSumIsZero("Cannot deposit zero sums");
        if (account.getBalance() + sum < 0)
            throw new InsufficientFundsException("Insufficient funds");
        if (sum >= 2000)
            sum = sum + Math.min(500,sum/10);
        account.setBalance(account.getBalance() + sum);
        transactionRepository.save(new Transaction(cbu,sum));
        accountRepository.save(account);

        return account;
    }

    public Collection<Transaction> allTransactions(Long cbu){
        ArrayList<Transaction> all = (ArrayList<Transaction>) transactionRepository.findAll();
        ArrayList<Transaction> valid = new ArrayList<Transaction>();
        for (Transaction transaction : all) {
            if (Objects.equals(transaction.getCbu(), cbu))
                valid.add(transaction);
        }
        return valid;
    }

    public Optional<Transaction> transaction(Long id,Long cbu){
        Optional<Transaction> tr = transactionRepository.findById(id);
        if(tr.isPresent()){
            if(tr.get().getCbu() == cbu)
                return tr;
        }
        return Optional.empty();
    }

    public void deleteTransaction(Long id,Long cbu) {
        Optional<Transaction> tr = transactionRepository.findById(id);
        if (tr.isPresent()) {
            if (tr.get().getCbu() == cbu) {
                Account account = accountRepository.findAccountByCbu(tr.get().getCbu());
                Double old = account.getBalance();
                old = old - tr.get().getSum();
                if (old < 0)
                    throw new InsufficientFundsException("Insufficient funds");
                account.setBalance(old);
                accountRepository.save(account);
            }
            transactionRepository.deleteById(id);
        }
    }
}