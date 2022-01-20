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

package org.hazelcast.msfdemo.acctsvc.business;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.query.Predicates;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msf.controller.MSFController;
import org.hazelcast.msf.messaging.APIResponse;
import org.hazelcast.msfdemo.acctsvc.domain.Account;
import org.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import org.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;
import org.hazelcast.msfdemo.acctsvc.views.AccountDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/** Server-side implementation of the AccountService API
 *  Takes requests and puts them to API-specific IMaps that trigger Jet pipelines
 *  Looks for result in corresponding result map to return to client
 */
public class AccountAPIImpl extends AccountGrpc.AccountImplBase {

    final MSFController controller = MSFController.getInstance();

    // OPEN
    final String openRequestMapName = AccountEventTypes.OPEN.getQualifiedName();
    final IMap<Long, AccountOuterClass.OpenAccountRequest> openPipelineInput = controller.getMap(openRequestMapName);
    final String openResponseMapName = openRequestMapName + ".Results";
    final IMap<Long, APIResponse<String>> openPipelineOutput = controller.getMap(openResponseMapName);

    // ADJUST
    final String adjustRequestMapName = AccountEventTypes.ADJUST.getQualifiedName();
    final IMap<Long, AccountOuterClass.AdjustBalanceRequest> adjustPipelineInput = controller.getMap(adjustRequestMapName);
    final String adjustResponseMapName = adjustRequestMapName + ".Results";
    final IMap<Long, APIResponse<Integer>> adjustPipelineOutput = controller.getMap(adjustResponseMapName);

    final private Map<Long, UUID> listenersByRequestID = new HashMap<>();

    private AccountDAO accountDAO = new AccountDAO(controller);


    @Override
    public void open(AccountOuterClass.OpenAccountRequest request, StreamObserver<AccountOuterClass.OpenAccountResponse> responseObserver) {
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = openPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<String>>) entryEvent -> {
            //System.out.println("OPEN completion listener fired for ID " + uniqueID);
            APIResponse<String> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                String acctNumber = apiResponse.getResultValue();
                AccountOuterClass.OpenAccountResponse grpcResponse =
                        AccountOuterClass.OpenAccountResponse.newBuilder().setAccountNumber(acctNumber).build();
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
    public void deposit(AccountOuterClass.AdjustBalanceRequest request, StreamObserver<AccountOuterClass.AdjustBalanceResponse> responseObserver) {
        //System.out.println("deposit requested " + request.getAccountNumber() + " " + request.getAmount());
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = adjustPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<Integer>>) entryEvent -> {
            //System.out.println("ADJUST completion listener fired for ID " + uniqueID);
            APIResponse<Integer> apiResponse = entryEvent.getValue();
            if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                Integer newBalance = apiResponse.getResultValue();
                AccountOuterClass.AdjustBalanceResponse grpcResponse =
                       AccountOuterClass.AdjustBalanceResponse.newBuilder().setNewBalance(newBalance).build();
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
    public void withdraw(AccountOuterClass.AdjustBalanceRequest request, StreamObserver<AccountOuterClass.AdjustBalanceResponse> responseObserver) {
        //System.out.println("withdrawal requested " + request.getAccountNumber() + " " + request.getAmount());
        AccountOuterClass.AdjustBalanceRequest withdrawal = AccountOuterClass.AdjustBalanceRequest.newBuilder(request)
                .setAmount(request.getAmount() * -1)
                .build();
        deposit(withdrawal, responseObserver);
    }

    @Override
    public void payment(AccountOuterClass.AdjustBalanceRequest request, StreamObserver<AccountOuterClass.AdjustBalanceResponse> responseObserver) {
        //System.out.println("withdrawal requested " + request.getAccountNumber() + " " + request.getAmount());
        // Just like withdrawals, we flip the sign and use the method to handle the transaction
        AccountOuterClass.AdjustBalanceRequest payment = AccountOuterClass.AdjustBalanceRequest.newBuilder(request)
                .setAmount(request.getAmount() * -1)
                .build();
        deposit(payment, responseObserver);
    }

    @Override
    public void checkBalance(AccountOuterClass.CheckBalanceRequest request, StreamObserver<AccountOuterClass.CheckBalanceResponse> responseObserver) {
        String acctNumber = request.getAccountNumber();
        Account account = accountDAO.findByKey(acctNumber);
        if (account == null) {
            Exception e = new IllegalArgumentException("Account Number does not exist: :" + acctNumber);
            responseObserver.onError(e);
        } else {
            System.out.println("Balance is " + account.getBalance());
            AccountOuterClass.CheckBalanceResponse response = AccountOuterClass.CheckBalanceResponse.newBuilder()
                    .setBalance(account.getBalance())
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void requestAuth(AccountOuterClass.AuthorizationRequest request, StreamObserver<AccountOuterClass.AuthorizationResponse> responseObserver) {
        String acctNumber = request.getAccountNumber();
        Account account = accountDAO.findByKey(acctNumber);
        boolean approved = false;
        if (account == null) {
            Exception e = new IllegalArgumentException("Account Number does not exist: :" + acctNumber);
            responseObserver.onError(e);
        } else {
            if (account.getBalance() >= request.getRequestedAmount())
                approved = true;
            AccountOuterClass.AuthorizationResponse response = AccountOuterClass.AuthorizationResponse.newBuilder()
                    .setApproved(approved)
                    .build();
            responseObserver.onNext(response);
        }
        System.out.println("Requested auth for " + request.getRequestedAmount() + ", balance is " +
                account.getBalance() + ", approved=" + approved);
        responseObserver.onCompleted();
    }

    @Override
    public void transferMoney(AccountOuterClass.TransferMoneyRequest request, StreamObserver<AccountOuterClass.TransferMoneyResponse> responseObserver) {
        //System.out.println("transfer requested " + request.getFromAccountNumber() + " to " + request.getToAccountNumber() + " " + request.getAmount());

        AccountOuterClass.AdjustBalanceRequest withdrawal = AccountOuterClass.AdjustBalanceRequest.newBuilder()
                .setAccountNumber(request.getFromAccountNumber())
                // Note that withdraw() will flip sign of the amount so don't do it here.
                .setAmount(request.getAmount())
                .build();

        AccountOuterClass.AdjustBalanceRequest deposit = AccountOuterClass.AdjustBalanceRequest.newBuilder()
                .setAccountNumber(request.getToAccountNumber())
                .setAmount(request.getAmount())
                .build();

        final CountDownLatch latch = new CountDownLatch(2);  // Simple Java, not HZ's distributed CPSubsystem one

        withdraw(withdrawal, new StreamObserver<>() {
                    @Override
                    public void onNext(AccountOuterClass.AdjustBalanceResponse adjustBalanceResponse) {
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
            public void onNext(AccountOuterClass.AdjustBalanceResponse adjustBalanceResponse) {
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
            AccountOuterClass.TransferMoneyResponse response = AccountOuterClass.TransferMoneyResponse.newBuilder()
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
    public void allAccountNumbers(AccountOuterClass.AllAccountsRequest request, StreamObserver<AccountOuterClass.AllAccountsResponse> responseObserver) {
        Collection<Account> accounts = accountDAO.getAllAccounts();
        List<String> accountNumbers = new ArrayList<>();
        for (Account a : accounts) {
            accountNumbers.add(a.getAcctNumber());
        }
        AccountOuterClass.AllAccountsResponse.Builder responseBuilder = AccountOuterClass.AllAccountsResponse.newBuilder();
        responseBuilder.addAllAccountNumber(accountNumbers);
        AccountOuterClass.AllAccountsResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void totalAccountBalances(AccountOuterClass.TotalBalanceRequest request, StreamObserver<AccountOuterClass.TotalBalanceResponse> responseObserver) {
        long total = accountDAO.getTotalAccountBalances();

        AccountOuterClass.TotalBalanceResponse response = AccountOuterClass.TotalBalanceResponse.newBuilder()
                .setTotalBalance(total)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}