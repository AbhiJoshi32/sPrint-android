package com.binktec.sprint.presenter;


import android.net.Uri;
import android.util.Log;

import com.binktec.sprint.interactor.modal.PrintJobModalListener;
import com.binktec.sprint.interactor.presenter.PrintJobPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class PrintJobPresenter implements PrintJobModalListener {

    private static final String TAG = "Print Job Presenter";
    private PrintJobPresenterListener printJobPresenterListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private PrintApi printApi;
    private List<String> transactionIds = new ArrayList<>();
    private List<String> historyIds = new ArrayList<>();
    private List<PrintJobDetail> progressPrintJobDetails;
    private List<PrintJobDetail> historyPrintJobDetails;

    public PrintJobPresenter(final PrintJobPresenterListener printJobPresenterListener) {
        this.printJobPresenterListener = printJobPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressPrintJobDetails = new ArrayList<>();
        historyPrintJobDetails = new ArrayList<>();
        transactionIds = new ArrayList<>();
        historyIds = new ArrayList<>();
    }



    @Override
    public void uploadFailed(String s) {
        printJobPresenterListener.showToastError("Upload Failed");
        SessionManager.clearCurrentPrintJob();
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        int uploadingIndex = transactionIds.indexOf("Uploading");
        if (uploadingIndex != -1) {
            transactionIds.remove(uploadingIndex);
            progressPrintJobDetails.remove(uploadingIndex);
            printJobPresenterListener.progressItemRemoved(uploadingIndex);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        }
    }

    @Override
    public void filesUploaded(PrintJobDetail jobDetail) {
        SessionManager.clearCurrentPrintJob();
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        int uploadingIndex = transactionIds.indexOf("Uploading");
        if (uploadingIndex != -1) {
            transactionIds.remove(uploadingIndex);
            progressPrintJobDetails.remove(uploadingIndex);
            printJobPresenterListener.progressItemRemoved(uploadingIndex);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        }
        jobDetail.setStatus("Waiting");
        printApi.enterTransaction(jobDetail);
    }

    @Override
    public void uploadFailed(PrintJobDetail file) {
        printJobPresenterListener.showToastError("Upload Failed");
        SessionManager.clearCurrentPrintJob();
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        int uploadingIndex = transactionIds.indexOf("Uploading");
        if (uploadingIndex != -1) {
            transactionIds.remove(uploadingIndex);
            progressPrintJobDetails.remove(uploadingIndex);
            printJobPresenterListener.progressItemRemoved(uploadingIndex);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        }
    }

    @Override
    public void apiHistoryAdded(PrintJobDetail historyDetail) {
        historyIds = SessionManager.getHistoryIds();
        historyPrintJobDetails = SessionManager.getHistoryPrintJobDetail();
        String tid = historyDetail.gettId();
        if (!historyIds.contains(historyDetail.gettId())) {
            int transactionIndex = transactionIds.indexOf(historyDetail.gettId());
            if (transactionIndex != -1) {
                transactionIds.remove(transactionIndex);
                progressPrintJobDetails.remove(transactionIndex);
            }
            historyIds.add(0,tid);
            historyPrintJobDetails.add(0,historyDetail);
            SessionManager.saveHistoryPrintJob(historyPrintJobDetails);
            SessionManager.saveHistoryIds(historyIds);
            if (historyDetail.getStatus().equals("Printed"))
                SessionManager.setIsPrinted(true);
            if (historyDetail.getStatus().equals("Rejected"))
                SessionManager.setIsRejected(true);
            printJobPresenterListener.historyItemInserted(historyDetail,0);
            SessionManager.setIsPrinted(false);
            SessionManager.setIsRejected(false);
        }
    }

    @Override
    public void apiPrintTransactionAdded(final PrintJobDetail transactionDetail, final String key, String prevKey) {
        int topIndex = 0;
        if (SessionManager.getCurrentPrintJobDetail() != null) {
            topIndex = 1;
        }
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        if (!transactionIds.contains(key)) {
            Log.d(TAG,"Item Added");
            transactionIds.add(topIndex,key);
            progressPrintJobDetails.add(topIndex,transactionDetail);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemInserted(transactionDetail,topIndex);
        } else {
            int transactionIndex = transactionIds.indexOf(key);
            if (!transactionDetail.getStatus().equals(progressPrintJobDetails.get(transactionIndex).getStatus())) {
                printJobPresenterListener.progressItemChanged(transactionDetail,transactionIndex);
            }
            Log.d(TAG,"item present" + key + transactionDetail.getStatus());
            progressPrintJobDetails.set(transactionIndex,transactionDetail);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
        }
    }

    @Override
    public void apiPrintTransactionChanged(PrintJobDetail changedTransaction, String key) {
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        int changedIndex = transactionIds.indexOf(key);
        if (changedIndex > -1) {
            progressPrintJobDetails.set(changedIndex,changedTransaction);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            printJobPresenterListener.progressItemChanged(changedTransaction,changedIndex);
        }
    }

    @Override
    public void apiPrintTransactionRemoved(PrintJobDetail deletedTransaction, String key) {
        transactionIds = SessionManager.getTransactionIds();
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        Log.d(TAG,"transaction id on item removed" + transactionIds);
        int removedIndex = transactionIds.indexOf(key);
        if (removedIndex > -1) {
            progressPrintJobDetails.remove(removedIndex);
            transactionIds.remove(removedIndex);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemRemoved(removedIndex);
        }
    }

    @Override
    public void connectionFaliure(String s) {
        printJobPresenterListener.showToastError("Connection Error");
    }

    public void appStart() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            printJobPresenterListener.openAuthActivity();
        } else {
            if (!firebaseUser.isEmailVerified()) {
                printJobPresenterListener.openAuthActivity();
            } else {
                printApi = new PrintApi(firebaseUser.getUid());
                printApi.prepareHistoryListeners(this);
                printApi.prepareTransactionListeners(this);
                PrintJobDetail currPrintDetail = SessionManager.getCurrentPrintJobDetail();
                progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
                transactionIds = SessionManager.getTransactionIds();
                historyPrintJobDetails = SessionManager.getHistoryPrintJobDetail();
                historyIds = SessionManager.getHistoryIds();
                if (currPrintDetail != null) {
                    printApi.startFileUpload(currPrintDetail, this);
                    if (transactionIds.indexOf("Uploading") == -1) {
                        progressPrintJobDetails.add(0, currPrintDetail);
                        transactionIds.add(0, "Uploading");
                        SessionManager.saveApiPrintJob(progressPrintJobDetails);
                        SessionManager.saveTrasactionIds(transactionIds);
                    }
                }
                Log.d(TAG,"app start transaction ids" + transactionIds);
                Log.d(TAG,"app start progressjobdetail" + progressPrintJobDetails);
                String displayName = firebaseUser.getDisplayName();
                Uri photoUrl = firebaseUser.getPhotoUrl();
                printJobPresenterListener.initializePrintJob(displayName, photoUrl);
            }
        }
    }


    public void onStopCalled() {
        printApi.removeListeners();
    }

    public void getPrintJobList() {
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        transactionIds = SessionManager.getTransactionIds();
        printJobPresenterListener.initProgressList(SessionManager.getApiPrintJobDetail());
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void getHistoryList() {
        historyPrintJobDetails = SessionManager.getHistoryPrintJobDetail();
        historyIds = SessionManager.getHistoryIds();
        printJobPresenterListener.initHistoryList(historyPrintJobDetails);
        if(SessionManager.getIsPrinted()) {
            printJobPresenterListener.showPrintConfirmDialog();
            SessionManager.setIsPrinted(false);
        }
        if (SessionManager.getIsRejected()) {
            printJobPresenterListener.showRejectDialog();
            SessionManager.setIsRejected(false);
        }
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void cancelUpload(PrintJobDetail printJobDetail) {
        progressPrintJobDetails = SessionManager.getApiPrintJobDetail();
        transactionIds = SessionManager.getTransactionIds();
        if (printJobDetail.getStatus().equals("Uploading")) {
            Log.d(TAG,"transaction ids are"+transactionIds);
            int uploadingIndex = transactionIds.indexOf("Uploading");
            Log.d(TAG,"Uploading index is" + uploadingIndex);
            if (uploadingIndex != -1) {
                progressPrintJobDetails.remove(uploadingIndex);
                transactionIds.remove(uploadingIndex);
                SessionManager.saveApiPrintJob(progressPrintJobDetails);
                SessionManager.saveTrasactionIds(transactionIds);
                printJobPresenterListener.progressItemRemoved(uploadingIndex);
            }
        } else {
            printJobDetail.setStatus("Cancelled");
            printApi.cancelTransaction(printJobDetail);
        }
    }
}