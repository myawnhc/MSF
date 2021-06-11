import grpc
import logging
import order_pb2
import order_pb2_grpc
from concurrent import futures


class OrderService(order_pb2_grpc.OrderServicer):


    def createOrder(self, request, context):
        return order_pb2.Order.createOrderResponse()


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    order_pb2_grpc.add_OrderServicer_to_server(OrderService(), server)
    server.add_insecure_port('[::]:50052')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()