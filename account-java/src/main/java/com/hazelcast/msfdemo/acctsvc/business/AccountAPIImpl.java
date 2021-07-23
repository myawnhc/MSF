/*
 * Copyright 2018-2021 Hazelcast, Inc
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
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msfdemo.acctsvc.business;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.views.AccountDAO;
import com.hazelcast.query.Predicates;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.*;

/** Server-side implementation of the AccountService API
 *  Takes requests and puts them to API-specific IMaps that trigger Jet pipelines
 *  Looks for result in corresponding result map to return to client
 */
public class AccountAPIImpl extends AccountGrpc.AccountImplBase {

    final MSFController controller = MSFController.getInstance();

    // OPEN
    final String openRequestMapName = AccountEventTypes.OPEN.getQualifiedName();
    final IMap<Long, OpenAccountRequest> openPipelineInput = controller.getMap(openRequestMapName);
    final String openResponseMapName = openRequestMapName + ".Results";
    final IMap<Long, APIResponse<String>> openPipelineOutput = controller.getMap(openResponseMapName);

    // ADJUST
    final String adjustRequestMapName = AccountEventTypes.ADJUST.getQualifiedName();
    final IMap<Long, AdjustBalanceRequest> adjustPipelineInput = controller.getMap(adjustRequestMapName);
    final String adjustResponseMapName = adjustRequestMapName + ".Results";
    final IMap<Long, APIResponse<Integer>> adjustPipelineOutput = controller.getMap(adjustResponseMapName);

    final private Map<Long, UUID> listenersByRequestID = new HashMap<>();


    @Override
    public void open(OpenAccountRequest request, StreamObserver<OpenAccountResponse> responseObserver) {
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = openPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<String>>) entryEvent -> {
            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
            APIResponse<String> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                String acctNumber = apiResponse.getResultValue();
                OpenAccountResponse grpcResponse =
                        OpenAccountResponse.newBuilder().setAccountNumber(acctNumber).build();
                responseObserver.onNext(grpcResponse);
            } else {
                responseObserver.onError(apiResponse.getError());
            }
            responseObserver.onCompleted();
            openPipelineOutput.remove(uniqueID);
            openPipelineInput.remove(uniqueID);
            // Remove ourself as a listener.  Have to do this indirection of getting from map because
            // otherwise we get 'listenerID may not have been initialized'.
            UUID myID = listenersByRequestID.remove(uniqueID);
            if (myID == null) {
                //System.out.println("AccountAPIImpl.open handler - listener for " + uniqueID + " was already removed");
            } else {
                openPipelineOutput.removeEntryListener(myID);
            }

        }, Predicates.sql("__key=" + uniqueID), true);

        UUID oldID = listenersByRequestID.put(uniqueID, listenerID);
        if (oldID != null)
            System.out.println("ERROR: Multiple requests with same ID! " + oldID + " (seen in open)");
        // Pass the request into the OpenAccountHandler pipeline
        openPipelineInput.set(uniqueID, request);
    }

    @Override
    public void deposit(AdjustBalanceRequest request, StreamObserver<AdjustBalanceResponse> responseObserver) {
        //System.out.println("deposit requested " + request.getAccountNumber() + " " + request.getAmount());
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = adjustPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<Integer>>) entryEvent -> {
            //System.out.println("ADJUST completion listener fired for ID " + uniqueID);
            APIResponse<Integer> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                Integer newBalance = apiResponse.getResultValue();
                AdjustBalanceResponse grpcResponse =
                       AdjustBalanceResponse.newBuilder().setNewBalance(newBalance).build();
                responseObserver.onNext(grpcResponse);
            } else {
                responseObserver.onError(apiResponse.getError());
            }
            responseObserver.onCompleted();
            adjustPipelineOutput.remove(uniqueID);
            adjustPipelineInput.remove(uniqueID);
            // Remove ourself as a listener.  Have to do this indirection of getting from map because
            // otherwise we get 'listerID may not have been initialized'.
            UUID myID = listenersByRequestID.remove(uniqueID);
            if (myID == null) {
//                if (request.getAmount() > 0)
//                    System.out.println("AccountAPIImpl.deposit    - listener for " + uniqueID + " was already removed");
//                else
//                    System.out.println("AccountAPIImpl.withdrawal - listener for " + uniqueID + " was already removed");

            } else {
                adjustPipelineOutput.removeEntryListener(myID);
            }
        }, Predicates.sql("__key=" + uniqueID), true);

        UUID oldID = listenersByRequestID.put(uniqueID, listenerID);
        if (oldID != null)
            System.out.println("ERROR: Multiple requests with same ID! " + oldID + " (seen in deposit)");
        // Pass the request into the AdjustBalancePipeline
        adjustPipelineInput.set(uniqueID, request);
    }

    @Override
    public void withdraw(AdjustBalanceRequest request, StreamObserver<AdjustBalanceResponse> responseObserver) {
        //System.out.println("withdrawal requested " + request.getAccountNumber() + " " + request.getAmount());
        AdjustBalanceRequest withdrawal = AdjustBalanceRequest.newBuilder(request)
                .setAmount(request.getAmount() * -1)
                .build();
        deposit(withdrawal, responseObserver);

    }

    @Override
    public void checkBalance(CheckBalanceRequest request, StreamObserver<CheckBalanceResponse> responseObserver) {
        AccountDAO dao = new AccountDAO();
        String acctNumber = request.getAccountNumber();
        Account account = dao.findByKey(acctNumber);
        if (account == null) {
            Exception e = new IllegalArgumentException("Account Number does not exist: :" + acctNumber);
            responseObserver.onError(e);
        } else {
            System.out.println("Balance is " + account.getBalance());
            CheckBalanceResponse response = CheckBalanceResponse.newBuilder()
                    .setBalance(account.getBalance())
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void transferMoney(TransferMoneyRequest request, StreamObserver<TransferMoneyResponse> responseObserver) {
        //System.out.println("transfer requested " + request.getFromAccountNumber() + " to " + request.getToAccountNumber() + " " + request.getAmount());

        AdjustBalanceRequest withdrawal = AdjustBalanceRequest.newBuilder()
                .setAccountNumber(request.getFromAccountNumber())
                // Note that withdraw() will flip sign of the amount so don't do it here.
                .setAmount(request.getAmount())
                .build();

        AdjustBalanceRequest deposit = AdjustBalanceRequest.newBuilder()
                .setAccountNumber(request.getToAccountNumber())
                .setAmount(request.getAmount())
                .build();

        final CountDownLatch latch = new CountDownLatch(2);  // Simple Java, not HZ's distributed CPSubsystem one

        withdraw(withdrawal, new StreamObserver<>() {
                    @Override
                    public void onNext(AdjustBalanceResponse adjustBalanceResponse) {
                        //System.out.println("Withdrawal processed");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // pass to our caller
                        responseObserver.onError(throwable);
                    }

                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }
                });

        deposit(deposit, new StreamObserver<>() {
            @Override
            public void onNext(AdjustBalanceResponse adjustBalanceResponse) {
                //System.out.println("Deposit processed");
            }

            @Override
            public void onError(Throwable throwable) {
                // pass to our caller
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            latch.await(); // TODO: maybe use a timer here in case of issues with pipeline
            //System.out.println("Both halves complete, sending response");
            TransferMoneyResponse response = TransferMoneyResponse.newBuilder()
                    .setSucceeded(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (InterruptedException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }

    }

    @Override
    public void allAccountNumbers(AllAccountsRequest request, StreamObserver<AllAccountsResponse> responseObserver) {
        AccountDAO dao = new AccountDAO();
        Collection<Account> accounts = dao.getAllAccounts();
        List<String> accountNumbers = new ArrayList<>();
        for (Account a : accounts) {
            accountNumbers.add(a.getAcctNumber());
        }
        AllAccountsResponse.Builder responseBuilder = AllAccountsResponse.newBuilder();
        responseBuilder.addAllAccountNumber(accountNumbers);
        AllAccountsResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void totalAccountBalances(TotalBalanceRequest request, StreamObserver<TotalBalanceResponse> responseObserver) {
        AccountDAO dao = new AccountDAO();
        long total = dao.getTotalAccountBalances();

        TotalBalanceResponse response = TotalBalanceResponse.newBuilder()
                .setTotalBalance(total)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}