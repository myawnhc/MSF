package com.hazelcast.msf.testclient;


import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.*;
import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;
import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse;

public class Client  {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private final AccountGrpc.AccountBlockingStub blockingStub;

    private List<String> accountNumbers = new ArrayList<>();

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
            Client client = new Client(channel);

            // Open 1000 accounts
            logger.info("Opening 1000 accounts - currently single-threaded");
            // TODO: Should be multithreaded for this.
            for(int i = 0; i<1000; i++) { // TODO: bump up when working
                String acctName = "Acct " + i;
                int beginningBalance = Double.valueOf(Math.random()*10000).intValue()*100;
                client.accountNumbers.add(client.open(acctName, beginningBalance));
            }
            logger.info("Number of account numbers opened : " + client.accountNumbers.size());

            // TODO: Test deposit, withdrawal
            String acctNumber = client.accountNumbers.get(0);
            int balance = client.checkBalance(acctNumber);
            System.out.printf("Account %s initial balance %d\n", acctNumber, balance);
            logger.info("Crediting account " + acctNumber);
            client.adjust(acctNumber, 200);
            logger.info("Debiting one account");
            client.adjust(acctNumber, -25);
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
    public Client(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = AccountGrpc.newBlockingStub(channel);
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
}
