package org.hazelcast.msfdemo.protosvc.events;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.37.1)",
    comments = "Source: catalog.proto")
public final class CatalogGrpc {

  private CatalogGrpc() {}

  public static final String SERVICE_NAME = "catalog.Catalog";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest,
      org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> getPriceLookupMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PriceLookup",
      requestType = org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest.class,
      responseType = org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest,
      org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> getPriceLookupMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest, org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> getPriceLookupMethod;
    if ((getPriceLookupMethod = CatalogGrpc.getPriceLookupMethod) == null) {
      synchronized (CatalogGrpc.class) {
        if ((getPriceLookupMethod = CatalogGrpc.getPriceLookupMethod) == null) {
          CatalogGrpc.getPriceLookupMethod = getPriceLookupMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest, org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PriceLookup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CatalogMethodDescriptorSupplier("PriceLookup"))
              .build();
        }
      }
    }
    return getPriceLookupMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CatalogStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CatalogStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CatalogStub>() {
        @java.lang.Override
        public CatalogStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CatalogStub(channel, callOptions);
        }
      };
    return CatalogStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CatalogBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CatalogBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CatalogBlockingStub>() {
        @java.lang.Override
        public CatalogBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CatalogBlockingStub(channel, callOptions);
        }
      };
    return CatalogBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CatalogFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CatalogFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CatalogFutureStub>() {
        @java.lang.Override
        public CatalogFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CatalogFutureStub(channel, callOptions);
        }
      };
    return CatalogFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class CatalogImplBase implements io.grpc.BindableService {

    /**
     */
    public void priceLookup(org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPriceLookupMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPriceLookupMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest,
                org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse>(
                  this, METHODID_PRICE_LOOKUP)))
          .build();
    }
  }

  /**
   */
  public static final class CatalogStub extends io.grpc.stub.AbstractAsyncStub<CatalogStub> {
    private CatalogStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CatalogStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CatalogStub(channel, callOptions);
    }

    /**
     */
    public void priceLookup(org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPriceLookupMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CatalogBlockingStub extends io.grpc.stub.AbstractBlockingStub<CatalogBlockingStub> {
    private CatalogBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CatalogBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CatalogBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse priceLookup(org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPriceLookupMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CatalogFutureStub extends io.grpc.stub.AbstractFutureStub<CatalogFutureStub> {
    private CatalogFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CatalogFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CatalogFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse> priceLookup(
        org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPriceLookupMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PRICE_LOOKUP = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CatalogImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CatalogImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PRICE_LOOKUP:
          serviceImpl.priceLookup((org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.PriceLookupResponse>) responseObserver);
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

  private static abstract class CatalogBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CatalogBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.hazelcast.msfdemo.protosvc.events.CatalogOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Catalog");
    }
  }

  private static final class CatalogFileDescriptorSupplier
      extends CatalogBaseDescriptorSupplier {
    CatalogFileDescriptorSupplier() {}
  }

  private static final class CatalogMethodDescriptorSupplier
      extends CatalogBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CatalogMethodDescriptorSupplier(String methodName) {
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
      synchronized (CatalogGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CatalogFileDescriptorSupplier())
              .addMethod(getPriceLookupMethod())
              .build();
        }
      }
    }
    return result;
  }
}
