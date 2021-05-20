package com.hazelcast.msfdemo.acctsvc.transactions;

import com.hazelcast.msf.transactions.TxnMessage;

import java.io.Serializable;

@Deprecated  // replaced by gRPC
public class TransferMoneyTxnMsg extends TxnMessage implements Serializable {
    private String fromAccountNumber;
    private String toAccountNumber;
    private int amount;

    public TransferMoneyTxnMsg(String fromAccountNumber, String toAccountNumber, int amount) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String id) { fromAccountNumber = id; }

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String id) { toAccountNumber = id; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
