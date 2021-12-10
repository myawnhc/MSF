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

package org.hazelcast.msfdemo.acctsvc.domain;

import org.hazelcast.msf.persistence.DTO;

import java.io.Serializable;

public class Account extends DTO<String> implements Serializable {

    private int balance;    // in cents
    private String acctNumber;
    private String name;

    public Account() {}

    public int getBalance() { return balance; }
    public void setBalance(int value) { balance = value; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setAcctNumber(String acctNum) { this.acctNumber = acctNum; }
    public String getAcctNumber() { return this.acctNumber; }

//    public void credit(int byAmount) { balance += byAmount; }
//    public void debit(int byAmount) { balance -= byAmount; }

}
