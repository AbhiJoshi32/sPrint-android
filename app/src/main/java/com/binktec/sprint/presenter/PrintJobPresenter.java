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
                    if (SessionManager.getApiPrintJobDetail() != null
                            && SessionManager.getTransactionIds() !=null) {
                        List<PrintJobDetail> apiProgressPrintDetail = new ArrayList<>(SessionManager.getApiPrintJobDetail());
                        List<String> savedTransactionIds = new ArrayList<>(SessionManager.getTransactionIds());
                        progressPrintJobDetails.addAll(apiProgressPrintDetail);
                        transactionIds.addAll(savedTransactionIds);
                    }
                    if (currJobDetail != null) {
                        progressPrintJobDetails.add(0,currJobDetail);
                        transactionIds.add(0,"Uploading File");
                        Log.d(TAG,"current file" + currJobDetail + "\n progressPrint " + progressPrintJobDetails);
                        printApi.startFileUpload(currJobDetail,this);
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
        if (transactionIds.get(0).equals("Uploading File")) {
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
            transactionIds.remove(0);
            printJobPresenterListener.progressItemRemoved(0);
            printJobPresenterListener.showToastError("upload Failed");
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        }
    }

    @Override
    public void filesUploaded(PrintJobDetail jobDetail) {
        if (transactionIds.get(0).equals("Uploading File")) {
            Log.d(TAG,"File Uploaded");
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
            transactionIds.remove(0);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemRemoved(0);
            jobDetail.setStatus("Waiting");
            printApi.enterTransaction(jobDetail);
        }
    }

    @Override
    public void uploadFailed(PrintJobDetail file) {
        if (transactionIds.get(0).equals("Uploading File")) {
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
            transactionIds.remove(0);
            printJobPresenterListener.progressItemRemoved(0);
            printJobPresenterListener.showToastError("upload Failed");
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        }
    }

    @Override
    public void apiHistoryAdded(PrintJobDetail historyDetail) {
        if (SessionManager.getTransactionIds() != null && SessionManager.getHistoryPrintJobDetail() != null) {
            historyIds = SessionManager.getHistoryIds();
            historyPrintJobDetails = SessionManager.getHistoryPrintJobDetail();
        }
        if (historyIds.indexOf(historyDetail.gettId()) == -1) {
            Log.d(TAG,"New history added");
            historyPrintJobDetails.add(0,historyDetail);
            historyIds.add(0,historyDetail.gettId());
            SessionManager.saveHistoryPrintJob(historyPrintJobDetails);
            SessionManager.saveHistoryIds(historyIds);
            apiPrintTransactionRemoved(historyDetail,historyDetail.gettId());
            printJobPresenterListener.historyItemInserted(historyDetail,0);
        }
    }

    @Override
    public void apiPrintTransactionAdded(PrintJobDetail transactionDetail, String key, String prevKey) {
        int topIndex;
        if (SessionManager.getCurrentPrintJobDetail() != null) {
            topIndex = 1;
        } else {
            topIndex = 0;
        }
        Log.d(TAG,"top index is" + topIndex);
        if (prevKey == null) {
            progressPrintJobDetails.clear();
            transactionIds.clear();
            PrintJobDetail uploadingDetail = SessionManager.getCurrentPrintJobDetail();
            if (uploadingDetail != null) {
                progressPrintJobDetails.add(0,uploadingDetail);
                transactionIds.add(0,"Uploading File");
            }
            printJobPresenterListener.initProgressList(progressPrintJobDetails);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
        } else {
            progressPrintJobDetails.add(topIndex,transactionDetail);
            transactionIds.add(topIndex,key);
            SessionManager.saveApiPrintJob(progressPrintJobDetails);
            SessionManager.saveTrasactionIds(transactionIds);
            printJobPresenterListener.progressItemInserted(transactionDetail, topIndex);
        }
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
        if (SessionManager.getApiPrintJobDetail() != null
                && SessionManager.getTransactionIds() != null
                && !SessionManager.getApiPrintJobDetail().isEmpty()
                && !SessionManager.getTransactionIds().isEmpty()) {
            progressPrintJobDetails.addAll(SessionManager.getApiPrintJobDetail());
            transactionIds.addAll(SessionManager.getTransactionIds());
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
//        Log.d(TAG,"History frag is " + SessionManager.getHistoryPrintJobDetail() + "trans id is" + SessionManager.getTransactionIds());
        List<PrintJobDetail> savedHistoryList = SessionManager.getHistoryPrintJobDetail();
        List<String> savedHistoryIds = SessionManager.getHistoryIds();
//        Log.d(TAG,"History saved frag is " + savedHistoryList + "historyId id saved is" + savedHistoryIds);
        if (SessionManager.getHistoryPrintJobDetail() != null
                && SessionManager.getHistoryIds() != null
                && !SessionManager.getHistoryPrintJobDetail().isEmpty()
                && !SessionManager.getHistoryIds().isEmpty()) {
            Log.d(TAG,"saved History list is" + savedHistoryList);
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
//            printApi.removeListeners();
        }
    }
}