package com.hazelcast.msfdemo.acctsvc.transactions;

import com.hazelcast.msf.controller.MSFController;
import com.hazelcast.msf.transactions.TxnMessage;
import com.hazelcast.msfdemo.acctsvc.events.AccountEventTypes;

import java.io.Serializable;

@Deprecated  // replaced by gRPC OpenAccountRequest in Account.proto
public class OpenAccountTxnMsg extends TxnMessage implements Serializable {

    private String accountName;
    private String accountNumber;
    private int beginningBalance;

    // For serializer only
    private OpenAccountTxnMsg() {}

    public OpenAccountTxnMsg(String name, int balance) {
        super(AccountEventTypes.OPEN.getQualifiedName(),
              MSFController.getInstance().getUniqueMessageID());
        this.accountName = name;
        this.accountNumber = ""+MSFController.getInstance().getUniqueId("account");
        this.beginningBalance = balance;
    }

    public String getAccountName() { return accountName; }
    public String getAccountNumber() { return accountNumber; }
    public int getBalance() { return beginningBalance; }


}
