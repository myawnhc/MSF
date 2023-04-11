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

import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.stub.StreamObserver;
import org.hazelcast.msfdemo.acctsvc.events.AccountGrpc;
import org.hazelcast.msfdemo.acctsvc.events.AccountOuterClass;

public class IgnoreExperimental {

    // Based on below ...
    // If we're going to do multiple services, they probably can't share a port
    // Our 'openHandler' below is a valid bindable service so could be passed to the
    // ServerBuilder.

//    int port = props.getGrpcPort();
//    server = ServerBuilder.forPort(port)
//            .addService(new AccountAPIImpl())
//            .build()
//                .start();

    /** Experiments to see how we might approach a gRPC connector for Jet pipelines */
    public static void main(String[] args) {
        ServiceDescriptor serviceDescriptor = AccountGrpc.getServiceDescriptor();
        MethodDescriptor<AccountOuterClass.OpenAccountRequest, AccountOuterClass.OpenAccountResponse> method = AccountGrpc.getOpenMethod();
        // method.getType allows to distinguish streaming vs. single-valued req, response
        // method.parseRequest, parseResponse give us req, resp types from inputstream
        //    but not clear how we find the inputstream ...
        AccountGrpc.AccountImplBase openHandler = new AccountGrpc.AccountImplBase() {
            @Override
            public void open(AccountOuterClass.OpenAccountRequest request, StreamObserver<AccountOuterClass.OpenAccountResponse> response) {

            }
        };
        // Seems problematic because this will bind our handler for all methods defined by the service,
        // I suspect, rather than just the one we are prepared to handle ...
        ServerServiceDefinition ssd = openHandler.bindService();

    }
}
