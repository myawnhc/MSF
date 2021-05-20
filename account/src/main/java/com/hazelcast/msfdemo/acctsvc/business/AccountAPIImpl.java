package com.hazelcast.msfdemo.acctsvc.business;

import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.messaging.APIResponse;
import com.hazelcast.msfdemo.acctsvc.domain.Account;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;
import com.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest;
import com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse;
import com.hazelcast.msfdemo.acctsvc.service.AccountService;
import com.hazelcast.msfdemo.acctsvc.views.AccountDAO;
import com.hazelcast.query.Predicates;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.*;

/** Server-side implementation of the AccountService Open API
 *  Takes requests and puts them to API-specific IMap that triggers Jet pipeline
 *  Looks for result in corresponding result map to return to client
 */
public class AccountAPIImpl extends AccountGrpc.AccountImplBase {

    MSFController controller = MSFController.getInstance();

    // OPEN
    String openRequestMapName = AccountEventTypes.OPEN.getQualifiedName();
    IMap<Long, OpenAccountRequest> openPipelineInput = controller.getMap(openRequestMapName);
    String openResponseMapName = openRequestMapName + "Results";
    IMap<Long, APIResponse<String>> openPipelineOutput = controller.getMap(openResponseMapName);

    // ADJUST
    String adjustRequestMapName = AccountEventTypes.ADJUST.getQualifiedName();
    IMap<Long, AdjustBalanceRequest> adjustPipelineInput = controller.getMap(adjustRequestMapName);
    String adjustResponseMapName = adjustRequestMapName + "Results";
    IMap<Long, APIResponse<Integer>> adjustPipelineOutput = controller.getMap(adjustResponseMapName);

    private List<UUID> openPipelineOutputlistenersToCleanUp = new ArrayList<>();
    private List<UUID> adjustPipelineOutputlistenersToCleanUp = new ArrayList<>();

    @Override
    public void open(OpenAccountRequest request, StreamObserver<OpenAccountResponse> responseObserver) {
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // We should never have more listeners attached than there are threads
        // feeding us Open events.  We should configure that somewhere, but for
        // now let's assume it won't be greater than 10.
        while (openPipelineOutputlistenersToCleanUp.size() > 10) {
            UUID id = openPipelineOutputlistenersToCleanUp.remove(0);
            //System.out.println("Cleaned up listener " + id); // temp
        }

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
        }, Predicates.sql("__key=" + uniqueID), true);

        // Pass the request into the OpenAccountHandler pipeline
        openPipelineInput.set(uniqueID, request);
        openPipelineOutputlistenersToCleanUp.add(listenerID);
    }

    @Override
    public void deposit(AdjustBalanceRequest request, StreamObserver<AdjustBalanceResponse> responseObserver) {
        System.out.println("deposit requested " + request.getAccountNumber() + " " + request.getAmount());
        // Unique ID used to pair up requests with responses
        long uniqueID = controller.getUniqueMessageID();

        // We should never have more listeners attached than there are threads
        // feeding us Open events.  We should configure that somewhere, but for
        // now let's assume it won't be greater than 10.
        while (adjustPipelineOutputlistenersToCleanUp.size() > 10) {
            UUID id = adjustPipelineOutputlistenersToCleanUp.remove(0);
            //System.out.println("Cleaned up listener " + id); // temp
        }

        // Get listener to result map armed before we trigger the pipeline
        UUID listenerID = adjustPipelineOutput.addEntryListener((EntryAddedListener<Long, APIResponse<Integer>>) entryEvent -> {
            System.out.println("ADJUST completion listener fired for ID " + uniqueID);
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
        }, Predicates.sql("__key=" + uniqueID), true);

        // Pass the request into the AdjustBalancePipeline
        adjustPipelineInput.set(uniqueID, request);
        adjustPipelineOutputlistenersToCleanUp.add(listenerID);
    }

    @Override
    public void withdraw(AdjustBalanceRequest request, StreamObserver<AdjustBalanceResponse> responseObserver) {
        System.out.println("withdrawal requested " + request.getAccountNumber() + " " + request.getAmount());
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
}