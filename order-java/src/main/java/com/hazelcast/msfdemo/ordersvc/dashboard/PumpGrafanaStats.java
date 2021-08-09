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

package com.hazelcast.msfdemo.ordersvc.dashboard;/*
 *  Copyright 2018-2021 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package com.theyawns.controller.launcher;
 */


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.msf.dashboard.Graphite;
import com.hazelcast.msfdemo.ordersvc.domain.WaitingOn;
import com.hazelcast.msfdemo.ordersvc.views.OrderDAO;
import com.hazelcast.scheduledexecutor.NamedTask;

import java.io.IOException;
import java.io.Serializable;


/** Runnable task that pumps stats from several IMaps to com.hazelcast.msf.dashboard.Graphite / Grafana */
public class PumpGrafanaStats implements Serializable, Runnable, HazelcastInstanceAware, NamedTask {

    private transient HazelcastInstance hazelcast;
    private transient Graphite graphite;
    private boolean initialized = false;
    private String host;
    private int measurementInterval = 5;   // seconds.   Should be in sync with schedule interval set by Launcher.
    private static int previouslyReportedCompletions = 0;
    private long lastTimeRun;
    private OrderDAO dao;

    public PumpGrafanaStats(String host, OrderDAO dao) {
        this.host = host;
        this.dao = dao;
    }

    private void init() {
        graphite = new Graphite(host);
        initialized = true;
        lastTimeRun = System.currentTimeMillis();
    }

    // Runs at intervals
    @Override
    public void run() {
    	//System.out.println("com.hazelcast.msfdemo.ordersvc.dashboard.PumpGrafanaStats active");
        if (!initialized)
            init();

        try {
            int wait0 = dao.countByWaitingOn(WaitingOn.PRICE_LOOKUP);
            int wait1 = dao.countByWaitingOn(WaitingOn.CREDIT_CHECK);
            int wait2 = dao.countByWaitingOn(WaitingOn.RESERVE_INVENTORY);
            System.out.println("*** WRITE STATS FIRST VALUES " + wait0 + ", " + wait1 + ", " + wait2);
            graphite.writeStats("wait4.priceLookup", dao.countByWaitingOn(WaitingOn.PRICE_LOOKUP));
            graphite.writeStats("wait4.creditCheck", dao.countByWaitingOn(WaitingOn.CREDIT_CHECK));
            graphite.writeStats("wait4.reserveInv", dao.countByWaitingOn(WaitingOn.RESERVE_INVENTORY));
            graphite.writeStats("wait4.chargeAcct", dao.countByWaitingOn(WaitingOn.CHARGE_ACCOUNT));
            graphite.writeStats("wait4.pullInv", dao.countByWaitingOn(WaitingOn.PULL_INVENTORY));
            graphite.writeStats("wait4.ship", dao.countByWaitingOn(WaitingOn.SHIP));
            graphite.writeStats("wait4.nothing", dao.countByWaitingOn(WaitingOn.NOTHING));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("** Reinitializing com.hazelcast.msf.dashboard.Graphite");
            graphite = new Graphite(host);
        }
        lastTimeRun = System.currentTimeMillis();
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcast = hazelcastInstance;
        init();
    }

    @Override
    public String getName() {
        return "com.hazelcast.msfdemo.ordersvc.dashboard.PumpGrafanaStats";
    }
}
