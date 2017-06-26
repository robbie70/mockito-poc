package com.packtpub.chapter07;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReconciliationJobTest {

    ReconciliationJob job;

    @Mock
    FinancialTransactionDAO financialTransactionDAO;

    @Mock
    MembershipDAO membershipDAO;

    @Mock PayPalFacade payPalFacade;

    @Mock PaymentAdviceDto paymentAdviceDto;

    @Before
    public void setUp(){
        //MockitoAnnotations.initMocks(this);
        job = new ReconciliationJob(financialTransactionDAO, membershipDAO, payPalFacade);
        MembershipStatusDto basicMembership = new MembershipStatusDto();
        basicMembership.setDeductable(.30);
        when(membershipDAO.getStatusFor(anyString())).thenReturn(basicMembership);
    }

    @Test
    public void when_no_Transaction_To_Process_Job_RETURNS_Processing_Count_Zero() throws Exception {
        assertEquals(0, job.reconcile());
    }

    @Test
    public void reconcile_returns_Transaction_count() throws Exception {
        List<TransactionDto> singleTxList = new ArrayList<TransactionDto>();
        singleTxList.add(new TransactionDto());
        when(financialTransactionDAO.retrieveUnSettledTransactions()).thenReturn(singleTxList);
        assertEquals(1, job.reconcile());
    }

    @Test
    public void when_transaction_exists_Then_membership_details_is_retrieved_for_the_developer() throws Exception{
        List<TransactionDto> singleTxList = new ArrayList<TransactionDto>();
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTargetId("DEV001");
        singleTxList.add(transactionDto);
        when(financialTransactionDAO.retrieveUnSettledTransactions()).thenReturn(singleTxList);
        assertEquals(1, job.reconcile());
        verify(membershipDAO).getStatusFor(anyString());
    }

    @Test
    public void when_transactions_exist_then_membership_details_is_retrieved_for_each_developer() throws Exception{
        List<TransactionDto> multipleTxs = new ArrayList<TransactionDto>();
        TransactionDto johnsTransaction = new TransactionDto();
        String johnsDeveloperId = "bob999";
        johnsTransaction.setTargetId(johnsDeveloperId);

        TransactionDto bobsTransaction = new TransactionDto();
        String bobsDeveloperId = "bob999";
        bobsTransaction.setTargetId(bobsDeveloperId);

        multipleTxs.add(johnsTransaction);
        multipleTxs.add(bobsTransaction);

        when(financialTransactionDAO.retrieveUnSettledTransactions()).thenReturn(multipleTxs);

        assertEquals(2, job.reconcile());

        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);

        verify(membershipDAO, new Times(2)).getStatusFor(argCaptor.capture());
        List<String> passedValues = argCaptor.getAllValues();

        assertEquals(johnsDeveloperId, passedValues.get(0));
        assertEquals(bobsDeveloperId, passedValues.get(1));
    }

    @Test
    public void when_transaction_exists_Then_sends_Payable_To_PayPal() throws Exception{
        List<TransactionDto> davidsTransactionList = new ArrayList<TransactionDto>();

        String davidsDeveloperId = "dev999";
        String davidsPayPalId = "david@paypal.com";
        double davidsSuperMarioGamePrice = 100.00;

        davidsTransactionList.add(createTxDto(davidsDeveloperId, davidsPayPalId, davidsSuperMarioGamePrice));

        when(financialTransactionDAO.retrieveUnSettledTransactions()).thenReturn(davidsTransactionList);

        assertEquals(1, job.reconcile());

        verify(payPalFacade).sendAdvice(isA(PaymentAdviceDto.class));
    }

    @Test
    public void calculates_payable() throws Exception {
        List<TransactionDto> ronaldosTransactions = new ArrayList<TransactionDto>();

        String ronaldosDeveloperId = "ronaldo007";
        String ronaldosPayPalId = "Ronaldo@RealMadrid.com";
        double ronaldosSoccerFee = 100.00;

        ronaldosTransactions.add(createTxDto(ronaldosDeveloperId, ronaldosPayPalId, ronaldosSoccerFee));

        when(financialTransactionDAO.retrieveUnSettledTransactions()).thenReturn(ronaldosTransactions);

        assertEquals(1, job.reconcile());

        ArgumentCaptor<PaymentAdviceDto> calculatedAdvice = ArgumentCaptor.forClass(PaymentAdviceDto.class);

        verify(payPalFacade).sendAdvice(calculatedAdvice.capture());

        assertTrue(70.00 == calculatedAdvice.getValue().getAmount());
    }


    private TransactionDto createTxDto(String developerId, String payPalId, double gamePrice) {
        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setTargetId(developerId);
        transactionDto.setTargetPayPalId(payPalId);
        transactionDto.setAmount(gamePrice);

        return transactionDto;
    }

}
