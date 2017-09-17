package com.binktec.sprint.interactor.modal;


import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public interface PrintJobModalListener {

    void uploadFailed(String s);

    void filesUploaded(PrintJobDetail jobDetail);

    void apiPrintTransactionRetrievalSuccessful(List<PrintJobDetail> apiPrintJobDetail);

    void apiPrintTransactionRetrievalUnsuccessful(String s);

    void uploadFailed(PrintJobDetail file);
}
