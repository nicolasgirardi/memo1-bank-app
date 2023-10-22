package com.aninfo.model;

import com.aninfo.repository.TransactionRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long cbu;
    private Double sum;
    public Transaction(){}
    public Transaction(Long cbu,Double sum){
        this.cbu = cbu;
        this.sum = sum;
    }
    public Long getId(){
        return id;
    }
    public Long getCbu(){
        return cbu;
    }
    public Double getSum(){
        return sum;
    }
    public void setCbu(Long cbu){
        this.cbu = cbu;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setSum(Double sum) {
        this.sum = sum;
    }
}
