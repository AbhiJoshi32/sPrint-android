package com.binktec.sprint.interactor.modal;

import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public interface SyncListener {
    void setProgressSession(List<PrintJobDetail> printJobDetailList, List<String> transactionIds);

    void setHistorySession(List<PrintJobDetail> printJobDetailList, List<String> historyIds);
}
