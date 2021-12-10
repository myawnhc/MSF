package org.hazelcast.msfdemo.invsvc.events;

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
  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailability",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityMethod;
    if ((getCheckAvailabilityMethod = InventoryGrpc.getCheckAvailabilityMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getCheckAvailabilityMethod = InventoryGrpc.getCheckAvailabilityMethod) == null) {
          InventoryGrpc.getCheckAvailabilityMethod = getCheckAvailabilityMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailability"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("CheckAvailability"))
              .build();
        }
      }
    }
    return getCheckAvailabilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckAvailabilityAllLocations",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> getCheckAvailabilityAllLocationsMethod;
    if ((getCheckAvailabilityAllLocationsMethod = InventoryGrpc.getCheckAvailabilityAllLocationsMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getCheckAvailabilityAllLocationsMethod = InventoryGrpc.getCheckAvailabilityAllLocationsMethod) == null) {
          InventoryGrpc.getCheckAvailabilityAllLocationsMethod = getCheckAvailabilityAllLocationsMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckAvailabilityAllLocations"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("CheckAvailabilityAllLocations"))
              .build();
        }
      }
    }
    return getCheckAvailabilityAllLocationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ship",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> getShipMethod;
    if ((getShipMethod = InventoryGrpc.getShipMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getShipMethod = InventoryGrpc.getShipMethod) == null) {
          InventoryGrpc.getShipMethod = getShipMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ship"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Ship"))
              .build();
        }
      }
    }
    return getShipMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Reserve",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getReserveMethod;
    if ((getReserveMethod = InventoryGrpc.getReserveMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getReserveMethod = InventoryGrpc.getReserveMethod) == null) {
          InventoryGrpc.getReserveMethod = getReserveMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Reserve"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Reserve"))
              .build();
        }
      }
    }
    return getReserveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getPullMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Pull",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getPullMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getPullMethod;
    if ((getPullMethod = InventoryGrpc.getPullMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getPullMethod = InventoryGrpc.getPullMethod) == null) {
          InventoryGrpc.getPullMethod = getPullMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Pull"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Pull"))
              .build();
        }
      }
    }
    return getPullMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getUnreserveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Unreserve",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getUnreserveMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> getUnreserveMethod;
    if ((getUnreserveMethod = InventoryGrpc.getUnreserveMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getUnreserveMethod = InventoryGrpc.getUnreserveMethod) == null) {
          InventoryGrpc.getUnreserveMethod = getUnreserveMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Unreserve"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Unreserve"))
              .build();
        }
      }
    }
    return getUnreserveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getRestockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Restock",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getRestockMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> getRestockMethod;
    if ((getRestockMethod = InventoryGrpc.getRestockMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getRestockMethod = InventoryGrpc.getRestockMethod) == null) {
          InventoryGrpc.getRestockMethod = getRestockMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Restock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("Restock"))
              .build();
        }
      }
    }
    return getRestockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> getGetItemCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetItemCount",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> getGetItemCountMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> getGetItemCountMethod;
    if ((getGetItemCountMethod = InventoryGrpc.getGetItemCountMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getGetItemCountMethod = InventoryGrpc.getGetItemCountMethod) == null) {
          InventoryGrpc.getGetItemCountMethod = getGetItemCountMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetItemCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("GetItemCount"))
              .build();
        }
      }
    }
    return getGetItemCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> getGetInventoryRecordCountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetInventoryRecordCount",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> getGetInventoryRecordCountMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> getGetInventoryRecordCountMethod;
    if ((getGetInventoryRecordCountMethod = InventoryGrpc.getGetInventoryRecordCountMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getGetInventoryRecordCountMethod = InventoryGrpc.getGetInventoryRecordCountMethod) == null) {
          InventoryGrpc.getGetInventoryRecordCountMethod = getGetInventoryRecordCountMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetInventoryRecordCount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("GetInventoryRecordCount"))
              .build();
        }
      }
    }
    return getGetInventoryRecordCountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToInventoryReserved",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> getSubscribeToInventoryReservedMethod;
    if ((getSubscribeToInventoryReservedMethod = InventoryGrpc.getSubscribeToInventoryReservedMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getSubscribeToInventoryReservedMethod = InventoryGrpc.getSubscribeToInventoryReservedMethod) == null) {
          InventoryGrpc.getSubscribeToInventoryReservedMethod = getSubscribeToInventoryReservedMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToInventoryReserved"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("SubscribeToInventoryReserved"))
              .build();
        }
      }
    }
    return getSubscribeToInventoryReservedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeToInventoryPulled",
      requestType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest.class,
      responseType = org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
      org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod() {
    io.grpc.MethodDescriptor<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> getSubscribeToInventoryPulledMethod;
    if ((getSubscribeToInventoryPulledMethod = InventoryGrpc.getSubscribeToInventoryPulledMethod) == null) {
      synchronized (InventoryGrpc.class) {
        if ((getSubscribeToInventoryPulledMethod = InventoryGrpc.getSubscribeToInventoryPulledMethod) == null) {
          InventoryGrpc.getSubscribeToInventoryPulledMethod = getSubscribeToInventoryPulledMethod =
              io.grpc.MethodDescriptor.<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest, org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeToInventoryPulled"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled.getDefaultInstance()))
              .setSchemaDescriptor(new InventoryMethodDescriptorSupplier("SubscribeToInventoryPulled"))
              .build();
        }
      }
    }
    return getSubscribeToInventoryPulledMethod;
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
    public void checkAvailability(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityMethod(), responseObserver);
    }

    /**
     */
    public void checkAvailabilityAllLocations(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckAvailabilityAllLocationsMethod(), responseObserver);
    }

    /**
     */
    public void ship(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShipMethod(), responseObserver);
    }

    /**
     */
    public void reserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReserveMethod(), responseObserver);
    }

    /**
     */
    public void pull(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPullMethod(), responseObserver);
    }

    /**
     */
    public void unreserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUnreserveMethod(), responseObserver);
    }

    /**
     */
    public void restock(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRestockMethod(), responseObserver);
    }

    /**
     * <pre>
     * These are more demo oriented than production, used to make sure we don't run
     * ahead of data generation and order items not yet added
     * </pre>
     */
    public void getItemCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetItemCountMethod(), responseObserver);
    }

    /**
     */
    public void getInventoryRecordCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetInventoryRecordCountMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToInventoryReserved(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToInventoryReservedMethod(), responseObserver);
    }

    /**
     */
    public void subscribeToInventoryPulled(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeToInventoryPulledMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCheckAvailabilityMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>(
                  this, METHODID_CHECK_AVAILABILITY)))
          .addMethod(
            getCheckAvailabilityAllLocationsMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>(
                  this, METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS)))
          .addMethod(
            getShipMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>(
                  this, METHODID_SHIP)))
          .addMethod(
            getReserveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>(
                  this, METHODID_RESERVE)))
          .addMethod(
            getPullMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>(
                  this, METHODID_PULL)))
          .addMethod(
            getUnreserveMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>(
                  this, METHODID_UNRESERVE)))
          .addMethod(
            getRestockMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>(
                  this, METHODID_RESTOCK)))
          .addMethod(
            getGetItemCountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse>(
                  this, METHODID_GET_ITEM_COUNT)))
          .addMethod(
            getGetInventoryRecordCountMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse>(
                  this, METHODID_GET_INVENTORY_RECORD_COUNT)))
          .addMethod(
            getSubscribeToInventoryReservedMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved>(
                  this, METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED)))
          .addMethod(
            getSubscribeToInventoryPulledMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest,
                org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled>(
                  this, METHODID_SUBSCRIBE_TO_INVENTORY_PULLED)))
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
    public void checkAvailability(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkAvailabilityAllLocations(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getCheckAvailabilityAllLocationsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ship(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShipMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pull(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPullMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void unreserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUnreserveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restock(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRestockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * These are more demo oriented than production, used to make sure we don't run
     * ahead of data generation and order items not yet added
     * </pre>
     */
    public void getItemCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetItemCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getInventoryRecordCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetInventoryRecordCountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToInventoryReserved(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToInventoryReservedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscribeToInventoryPulled(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request,
        io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeToInventoryPulledMethod(), getCallOptions()), request, responseObserver);
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
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse checkAvailability(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckAvailabilityMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> checkAvailabilityAllLocations(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getCheckAvailabilityAllLocationsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse ship(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getShipMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse reserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReserveMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse pull(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPullMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse unreserve(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUnreserveMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse restock(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRestockMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * These are more demo oriented than production, used to make sure we don't run
     * ahead of data generation and order items not yet added
     * </pre>
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse getItemCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetItemCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse getInventoryRecordCount(org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetInventoryRecordCountMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved> subscribeToInventoryReserved(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToInventoryReservedMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled> subscribeToInventoryPulled(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeToInventoryPulledMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse> checkAvailability(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckAvailabilityMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse> ship(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShipMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> reserve(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReserveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> pull(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPullMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse> unreserve(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUnreserveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse> restock(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRestockMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * These are more demo oriented than production, used to make sure we don't run
     * ahead of data generation and order items not yet added
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse> getItemCount(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetItemCountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse> getInventoryRecordCount(
        org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetInventoryRecordCountMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_AVAILABILITY = 0;
  private static final int METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS = 1;
  private static final int METHODID_SHIP = 2;
  private static final int METHODID_RESERVE = 3;
  private static final int METHODID_PULL = 4;
  private static final int METHODID_UNRESERVE = 5;
  private static final int METHODID_RESTOCK = 6;
  private static final int METHODID_GET_ITEM_COUNT = 7;
  private static final int METHODID_GET_INVENTORY_RECORD_COUNT = 8;
  private static final int METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED = 9;
  private static final int METHODID_SUBSCRIBE_TO_INVENTORY_PULLED = 10;

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
          serviceImpl.checkAvailability((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>) responseObserver);
          break;
        case METHODID_CHECK_AVAILABILITY_ALL_LOCATIONS:
          serviceImpl.checkAvailabilityAllLocations((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ATPResponse>) responseObserver);
          break;
        case METHODID_SHIP:
          serviceImpl.ship((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ShipResponse>) responseObserver);
          break;
        case METHODID_RESERVE:
          serviceImpl.reserve((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>) responseObserver);
          break;
        case METHODID_PULL:
          serviceImpl.pull((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>) responseObserver);
          break;
        case METHODID_UNRESERVE:
          serviceImpl.unreserve((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ReserveResponse>) responseObserver);
          break;
        case METHODID_RESTOCK:
          serviceImpl.restock((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.PullResponse>) responseObserver);
          break;
        case METHODID_GET_ITEM_COUNT:
          serviceImpl.getItemCount((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.ItemCountResponse>) responseObserver);
          break;
        case METHODID_GET_INVENTORY_RECORD_COUNT:
          serviceImpl.getInventoryRecordCount((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryCountResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_INVENTORY_RESERVED:
          serviceImpl.subscribeToInventoryReserved((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryReserved>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_TO_INVENTORY_PULLED:
          serviceImpl.subscribeToInventoryPulled((org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.InventoryPulled>) responseObserver);
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
      return org.hazelcast.msfdemo.invsvc.events.InventoryOuterClass.getDescriptor();
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
              .addMethod(getPullMethod())
              .addMethod(getUnreserveMethod())
              .addMethod(getRestockMethod())
              .addMethod(getGetItemCountMethod())
              .addMethod(getGetInventoryRecordCountMethod())
              .addMethod(getSubscribeToInventoryReservedMethod())
              .addMethod(getSubscribeToInventoryPulledMethod())
              .build();
        }
      }
    }
    return result;
  }
}
