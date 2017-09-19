package com.binktec.sprint.interactor.presenter;

import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public interface PrintJobPresenterListener {
    void openAuthActivity();

    void initializePrintJob(String displayName, String s);

    void openInstructionActivity();

    void showToastError(String s);

    void progressItemInserted(PrintJobDetail transactionDetail, int i);

    void progressItemChanged(PrintJobDetail changedTransaction, int changedIndex);

    void progressItemRemoved(int removeIndex);
    
    void initProgressList(List<PrintJobDetail> progressPrintJobDetails);

    void initHistoryList(List<PrintJobDetail> historyPrintJobDetails);

    void historyItemInserted(PrintJobDetail historyDetail, int i);
}
