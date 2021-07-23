package com.hazelcast.msfdemo.acctsvc.events;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.37.1)",
    comments = "Source: Account.proto")
public final class AccountGrpc {

  private AccountGrpc() {}

  public static final String SERVICE_NAME = "com.hazelcast.msfdemo.acctsvc.events.Account";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> getOpenMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Open",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> getOpenMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> getOpenMethod;
    if ((getOpenMethod = AccountGrpc.getOpenMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getOpenMethod = AccountGrpc.getOpenMethod) == null) {
          AccountGrpc.getOpenMethod = getOpenMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Open"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("Open"))
              .build();
        }
      }
    }
    return getOpenMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getDepositMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Deposit",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getDepositMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getDepositMethod;
    if ((getDepositMethod = AccountGrpc.getDepositMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getDepositMethod = AccountGrpc.getDepositMethod) == null) {
          AccountGrpc.getDepositMethod = getDepositMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Deposit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("Deposit"))
              .build();
        }
      }
    }
    return getDepositMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getWithdrawMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Withdraw",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getWithdrawMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> getWithdrawMethod;
    if ((getWithdrawMethod = AccountGrpc.getWithdrawMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getWithdrawMethod = AccountGrpc.getWithdrawMethod) == null) {
          AccountGrpc.getWithdrawMethod = getWithdrawMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Withdraw"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("Withdraw"))
              .build();
        }
      }
    }
    return getWithdrawMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> getCheckBalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckBalance",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> getCheckBalanceMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> getCheckBalanceMethod;
    if ((getCheckBalanceMethod = AccountGrpc.getCheckBalanceMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getCheckBalanceMethod = AccountGrpc.getCheckBalanceMethod) == null) {
          AccountGrpc.getCheckBalanceMethod = getCheckBalanceMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckBalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("CheckBalance"))
              .build();
        }
      }
    }
    return getCheckBalanceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> getTransferMoneyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransferMoney",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> getTransferMoneyMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> getTransferMoneyMethod;
    if ((getTransferMoneyMethod = AccountGrpc.getTransferMoneyMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getTransferMoneyMethod = AccountGrpc.getTransferMoneyMethod) == null) {
          AccountGrpc.getTransferMoneyMethod = getTransferMoneyMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TransferMoney"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("TransferMoney"))
              .build();
        }
      }
    }
    return getTransferMoneyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> getAllAccountNumbersMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AllAccountNumbers",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> getAllAccountNumbersMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> getAllAccountNumbersMethod;
    if ((getAllAccountNumbersMethod = AccountGrpc.getAllAccountNumbersMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getAllAccountNumbersMethod = AccountGrpc.getAllAccountNumbersMethod) == null) {
          AccountGrpc.getAllAccountNumbersMethod = getAllAccountNumbersMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AllAccountNumbers"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("AllAccountNumbers"))
              .build();
        }
      }
    }
    return getAllAccountNumbersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> getTotalAccountBalancesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TotalAccountBalances",
      requestType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest.class,
      responseType = com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest,
      com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> getTotalAccountBalancesMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> getTotalAccountBalancesMethod;
    if ((getTotalAccountBalancesMethod = AccountGrpc.getTotalAccountBalancesMethod) == null) {
      synchronized (AccountGrpc.class) {
        if ((getTotalAccountBalancesMethod = AccountGrpc.getTotalAccountBalancesMethod) == null) {
          AccountGrpc.getTotalAccountBalancesMethod = getTotalAccountBalancesMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest, com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TotalAccountBalances"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountMethodDescriptorSupplier("TotalAccountBalances"))
              .build();
        }
      }
    }
    return getTotalAccountBalancesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccountStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountStub>() {
        @java.lang.Override
        public AccountStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountStub(channel, callOptions);
        }
      };
    return AccountStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccountBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountBlockingStub>() {
        @java.lang.Override
        public AccountBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountBlockingStub(channel, callOptions);
        }
      };
    return AccountBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccountFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountFutureStub>() {
        @java.lang.Override
        public AccountFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountFutureStub(channel, callOptions);
        }
      };
    return AccountFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class AccountImplBase implements io.grpc.BindableService {

    /**
     */
    public void open(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getOpenMethod(), responseObserver);
    }

    /**
     */
    public void deposit(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDepositMethod(), responseObserver);
    }

    /**
     */
    public void withdraw(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWithdrawMethod(), responseObserver);
    }

    /**
     */
    public void checkBalance(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckBalanceMethod(), responseObserver);
    }

    /**
     */
    public void transferMoney(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTransferMoneyMethod(), responseObserver);
    }

    /**
     */
    public void allAccountNumbers(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAllAccountNumbersMethod(), responseObserver);
    }

    /**
     */
    public void totalAccountBalances(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTotalAccountBalancesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getOpenMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse>(
                  this, METHODID_OPEN)))
          .addMethod(
            getDepositMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>(
                  this, METHODID_DEPOSIT)))
          .addMethod(
            getWithdrawMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>(
                  this, METHODID_WITHDRAW)))
          .addMethod(
            getCheckBalanceMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse>(
                  this, METHODID_CHECK_BALANCE)))
          .addMethod(
            getTransferMoneyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse>(
                  this, METHODID_TRANSFER_MONEY)))
          .addMethod(
            getAllAccountNumbersMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse>(
                  this, METHODID_ALL_ACCOUNT_NUMBERS)))
          .addMethod(
            getTotalAccountBalancesMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest,
                com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse>(
                  this, METHODID_TOTAL_ACCOUNT_BALANCES)))
          .build();
    }
  }

  /**
   */
  public static final class AccountStub extends io.grpc.stub.AbstractAsyncStub<AccountStub> {
    private AccountStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountStub(channel, callOptions);
    }

    /**
     */
    public void open(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getOpenMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deposit(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDepositMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void withdraw(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWithdrawMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkBalance(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckBalanceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transferMoney(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTransferMoneyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void allAccountNumbers(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAllAccountNumbersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void totalAccountBalances(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTotalAccountBalancesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AccountBlockingStub extends io.grpc.stub.AbstractBlockingStub<AccountBlockingStub> {
    private AccountBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse open(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getOpenMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse deposit(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDepositMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse withdraw(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getWithdrawMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse checkBalance(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckBalanceMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse transferMoney(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTransferMoneyMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse allAccountNumbers(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAllAccountNumbersMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse totalAccountBalances(com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTotalAccountBalancesMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AccountFutureStub extends io.grpc.stub.AbstractFutureStub<AccountFutureStub> {
    private AccountFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse> open(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getOpenMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> deposit(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDepositMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse> withdraw(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWithdrawMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse> checkBalance(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckBalanceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse> transferMoney(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTransferMoneyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse> allAccountNumbers(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAllAccountNumbersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse> totalAccountBalances(
        com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTotalAccountBalancesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_OPEN = 0;
  private static final int METHODID_DEPOSIT = 1;
  private static final int METHODID_WITHDRAW = 2;
  private static final int METHODID_CHECK_BALANCE = 3;
  private static final int METHODID_TRANSFER_MONEY = 4;
  private static final int METHODID_ALL_ACCOUNT_NUMBERS = 5;
  private static final int METHODID_TOTAL_ACCOUNT_BALANCES = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AccountImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AccountImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_OPEN:
          serviceImpl.open((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.OpenAccountResponse>) responseObserver);
          break;
        case METHODID_DEPOSIT:
          serviceImpl.deposit((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>) responseObserver);
          break;
        case METHODID_WITHDRAW:
          serviceImpl.withdraw((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AdjustBalanceResponse>) responseObserver);
          break;
        case METHODID_CHECK_BALANCE:
          serviceImpl.checkBalance((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.CheckBalanceResponse>) responseObserver);
          break;
        case METHODID_TRANSFER_MONEY:
          serviceImpl.transferMoney((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TransferMoneyResponse>) responseObserver);
          break;
        case METHODID_ALL_ACCOUNT_NUMBERS:
          serviceImpl.allAccountNumbers((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.AllAccountsResponse>) responseObserver);
          break;
        case METHODID_TOTAL_ACCOUNT_BALANCES:
          serviceImpl.totalAccountBalances((com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.TotalBalanceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class AccountBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccountBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hazelcast.msfdemo.acctsvc.events.AccountOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Account");
    }
  }

  private static final class AccountFileDescriptorSupplier
      extends AccountBaseDescriptorSupplier {
    AccountFileDescriptorSupplier() {}
  }

  private static final class AccountMethodDescriptorSupplier
      extends AccountBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AccountMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AccountGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccountFileDescriptorSupplier())
              .addMethod(getOpenMethod())
              .addMethod(getDepositMethod())
              .addMethod(getWithdrawMethod())
              .addMethod(getCheckBalanceMethod())
              .addMethod(getTransferMoneyMethod())
              .addMethod(getAllAccountNumbersMethod())
              .addMethod(getTotalAccountBalancesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
