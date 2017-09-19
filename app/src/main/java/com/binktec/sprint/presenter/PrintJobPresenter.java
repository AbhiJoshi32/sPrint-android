package com.binktec.sprint.presenter;


import android.util.Log;

import com.binktec.sprint.interactor.modal.PrintJobModalListener;
import com.binktec.sprint.interactor.presenter.PrintJobPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.utility.Misc;
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

    public PrintJobPresenter(PrintJobPresenterListener printJobPresenterListener) {
        this.printJobPresenterListener = printJobPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressPrintJobDetails = new ArrayList<>();
        historyPrintJobDetails = new ArrayList<>();
        transactionIds = new ArrayList<>();
        historyIds = new ArrayList<>();
    }

    public void appStart() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (!firebaseUser.isEmailVerified()) {
                printJobPresenterListener.openAuthActivity();
            } else {
                if (SessionManager.getIsFirstSetup()){
                    SessionManager.saveFirstSetup(false);
                    printJobPresenterListener.openInstructionActivity();
                } else {
                    printApi = new PrintApi(firebaseUser.getUid());
                    clearTransactionSessions();
                    PrintJobDetail currJobDetail = SessionManager.getCurrentPrintJobDetail();
                    if (currJobDetail != null) {
                        progressPrintJobDetails.add(currJobDetail);
                        transactionIds.add("Uploading File");
                        Log.d(TAG,"current file" + currJobDetail + "\n progressPrint " + progressPrintJobDetails);
                        printApi.startFileUpload(currJobDetail,this);
                    }
                    if (SessionManager.getApiPrintJobDetail() != null
                            && SessionManager.getTransactionIds() !=null) {
                        List<PrintJobDetail> apiProgressPrintDetail = new ArrayList<>(SessionManager.getApiPrintJobDetail());
                        List<String> savedTransactionIds = new ArrayList<>(SessionManager.getTransactionIds());
                        progressPrintJobDetails.addAll(apiProgressPrintDetail);
                        transactionIds.addAll(savedTransactionIds);
                    }
                    SessionManager.saveApiPrintJob(progressPrintJobDetails);
                    SessionManager.saveTrasactionIds(transactionIds);
                    Log.d(TAG,"transaction id at app start" + SessionManager.getTransactionIds());
//                    printApi.getHistoryInfo(this);

                    if (firebaseUser.getPhotoUrl() != null) {
                        printJobPresenterListener.initializePrintJob(firebaseUser.getDisplayName(),
                                firebaseUser.getPhotoUrl().toString());
                    } else {
                        printJobPresenterListener.initializePrintJob(firebaseUser.getDisplayName(),
                                "");
                    }
                }
            }
        } else {
            printJobPresenterListener.openAuthActivity();
        }
    }

    private void clearTransactionSessions() {
        SessionManager.clearFileDetail();
        SessionManager.clearPrintDetail();
    }

    @Override
    public void uploadFailed(String s) {
        SessionManager.clearCurrentPrintJob();
        progressPrintJobDetails.remove(0);
        transactionIds.remove(0);
        printJobPresenterListener.progressItemRemoved(0);
        printJobPresenterListener.showToastError("upload Failed");
    }

    @Override
    public void filesUploaded(PrintJobDetail jobDetail) {
        if (SessionManager.getCurrentPrintJobDetail() !=  null) {
            Log.d(TAG,"File Uploaded");
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
            transactionIds.remove(0);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemRemoved(0);
            jobDetail.setStatus("Waiting");
            jobDetail.setIssuedTime(Misc.getTime());
            jobDetail.setIssuedDate(Misc.getDate());
            printApi.enterTransaction(jobDetail);
        }
    }

    @Override
    public void uploadFailed(PrintJobDetail file) {
        printJobPresenterListener.showToastError("Upload Failed");
        progressPrintJobDetails.remove(0);
        transactionIds.remove(0);
        printJobPresenterListener.progressItemRemoved(0);
        SessionManager.clearCurrentPrintJob();
    }

    @Override
    public void apiHistoryAdded(PrintJobDetail historyDetail) {
        if (SessionManager.getTransactionIds() != null && SessionManager.getHistoryPrintJobDetail() != null) {
            historyIds = SessionManager.getHistoryIds();
            historyPrintJobDetails = SessionManager.getHistoryPrintJobDetail();
        }
        if (historyIds.indexOf(historyDetail.gettId()) == -1) {
            historyPrintJobDetails.add(historyDetail);
            historyIds.add(historyDetail.gettId());
            SessionManager.saveHistoryPrintJob(historyPrintJobDetails);
            SessionManager.saveHistoryIds(historyIds);
            printJobPresenterListener.historyItemInserted(historyDetail,historyPrintJobDetails.size()-1);
        }
    }

    @Override
    public void apiPrintTransactionAdded(PrintJobDetail transactionDetail, String key, String prevKey) {
        if (prevKey == null) {
            progressPrintJobDetails.clear();
            transactionIds.clear();
            PrintJobDetail uploadingDetail = SessionManager.getCurrentPrintJobDetail();
            if (uploadingDetail != null) {
                progressPrintJobDetails.add(uploadingDetail);
                transactionIds.add("Uploading File");
                printJobPresenterListener.progressItemInserted(uploadingDetail,progressPrintJobDetails.size()-1);
            }
        }
        transactionDetail.settId(key);
        progressPrintJobDetails.add(transactionDetail);
        transactionIds.add(key);
        SessionManager.saveApiPrintJob(progressPrintJobDetails);
        SessionManager.saveTrasactionIds(transactionIds);
        printJobPresenterListener.progressItemInserted(transactionDetail,progressPrintJobDetails.size()-1);
    }

    @Override
    public void apiPrintTransactionChanged(PrintJobDetail changedTransaction, String key) {
        int changedIndex = transactionIds.indexOf(key);
        if (changedIndex > -1) {
            progressPrintJobDetails.set(changedIndex,changedTransaction);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            printJobPresenterListener.progressItemChanged(changedTransaction,changedIndex);
        }
    }

    @Override
    public void apiPrintTransactionRemoved(PrintJobDetail deletedTransaction, String key) {
        int removeIndex = transactionIds.indexOf(key);
        if (removeIndex > -1) {
            progressPrintJobDetails.remove(removeIndex);
            transactionIds.remove(removeIndex);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemRemoved(removeIndex);
        }
    }

    @Override
    public void connectionFaliure(String s) {
        printJobPresenterListener.showToastError(s);
    }

    public void getPrintJobList() {
        progressPrintJobDetails.clear();
        transactionIds.clear();
        List<PrintJobDetail> savedApiList = SessionManager.getApiPrintJobDetail();
        List<String> savedTransactionIds = SessionManager.getTransactionIds();
        Log.d(TAG,"the saved Api list is" + savedApiList+"saved transactionn is"+savedTransactionIds);
        if (savedApiList!= null
                && savedTransactionIds != null
                && !savedApiList.isEmpty()
                && !savedTransactionIds.isEmpty()) {
            progressPrintJobDetails.addAll(savedApiList);
            transactionIds.addAll(savedTransactionIds);
            printJobPresenterListener.initProgressList(progressPrintJobDetails);
        }
        if (printApi == null) {
            printApi = new PrintApi(firebaseUser.getUid());
        }
        printApi.prepareTransactionListeners(this);
    }

    public void getHistoryList() {
        historyPrintJobDetails.clear();
        historyIds.clear();
        List<PrintJobDetail> savedHistoryList = SessionManager.getHistoryPrintJobDetail();
        List<String> savedHistoryIds = SessionManager.getHistoryIds();
        if (savedHistoryList != null
                && savedHistoryIds != null
                && !savedHistoryList.isEmpty()
                && !savedHistoryList.isEmpty()) {
            historyPrintJobDetails.addAll(savedHistoryList);
            historyIds.addAll(savedHistoryIds);
            printJobPresenterListener.initHistoryList(historyPrintJobDetails);
        }
        if (printApi == null) {
            printApi = new PrintApi(firebaseUser.getUid());
        }
        printApi.prepareHistoryListeners(this);
    }

    public void cancelUpload(PrintJobDetail printJobDetail) {
        if (printJobDetail.getStatus().equals("Uploading")) {
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
            transactionIds.remove(0);
            printJobPresenterListener.progressItemRemoved(0);
        } else {
            printJobDetail.setStatus("Cancelled");
            printApi.cancelTransaction(printJobDetail);
        }
    }

    public void onStopCalled() {
        if (printApi != null) {
            printApi.removeListeners();
        }
    }
}