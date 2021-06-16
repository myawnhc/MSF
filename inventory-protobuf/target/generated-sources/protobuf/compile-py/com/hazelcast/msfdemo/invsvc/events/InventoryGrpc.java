package com.hazelcast.msfdemo.invsvc.events;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.37.1)",
    comments = "Source: inventory.proto")
public final class InventoryGrpc {

  private InventoryGrpc() {}

  public static final String SERVICE_NAME = "inventory.Inventory";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailability",
      requestType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.class,
      responseType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod;
    if ((getCheckAvailabilityMethod = InventoryGrpc.getCheckAvailabilityMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getCheckAvailabilityMethod = InventoryGrpc.getCheckAvailabilityMethod) == null) {
          InventoryGrpc.getCheckAvailabilityMethod = getCheckAvailabilityMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("CheckAvailability"))
              .build();
        }
      }
    }
    return getCheckAvailabilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailabilityAllLocations",
      requestType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.class,
      responseType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod;
    if ((getCheckAvailabilityAllLocationsMethod = InventoryGrpc.getCheckAvailabilityAllLocationsMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getCheckAvailabilityAllLocationsMethod = InventoryGrpc.getCheckAvailabilityAllLocationsMethod) == null) {
          InventoryGrpc.getCheckAvailabilityAllLocationsMethod = getCheckAvailabilityAllLocationsMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailabilityAllLocations"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("CheckAvailabilityAllLocations"))
              .build();
        }
      }
    }
    return getCheckAvailabilityAllLocationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ship",
      requestType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest.class,
      responseType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod;
    if ((getShipMethod = InventoryGrpc.getShipMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getShipMethod = InventoryGrpc.getShipMethod) == null) {
          InventoryGrpc.getShipMethod = getShipMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ship"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Ship"))
              .build();
        }
      }
    }
    return getShipMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Reserve",
      requestType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.class,
      responseType = com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod() {
    io.grpc.MethodDescriptor<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod;
    if ((getReserveMethod = InventoryGrpc.getReserveMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getReserveMethod = InventoryGrpc.getReserveMethod) == null) {
          InventoryGrpc.getReserveMethod = getReserveMethod =
              io.grpc.MethodDescriptor.<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Reserve"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Reserve"))
              .build();
        }
      }
    }
    return getReserveMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InventoryStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryStub>() {
        @java.lang.Override
        public InventoryStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryStub(channel, callOptions);
        }
      };
    return InventoryStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InventoryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryBlockingStub>() {
        @java.lang.Override
        public InventoryBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryBlockingStub(channel, callOptions);
        }
      };
    return InventoryBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InventoryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InventoryFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InventoryFutureStub>() {
        @java.lang.Override
        public InventoryFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InventoryFutureStub(channel, callOptions);
        }
      };
    return InventoryFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InventoryImplBase implements io.grpc.BindableService {

    /**
     */
    public void checkAvailability(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityMethod(), responseObserver);
    }

    /**
     */
    public void checkAvailabilityAllLocations(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityAllLocationsMethod(), responseObserver);
    }

    /**
     */
    public void ship(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShipMethod(), responseObserver);
    }

    /**
     */
    public void reserve(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReserveMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCheckAvailabilityMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>(
                  this, METHODID_CHECK_AVAILABILITY)))
          .addMethod(
            getCheckAvailabilityAllLocationsMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>(
                  this, METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS)))
          .addMethod(
            getShipMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>(
                  this, METHODID_SHIP)))
          .addMethod(
            getReserveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
                com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>(
                  this, METHODID_RESERVE)))
          .build();
    }
  }

  /**
   */
  public static final class InventoryStub extends io.grpc.stub.AbstractAsyncStub<InventoryStub> {
    private InventoryStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryStub(channel, callOptions);
    }

    /**
     */
    public void checkAvailability(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkAvailabilityAllLocations(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getCheckAvailabilityAllLocationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ship(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShipMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reserve(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InventoryBlockingStub extends io.grpc.stub.AbstractBlockingStub<InventoryBlockingStub> {
    private InventoryBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse checkAvailability(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckAvailabilityMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> checkAvailabilityAllLocations(
        com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getCheckAvailabilityAllLocationsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse ship(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getShipMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse reserve(com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReserveMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InventoryFutureStub extends io.grpc.stub.AbstractFutureStub<InventoryFutureStub> {
    private InventoryFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InventoryFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InventoryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> checkAvailability(
        com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> ship(
        com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShipMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> reserve(
        com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_AVAILABILITY = 0;
  private static final int METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS = 1;
  private static final int METHODID_SHIP = 2;
  private static final int METHODID_RESERVE = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InventoryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InventoryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK_AVAILABILITY:
          serviceImpl.checkAvailability((com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>) responseObserver);
          break;
        case METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS:
          serviceImpl.checkAvailabilityAllLocations((com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>) responseObserver);
          break;
        case METHODID_SHIP:
          serviceImpl.ship((com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>) responseObserver);
          break;
        case METHODID_RESERVE:
          serviceImpl.reserve((com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest) request,
              (io.grpc.stub.StreamObserver<com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>) responseObserver);
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

  private static abstract class InventoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InventoryBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Inventory");
    }
  }

  private static final class InventoryFileDescriptorSupplier
      extends InventoryBaseDescriptorSupplier {
    InventoryFileDescriptorSupplier() {}
  }

  private static final class InventoryMethodDescriptorSupplier
      extends InventoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InventoryMethodDescriptorSupplier(String methodName) {
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
      synchronized (InventoryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InventoryFileDescriptorSupplier())
              .addMethod(getCheckAvailabilityMethod())
              .addMethod(getCheckAvailabilityAllLocationsMethod())
              .addMethod(getShipMethod())
              .addMethod(getReserveMethod())
              .build();
        }
      }
    }
    return result;
  }
}
