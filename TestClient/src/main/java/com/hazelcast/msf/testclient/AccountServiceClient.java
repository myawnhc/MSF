package com.hazelcast.msf.testclient;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
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
    private static final int THREAD_COUNT = 1;
    private static final int ACCOUNT_COUNT = 1000;
    private static final int TRANSFER_COUNT = 1_000_000;

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
        try {
            AccountServiceClient accountServiceClient = new AccountServiceClient(channel);

            // Open 1000 accounts
            logger.info("Opening 1000 accounts - currently single-threaded");
            // TODO: Should be multithreaded for this.
            long start = System.currentTimeMillis();
//            for(int i = 0; i<1000; i++) { // TODO: bump up when working
//                String acctName = "Acct " + i;
//                int beginningBalance = Double.valueOf(Math.random()*10000).intValue()*100;
//                accountServiceClient.accountNumbers.add(accountServiceClient.open(acctName, beginningBalance));
//            }

            // This works well
            accountServiceClient.accountNumbers.addAll(accountServiceClient.nonBlockingOpen("A"));

//            // This is failure - Future failures.
//            for (int i=0; i<THREAD_COUNT; i++) {
//                Thread t = new Thread(accountServiceClient.new NboRunnable("B0" + i));
//                t.start();
//            }

            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Opened 1000 accounts single threaded non-blocking in " + elapsed + "ms");
            logger.info("Number of account numbers opened : " + accountServiceClient.accountNumbers.size());

            // TODO: Test deposit, withdrawal
            String acctNumber = accountServiceClient.accountNumbers.get(0);
            int balance = accountServiceClient.checkBalance(acctNumber);
            System.out.printf("Account %s initial balance %d\n", acctNumber, balance);
            logger.info("Crediting account " + acctNumber);
            accountServiceClient.adjust(acctNumber, 200);
            logger.info("Debiting one account");
            accountServiceClient.adjust(acctNumber, -25);
            logger.info("Finished, disconnecting");

            // TODO: Test transfer money between accounts
            // TODO: check account balances
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    public AccountServiceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = AccountGrpc.newBlockingStub(channel);
        // Not sure if it's legit to have blocking and future share a channel,
        // but when cutting over everything will probably switch to future at once
        futureStub = AccountGrpc.newFutureStub(channel);
    }

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

    class NboRunnable implements Runnable {
        final String prefix;
        public NboRunnable(String prefix) { this.prefix = prefix; }
        @Override
        public void run() {
            List<String> openedAccounts = nonBlockingOpen(prefix);
            accountNumbers.addAll(openedAccounts);
        }
    }

    // TODO: wrap in a Runnable
    public List<String> nonBlockingOpen(String prefix)  {
        //logger.info("Opening account");
        int n = ACCOUNT_COUNT / THREAD_COUNT;

        List<ListenableFuture<OpenAccountResponse>> futures = new ArrayList<>();
        for (int i=0; i<n; i++) {
            String name = "Acct " + prefix + i; // TODO: will cause duplication across threads!
            int balance = Double.valueOf(Math.random()*10000).intValue()*100;
            OpenAccountRequest request = OpenAccountRequest.newBuilder()
                    .setAccountName(name)
                    .setInitialBalance(balance)
                    .build();
            try {
                //response = blockingStub.open(request);
                futures.add(futureStub.open(request));
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return null;
            }
        }
        //logger.info("Opened account: " + response.getAccountNumber());
        ListenableFuture<OpenAccountResponse> response;

        try {
            // Can use successfulAsList rather than allAsList to get only good responses
            ListenableFuture<List<OpenAccountResponse>> responseList = Futures.successfulAsList(futures);
            List<OpenAccountResponse> responses = responseList.get();
            List<String> openedAccountNumbers = new ArrayList<>();
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

        logger.info("adjusted account: " + acctNumber + " new balance is " + response.getNewBalance());
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
            response = blockingStub.transferMoney(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        return response.getSucceeded();
    }
}
