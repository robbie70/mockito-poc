package com.packtpub.chapter07;

import java.util.List;

public class ReconciliationJob {

    private final FinancialTransactionDAO financialTxDAO;
    private final MembershipDAO membershipDAO;
    private final PayPalFacade payPalFacade;

    public ReconciliationJob(FinancialTransactionDAO financialTxDAO, MembershipDAO membershipDAO, PayPalFacade payPalFacade){
        this.financialTxDAO = financialTxDAO;
        this.membershipDAO = membershipDAO;
        this.payPalFacade = payPalFacade;
    }

    public int reconcile(){
        List<TransactionDto> unSettledTxs = financialTxDAO.retrieveUnSettledTransactions();

        for(TransactionDto transactionDto : unSettledTxs) {
            MembershipStatusDto membership = membershipDAO.getStatusFor(transactionDto.getTargetId());

            double payableAmount = transactionDto.getAmount() - transactionDto.getAmount() * membership.getDeductable();

            payPalFacade.sendAdvice(new PaymentAdviceDto(payableAmount, transactionDto.getTargetPayPalId(), "Post payment for developer " +
                    transactionDto.getTargetId()));
        }

        return unSettledTxs.size();
    }


}
