package com.uh2.devops.cheque;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chequeNumber;
    private String accountId;
    private Double amount;

    private LocalDate depositDate;
    private String depositAccountId;

    @Enumerated(EnumType.STRING)
    private ChequeStatus status;

    // ENUM
    public enum ChequeStatus {
        ISSUED,
        DEPOSITED,
        CLEARED,
        REJECTED,
        CANCELED
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public String getDepositAccountId() {
        return depositAccountId;
    }

    public void setDepositAccountId(String depositAccountId) {
        this.depositAccountId = depositAccountId;
    }

    public ChequeStatus getStatus() {
        return status;
    }

    public void setStatus(ChequeStatus status) {
        this.status = status;
    }
}
