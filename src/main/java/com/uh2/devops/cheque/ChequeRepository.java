package com.uh2.devops.cheque;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {

    List<Cheque> findByAccountId(String accountId);

    List<Cheque> findByChequeNumber(String chequeNumber);

    List<Cheque> findByAccountIdAndChequeNumber(String accountId, String chequeNumber);
}
