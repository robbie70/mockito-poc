package com.packtpub.chapter07;

import java.util.List;

public interface FinancialTransactionDAO {

    List<TransactionDto> retrieveUnSettledTransactions();

}
