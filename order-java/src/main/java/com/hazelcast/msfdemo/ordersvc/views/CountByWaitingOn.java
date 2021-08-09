/*
 * Copyright 2018-2021 Hazelcast, Inc
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
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msfdemo.ordersvc.views;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.msfdemo.ordersvc.domain.Order;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;

import java.util.Map;

public class CountByWaitingOn implements Aggregator<Map.Entry<String,Order>, Integer> {
    private WaitingOn wait;
    int unmatchedCount; // Only putting this in for debugging
    int matchedCount;

    public CountByWaitingOn(WaitingOn value) {
        this.wait = value;
    }
    @Override
    public void accumulate(Map.Entry<String,Order> order) {
        if (wait == null || wait.equals(WaitingOn.NOTHING)) {
            if (order.getValue().getWaitingOn().isEmpty()) {
                matchedCount++;
            } else {
                unmatchedCount++;
            }
        } else if (order.getValue().getWaitingOn().contains(wait)) {
            matchedCount++;
        } else {
            unmatchedCount++;
        }
    }

    @Override
    public void combine(Aggregator aggregator) {
        matchedCount += this.getClass().cast(aggregator).matchedCount;
        unmatchedCount += this.getClass().cast(aggregator).unmatchedCount;
    }

    @Override
    public Integer aggregate() {
        System.out.println("Aggregator for " + wait.name() + " matches " + matchedCount + " of " + (matchedCount + unmatchedCount));
        return matchedCount;
    }
}
