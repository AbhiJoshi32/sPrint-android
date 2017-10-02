package com.binktec.sprint.interactor.modal;


import com.binktec.sprint.modal.pojo.PrintJobDetail;

public interface PrintJobModalListener {

    void uploadFailed(String s);

    void filesUploaded(PrintJobDetail jobDetail);

    void uploadFailed(PrintJobDetail file);

    void apiHistoryAdded(PrintJobDetail historyDetail);

    void apiPrintTransactionAdded(PrintJobDetail transactionDetail, String key, String prevKey);

    void apiPrintTransactionChanged(PrintJobDetail changedTransaction, String key);

    void apiPrintTransactionRemoved(PrintJobDetail deletedTransaction, String key);

    void connectionFaliure(String s);
}
