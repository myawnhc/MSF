package org.hazelcast.msfdemo.ordersvc.events;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.37.1)",
    comments = "Source: order.proto")
public final class OrderGrpc {

  private OrderGrpc() {}

  public static final String SERVICE_NAME = "order.Order";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> getCreateOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateOrder",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> getCreateOrderMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> getCreateOrderMethod;
    if ((getCreateOrderMethod = OrderGrpc.getCreateOrderMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getCreateOrderMethod = OrderGrpc.getCreateOrderMethod) == null) {
          OrderGrpc.getCreateOrderMethod = getCreateOrderMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("CreateOrder"))
              .build();
        }
      }
    }
    return getCreateOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> getSubscribeToOrderCreatedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToOrderCreated",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> getSubscribeToOrderCreatedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> getSubscribeToOrderCreatedMethod;
    if ((getSubscribeToOrderCreatedMethod = OrderGrpc.getSubscribeToOrderCreatedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToOrderCreatedMethod = OrderGrpc.getSubscribeToOrderCreatedMethod) == null) {
          OrderGrpc.getSubscribeToOrderCreatedMethod = getSubscribeToOrderCreatedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToOrderCreated"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToOrderCreated"))
              .build();
        }
      }
    }
    return getSubscribeToOrderCreatedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> getSubscribeToOrderPricedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToOrderPriced",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> getSubscribeToOrderPricedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> getSubscribeToOrderPricedMethod;
    if ((getSubscribeToOrderPricedMethod = OrderGrpc.getSubscribeToOrderPricedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToOrderPricedMethod = OrderGrpc.getSubscribeToOrderPricedMethod) == null) {
          OrderGrpc.getSubscribeToOrderPricedMethod = getSubscribeToOrderPricedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToOrderPriced"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToOrderPriced"))
              .build();
        }
      }
    }
    return getSubscribeToOrderPricedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToInventoryReserved",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod;
    if ((getSubscribeToInventoryReservedMethod = OrderGrpc.getSubscribeToInventoryReservedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToInventoryReservedMethod = OrderGrpc.getSubscribeToInventoryReservedMethod) == null) {
          OrderGrpc.getSubscribeToInventoryReservedMethod = getSubscribeToInventoryReservedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToInventoryReserved"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToInventoryReserved"))
              .build();
        }
      }
    }
    return getSubscribeToInventoryReservedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> getSubscribeToCreditCheckedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToCreditChecked",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> getSubscribeToCreditCheckedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> getSubscribeToCreditCheckedMethod;
    if ((getSubscribeToCreditCheckedMethod = OrderGrpc.getSubscribeToCreditCheckedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToCreditCheckedMethod = OrderGrpc.getSubscribeToCreditCheckedMethod) == null) {
          OrderGrpc.getSubscribeToCreditCheckedMethod = getSubscribeToCreditCheckedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToCreditChecked"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToCreditChecked"))
              .build();
        }
      }
    }
    return getSubscribeToCreditCheckedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> getSubscribeToAccountChargedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToAccountCharged",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> getSubscribeToAccountChargedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> getSubscribeToAccountChargedMethod;
    if ((getSubscribeToAccountChargedMethod = OrderGrpc.getSubscribeToAccountChargedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToAccountChargedMethod = OrderGrpc.getSubscribeToAccountChargedMethod) == null) {
          OrderGrpc.getSubscribeToAccountChargedMethod = getSubscribeToAccountChargedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToAccountCharged"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToAccountCharged"))
              .build();
        }
      }
    }
    return getSubscribeToAccountChargedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToInventoryPulled",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod;
    if ((getSubscribeToInventoryPulledMethod = OrderGrpc.getSubscribeToInventoryPulledMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToInventoryPulledMethod = OrderGrpc.getSubscribeToInventoryPulledMethod) == null) {
          OrderGrpc.getSubscribeToInventoryPulledMethod = getSubscribeToInventoryPulledMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToInventoryPulled"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToInventoryPulled"))
              .build();
        }
      }
    }
    return getSubscribeToInventoryPulledMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> getSubscribeToOrderShippedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToOrderShipped",
      requestType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> getSubscribeToOrderShippedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> getSubscribeToOrderShippedMethod;
    if ((getSubscribeToOrderShippedMethod = OrderGrpc.getSubscribeToOrderShippedMethod) == null) {
      synchronized (OrderGrpc.class) {
        if ((getSubscribeToOrderShippedMethod = OrderGrpc.getSubscribeToOrderShippedMethod) == null) {
          OrderGrpc.getSubscribeToOrderShippedMethod = getSubscribeToOrderShippedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest, org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToOrderShipped"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped.getDefaultInstance()))
              .setSchemaDescriptor(new OrderMethodDescriptorSupplier("SubscribeToOrderShipped"))
              .build();
        }
      }
    }
    return getSubscribeToOrderShippedMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static OrderStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderStub>() {
        @java.lang.Override
        public OrderStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderStub(channel, callOptions);
        }
      };
    return OrderStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static OrderBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderBlockingStub>() {
        @java.lang.Override
        public OrderBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderBlockingStub(channel, callOptions);
        }
      };
    return OrderBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static OrderFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<OrderFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<OrderFutureStub>() {
        @java.lang.Override
        public OrderFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new OrderFutureStub(channel, callOptions);
        }
      };
    return OrderFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class OrderImplBase implements io.grpc.BindableService {

    /**
     */
    public void createOrder(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateOrderMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToOrderCreated(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToOrderCreatedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToOrderPriced(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToOrderPricedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToInventoryReserved(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToInventoryReservedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToCreditChecked(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToCreditCheckedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToAccountCharged(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToAccountChargedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToInventoryPulled(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToInventoryPulledMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToOrderShipped(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToOrderShippedMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateOrderMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse>(
                  this, METHODID_CREATE_ORDER)))
          .addMethod(
            getSubscribeToOrderCreatedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated>(
                  this, METHODID_SUBSCRIBE_TO_ORDER_CREATED)))
          .addMethod(
            getSubscribeToOrderPricedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced>(
                  this, METHODID_SUBSCRIBE_TO_ORDER_PRICED)))
          .addMethod(
            getSubscribeToInventoryReservedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved>(
                  this, METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED)))
          .addMethod(
            getSubscribeToCreditCheckedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked>(
                  this, METHODID_SUBSCRIBE_TO_CREDIT_CHECKED)))
          .addMethod(
            getSubscribeToAccountChargedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged>(
                  this, METHODID_SUBSCRIBE_TO_ACCOUNT_CHARGED)))
          .addMethod(
            getSubscribeToInventoryPulledMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled>(
                  this, METHODID_SUBSCRIBE_TO_INVENTORY_PULLED)))
          .addMethod(
            getSubscribeToOrderShippedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped>(
                  this, METHODID_SUBSCRIBE_TO_ORDER_SHIPPED)))
          .build();
    }
  }

  /**
   */
  public static final class OrderStub extends io.grpc.stub.AbstractAsyncStub<OrderStub> {
    private OrderStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderStub(channel, callOptions);
    }

    /**
     */
    public void createOrder(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToOrderCreated(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToOrderCreatedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToOrderPriced(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToOrderPricedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToInventoryReserved(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToInventoryReservedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToCreditChecked(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToCreditCheckedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToAccountCharged(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToAccountChargedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToInventoryPulled(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToInventoryPulledMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToOrderShipped(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToOrderShippedMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class OrderBlockingStub extends io.grpc.stub.AbstractBlockingStub<OrderBlockingStub> {
    private OrderBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse createOrder(org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateOrderMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated> subscribeToOrderCreated(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToOrderCreatedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced> subscribeToOrderPriced(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToOrderPricedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved> subscribeToInventoryReserved(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToInventoryReservedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked> subscribeToCreditChecked(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToCreditCheckedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged> subscribeToAccountCharged(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToAccountChargedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled> subscribeToInventoryPulled(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToInventoryPulledMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped> subscribeToOrderShipped(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToOrderShippedMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class OrderFutureStub extends io.grpc.stub.AbstractFutureStub<OrderFutureStub> {
    private OrderFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected OrderFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new OrderFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse> createOrder(
        org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_ORDER = 0;
  private static final int METHODID_SUBSCRIBE_TO_ORDER_CREATED = 1;
  private static final int METHODID_SUBSCRIBE_TO_ORDER_PRICED = 2;
  private static final int METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED = 3;
  private static final int METHODID_SUBSCRIBE_TO_CREDIT_CHECKED = 4;
  private static final int METHODID_SUBSCRIBE_TO_ACCOUNT_CHARGED = 5;
  private static final int METHODID_SUBSCRIBE_TO_INVENTORY_PULLED = 6;
  private static final int METHODID_SUBSCRIBE_TO_ORDER_SHIPPED = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final OrderImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(OrderImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_ORDER:
          serviceImpl.createOrder((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreateOrderResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_ORDER_CREATED:
          serviceImpl.subscribeToOrderCreated((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderCreated>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_ORDER_PRICED:
          serviceImpl.subscribeToOrderPriced((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderPriced>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED:
          serviceImpl.subscribeToInventoryReserved((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryReserved>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_CREDIT_CHECKED:
          serviceImpl.subscribeToCreditChecked((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.CreditChecked>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_ACCOUNT_CHARGED:
          serviceImpl.subscribeToAccountCharged((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.AccountCharged>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_INVENTORY_PULLED:
          serviceImpl.subscribeToInventoryPulled((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.InventoryPulled>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_ORDER_SHIPPED:
          serviceImpl.subscribeToOrderShipped((org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.OrderShipped>) responseObserver);
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

  private static abstract class OrderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    OrderBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.hazelcast.msfdemo.ordersvc.events.OrderOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Order");
    }
  }

  private static final class OrderFileDescriptorSupplier
      extends OrderBaseDescriptorSupplier {
    OrderFileDescriptorSupplier() {}
  }

  private static final class OrderMethodDescriptorSupplier
      extends OrderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    OrderMethodDescriptorSupplier(String methodName) {
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
      synchronized (OrderGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new OrderFileDescriptorSupplier())
              .addMethod(getCreateOrderMethod())
              .addMethod(getSubscribeToOrderCreatedMethod())
              .addMethod(getSubscribeToOrderPricedMethod())
              .addMethod(getSubscribeToInventoryReservedMethod())
              .addMethod(getSubscribeToCreditCheckedMethod())
              .addMethod(getSubscribeToAccountChargedMethod())
              .addMethod(getSubscribeToInventoryPulledMethod())
              .addMethod(getSubscribeToOrderShippedMethod())
              .build();
        }
      }
    }
    return result;
  }
}
