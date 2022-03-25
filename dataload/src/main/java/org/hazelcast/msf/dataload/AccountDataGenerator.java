/*
 * Copyright 2018-2022 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.hazelcast.msf.dataload;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.hazelcast.msf.configuration.ServiceConfig;
import org.hazelcast.msfdemo.acctsvc.events.AccountGrpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.*;

public class AccountDataGenerator {

    private static final Logger logger = Logger.getLogger(AccountDataGenerator.class.getName());
    private final AccountGrpc.AccountBlockingStub blockingStub;
    private final AccountGrpc.AccountFutureStub futureStub;
    private ManagedChannel channel;

    private List<String> accountNumbers = new ArrayList<>();
    private static final int OPEN_THREAD_COUNT = 10;

    private ManagedChannel initChannel() {
        ClassLoader cl = AccountDataGenerator.class.getClassLoader();
        ServiceConfig.ServiceProperties props = ServiceConfig.get("dataload.yaml", "account-service", cl);
        String target = props.getTarget();
        logger.info("Target from dataload.yaml " + target);

        channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        return channel;
    }

    private void shutdownChannel() {
        try {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ;
        }
    }

    public int getNumberOfAccounts() {
        ConnectivityState state = channel.getState(true);
        // Can treat IDLE, CONNECTING, READY as good to go
        while( state != ConnectivityState.READY) {
            try {
                Thread.sleep(10_000);
                logger.info("Waiting on acctsvc to be ready, channel state " + state.toString());
                state = channel.getState(true);

            } catch (InterruptedException e) {
                // OK
            }
        }
        logger.info("*** Connected to account service");
        AllAccountsRequest request = AllAccountsRequest.newBuilder().build();
        AllAccountsResponse response = blockingStub.allAccountNumbers(request);
        return response.getAccountNumberCount();
    }

    public void openTestAccounts(int number) {
        List<Thread> openWorkers = new ArrayList<>();
        int accountsPerThread = number / OPEN_THREAD_COUNT;
        for (int i=0; i<OPEN_THREAD_COUNT; i++) {
            Thread t = new Thread(new OpenRunnable("B0" + i, accountsPerThread));
            openWorkers.add(t);
            t.start();
        }

        for (Thread t : openWorkers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //logger.info("Completed account creation.");
        // No - caller will want to query so leave channel open
        //shutdownChannel();
    }


    /** Construct client for accessing server using the existing channel. */
    public AccountDataGenerator() {
        channel = initChannel();

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = AccountGrpc.newBlockingStub(channel);
        futureStub = AccountGrpc.newFutureStub(channel);
    }

    /* Non-blocking runnable uses prefix so account names don't repeat between threads */
    class OpenRunnable implements Runnable {
        final String prefix;
        final int accountsToOpen;
        public OpenRunnable(String prefix, int accounts) {
            this.prefix = prefix;
            this.accountsToOpen = accounts;
        }
        @Override
        public void run() {
            List<String> openedAccounts = nonBlockingOpen(prefix, accountsToOpen);
            accountNumbers.addAll(openedAccounts);
            //System.out.println("Open worker " + prefix + " finished opening " + openedAccounts.size() + " accounts");
        }
    }

    public List<String> nonBlockingOpen(String prefix, int accountsToOpen)  {
        // This should probably be implemented on all services, but since this is the
        // first thing we call just putting it here for now.
        ConnectivityState state = channel.getState(true);
        // Can treat IDLE, CONNECTING, READY as good to go
        while( state != ConnectivityState.READY) {
            try {
                Thread.sleep(10_000);
                logger.info("Waiting on acctsvc to be ready, channel state " + state.toString());
                state = channel.getState(true);

            } catch (InterruptedException e) {
                // OK
            }
        }
        logger.info("*** Connected to account service");

        List<ListenableFuture<OpenAccountResponse>> futures = new ArrayList<>();
        for (int i=0; i<accountsToOpen; i++) {
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
}
