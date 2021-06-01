package com.hazelcast.msf.testclient;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.views.AccountDAO;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.*;

public class AccountServiceClient {

    private static final Logger logger = Logger.getLogger(AccountServiceClient.class.getName());
    private final AccountGrpc.AccountBlockingStub blockingStub;
    private final AccountGrpc.AccountFutureStub futureStub;

    private List<String> accountNumbers = new ArrayList<>();
    private static final int OPEN_THREAD_COUNT = 10;
    private static final int TRANSFER_THREAD_COUNT = 1;
    private static final int ACCOUNT_COUNT = 1000;
    private static final int TRANSFER_COUNT = 10000; // 100K will run out of native threads in gRPC

    public static void main(String[] args) throws Exception {

        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        AccountDAO accountDAO = new AccountDAO(); // for queries

        try {
            AccountServiceClient accountServiceClient = new AccountServiceClient(channel);

            // Open 1000 accounts
            logger.info("Opening 1000 accounts using " + OPEN_THREAD_COUNT + " threads");
            long start = System.currentTimeMillis();
            List<Thread> openWorkers = new ArrayList<>();
            for (int i=0; i<OPEN_THREAD_COUNT; i++) {
                Thread t = new Thread(accountServiceClient.new OpenRunnable("B0" + i));
                openWorkers.add(t);
                t.start();
            }
            for (Thread t : openWorkers)
                t.join();
            long elapsed = System.currentTimeMillis() - start;
            logger.info("Opened accounts multi threaded non-blocking in " + elapsed + "ms");
            logger.info("Number of account numbers opened : " + accountServiceClient.accountNumbers.size());
            long totalBalanceAfterOpens = accountDAO.getTotalAccountBalances();
            logger.info("Total balance across accounts: " + totalBalanceAfterOpens);



            // Test transfer money between accounts
            logger.info("Performing transfers");
            start = System.currentTimeMillis();

            // 1. Blocking, single threaded
//            for (int i=0; i<10_000; i++) {
//                int fromIndex = Double.valueOf(Math.random()*100).intValue();
//                if (fromIndex < 0 || fromIndex > 99) System.out.println("BAD VALUUE " + fromIndex);
//                int toIndex = Double.valueOf(Math.random()*100).intValue();
//                if (toIndex < 0 || toIndex > 99) System.out.println("BAD VALUUE " + toIndex);
//                String fromAcct = accountServiceClient.accountNumbers.get(fromIndex);
//                String toAcct = accountServiceClient.accountNumbers.get(toIndex);
//                accountServiceClient.transfer(fromAcct, toAcct, 1000);
//            }
//            elapsed = System.currentTimeMillis() - start;
//            logger.info("Finished transfers using single-threaded blocking in " + elapsed + "ms");

            // 2. Non-blocking, single threaded
            int successCount = accountServiceClient.nonBlockingTransfer("A");
            elapsed = System.currentTimeMillis() - start;
            logger.info("Finished " + successCount + " transfers using single-threaded non-blocking in " + elapsed + "ms");

//            // 3. Non-blocking, multi threaded
//            //logger.info(successCount + " transfer requests reported success");
//            elapsed = System.currentTimeMillis() - start;
//            logger.info("Finished transfers using multi-threaded non-blocking in " + elapsed + "ms");

            long totalBalanceAfterTransfers = accountDAO.getTotalAccountBalances();
            logger.info("Total balance across accounts : " + totalBalanceAfterTransfers);
            if (totalBalanceAfterOpens == totalBalanceAfterTransfers)
                logger.info("It's all good.");
            else
                logger.info("Out of balance by " + (totalBalanceAfterOpens - totalBalanceAfterTransfers));

            logger.info("Finished, disconnecting");
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            accountDAO.disconnect();
        }
    }

    void deadCode() {
        // Tests deposit, withdrawal - verified working, not needed for prototype
//            String acctNumber = accountServiceClient.accountNumbers.get(0);
//            int balance = accountServiceClient.checkBalance(acctNumber);
//            System.out.printf("Account %s initial balance %d\n", acctNumber, balance);
//            logger.info("Crediting account " + acctNumber);
//            accountServiceClient.adjust(acctNumber, 200);
//            logger.info("Debiting one account");
//            accountServiceClient.adjust(acctNumber, -25);
//            logger.info("Finished, disconnecting");

        // First transfer attempts
        //            for (int i=0; i<10_000; i++) {
//                int fromIndex = Double.valueOf(Math.random()*100).intValue();
//                if (fromIndex < 0 || fromIndex > 99) System.out.println("BAD VALUUE " + fromIndex);
//                int toIndex = Double.valueOf(Math.random()*100).intValue();
//                if (toIndex < 0 || toIndex > 99) System.out.println("BAD VALUUE " + toIndex);
//                String fromAcct = accountServiceClient.accountNumbers.get(fromIndex);
//                String toAcct = accountServiceClient.accountNumbers.get(toIndex);
//                accountServiceClient.transfer(fromAcct, toAcct, 1000);
//            }
    }

    /** Construct client for accessing server using the existing channel. */
    public AccountServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = AccountGrpc.newBlockingStub(channel);
        futureStub = AccountGrpc.newFutureStub(channel);
    }

    // Blocking version on open, not used any longer
    public String open(String name, int balance) {
        //logger.info("Opening account");
        OpenAccountRequest request = OpenAccountRequest.newBuilder()
                .setAccountName(name)
                .setInitialBalance(balance)
               .build();
        OpenAccountResponse response;
        try {
            response = blockingStub.open(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
        //logger.info("Opened account: " + response.getAccountNumber());
        return response.getAccountNumber();
    }

    /* Non-blocking runnable uses prefix so account names don't repeat between threads */
    class OpenRunnable implements Runnable {
        final String prefix;
        public OpenRunnable(String prefix) { this.prefix = prefix; }
        @Override
        public void run() {
            List<String> openedAccounts = nonBlockingOpen(prefix);
            accountNumbers.addAll(openedAccounts);
            //System.out.println("Open worker " + prefix + " finished opening " + openedAccounts.size() + " accounts");
        }
    }

    public List<String> nonBlockingOpen(String prefix)  {
        //logger.info("Opening account");
        int n = ACCOUNT_COUNT / TRANSFER_THREAD_COUNT;

        List<ListenableFuture<OpenAccountResponse>> futures = new ArrayList<>();
        for (int i=0; i<n; i++) {
            String name = "Acct " + prefix + i;
            int balance = Double.valueOf(Math.random()*10000).intValue()*100;
            OpenAccountRequest request = OpenAccountRequest.newBuilder()
                    .setAccountName(name)
                    .setInitialBalance(balance)
                    .build();
            try {
                futures.add(futureStub.open(request));
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return null;
            }
        }
        //System.out.println("Submitted " + n + " open account requests for group " + prefix);

        try {
            // Can use successfulAsList rather than allAsList to get only good responses
            ListenableFuture<List<OpenAccountResponse>> responseList = Futures.allAsList(futures);
            List<OpenAccountResponse> responses = responseList.get();
            List<String> openedAccountNumbers = new ArrayList<>();
            //System.out.println("Successful response count for group " + prefix + " = " + responses.size());
            for (OpenAccountResponse oar : responses) {
                openedAccountNumbers.add(oar.getAccountNumber());
            }
            return openedAccountNumbers;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public int adjust(String acctNumber, int amount) {
        boolean debit = amount < 0;
        if (debit) amount = -amount;
        AdjustBalanceRequest request = AdjustBalanceRequest.newBuilder()
                .setAccountNumber(acctNumber)
                .setAmount(amount)
                .build();
        AdjustBalanceResponse response;
        try {
            if (debit)
                response = blockingStub.withdraw(request);
            else
                response = blockingStub.deposit(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return 0;
        }

        //logger.info("adjusted account: " + acctNumber + " new balance is " + response.getNewBalance());
        return response.getNewBalance();
    }

    public int checkBalance(String acctNumber) {
        CheckBalanceRequest request = CheckBalanceRequest.newBuilder()
                .setAccountNumber(acctNumber)
                .build();
        CheckBalanceResponse response;
        try {
            response = blockingStub.checkBalance(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return 0;
        }

        return response.getBalance();
    }

    public boolean transfer(String fromAcct, String toAcct, int amount) {

        TransferMoneyRequest request = TransferMoneyRequest.newBuilder()
                .setFromAccountNumber(fromAcct)
                .setToAccountNumber(toAcct)
                .setAmount(amount).build();
        TransferMoneyResponse response;
        try {
            //logger.info("Calling blocking stub for transfer");
            response = blockingStub.transferMoney(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return response.getSucceeded();
    }

    /* Non-blocking runnable uses prefix so account names don't repeat between threads */
    class TransferRunnable implements Runnable {
        final String prefix;
        public TransferRunnable(String prefix) { this.prefix = prefix; }
        @Override
        public void run() {
            int transfersCompleted = nonBlockingTransfer(prefix);
            logger.info("Transfer worker " + prefix + " finished " + transfersCompleted + " transfers");
        }
    }

    // Returns the number of successful transfers
    public int nonBlockingTransfer(String prefix) {
        int n = TRANSFER_COUNT / TRANSFER_THREAD_COUNT;
        logger.info("Transfer handler " + prefix + " initiating " + n + " transfers");

        List<ListenableFuture<TransferMoneyResponse>> futures = new ArrayList<>();

        for (int i=0; i<n; i++) {
            int fromIndex = Double.valueOf(Math.random()*100).intValue();
            if (fromIndex < 0 || fromIndex > 99) System.out.println("BAD VALUE " + fromIndex);
            int toIndex = Double.valueOf(Math.random()*100).intValue();
            if (toIndex < 0 || toIndex > 99) System.out.println("BAD VALUE " + toIndex);
            while (toIndex == fromIndex) {
                // recalc
                toIndex = Double.valueOf(Math.random()*100).intValue();
            }
            String fromAcct = accountNumbers.get(fromIndex);
            String toAcct = accountNumbers.get(toIndex);
            TransferMoneyRequest request = TransferMoneyRequest.newBuilder()
                    .setFromAccountNumber(fromAcct)
                    .setToAccountNumber(toAcct)
                    .setAmount(1000)
                    .build();
            try {
                futures.add(futureStub.transferMoney(request));
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return 0;
            }
        }

        try {
            // Can use successfulAsList rather than allAsList to get only good responses
            ListenableFuture<List<TransferMoneyResponse>> responseList = Futures.allAsList(futures);
            List<TransferMoneyResponse> responses = responseList.get();
            logger.info("Successful transfer response count for group " + prefix + " = " + responses.size());
            return responses.size();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
