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

package org.hazelcast.msfdemo.ordersvc.events;

public enum OrderEventTypes {
    CREATE("OrderEvent_CREATE"),
    COMPACTION("OrderEvent_COMPACTION"),
    PRICE_CALCULATED("OrderEvent_PRICED"),
    CREDIT_CHECKED("OrderEvent_CREDIT_OK"),
    INV_RESERVED("OrderEvent_INV_RESERVED"),
    COMPLETE("OrderEvent_SHIP_COMPLETE"),
    CANCELED("OrderEvent_CANCELED"), // unused
    INV_NOT_AVAIL("OrderEvent_FAIL_NO_INV"),
    CREDIT_DECLINED("OrderEvent_FAIL_CREDIT");

    private String qualifiedName;
    OrderEventTypes(String fqname) {
        qualifiedName = fqname;
    }
    public String getQualifiedName() { return qualifiedName; }
}
