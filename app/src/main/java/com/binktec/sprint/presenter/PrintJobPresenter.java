package com.binktec.sprint.presenter;


import android.util.Log;

import com.binktec.sprint.interactor.modal.PrintJobModalListener;
import com.binktec.sprint.interactor.presenter.PrintJobPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintJobPresenter implements PrintJobModalListener{
    private static final String TAG = "Print Job Presenter";
    private PrintJobPresenterListener printJobPresenterListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private PrintApi printApi;
    private List<PrintJobDetail> progressPrintJobDetails;
    private List<PrintJobDetail> historyPrintJobDetails;
//    private final static String TAG = "print job pesenter";


    public PrintJobPresenter(PrintJobPresenterListener printJobPresenterListener) {
        this.printJobPresenterListener = printJobPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        printApi = new PrintApi();
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
                }
                else {
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
    }

    @Override
    public void filesUploaded(PrintJobDetail jobDetail) {
        if (SessionManager.getCurrentPrintJobDetail() !=  null) {
            progressPrintJobDetails = new ArrayList<>();
            SessionManager.clearCurrentPrintJob();
            if (SessionManager.getApiPrintJobDetail() != null) {
                progressPrintJobDetails.addAll(SessionManager.getApiPrintJobDetail());
            }
            jobDetail.setStatus("Waiting");
            printApi.enterTransaction(jobDetail);
            printJobPresenterListener.updatePrintJobFragment(progressPrintJobDetails);
        }
    }

    @Override
    public void apiPrintTransactionRetrievalSuccessful(List<PrintJobDetail> apiPrintJobDetails) {

        Log.d(TAG,"api list is " + apiPrintJobDetails);

        if (progressPrintJobDetails == null) {
            progressPrintJobDetails = new ArrayList<>();
        } else {
            progressPrintJobDetails.clear();
        }

        if (historyPrintJobDetails == null) {
            historyPrintJobDetails = new ArrayList<>();
        } else {
            historyPrintJobDetails.clear();
        }

        if (SessionManager.getCurrentPrintJobDetail() != null) {
            progressPrintJobDetails.add(SessionManager.getCurrentPrintJobDetail());
        }

        Collections.reverse(apiPrintJobDetails);

        for (PrintJobDetail apiPrintJob:apiPrintJobDetails) {
            if (!apiPrintJob.getStatus().equals("Printed") && !apiPrintJob.getStatus().equals("Cancelled")){
                progressPrintJobDetails.add(apiPrintJob);
            } else {
                historyPrintJobDetails.add(apiPrintJob);
            }
        }
        SessionManager.saveHistoryPrintJob(historyPrintJobDetails);
        SessionManager.saveApiPrintJob(progressPrintJobDetails);
        printJobPresenterListener.updatePrintJobFragment(progressPrintJobDetails);
        printJobPresenterListener.updateHistoryJobFragment(historyPrintJobDetails);
    }

    @Override
    public void apiPrintTransactionRetrievalUnsuccessful(String s) {
        Log.d(TAG,"Error Occured in retrieval");
    }

    @Override
    public void uploadFailed(PrintJobDetail file) {
        Log.d(TAG,"Error Occurred");
        SessionManager.clearCurrentPrintJob();
    }

    public void getPrintJobList() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (progressPrintJobDetails == null) {
            progressPrintJobDetails = new ArrayList<>();
        } else {
            progressPrintJobDetails.clear();
        }

        if (firebaseUser != null) {
            printApi.getTransactionInfo(this, firebaseUser.getUid());
        }

        if (SessionManager.getCurrentPrintJobDetail() != null ) {
            progressPrintJobDetails.add(SessionManager.getCurrentPrintJobDetail());
        }

        if (SessionManager.getApiPrintJobDetail() != null) {
            progressPrintJobDetails.addAll(SessionManager.getApiPrintJobDetail());
        }

        if (!progressPrintJobDetails.isEmpty()) {
            printJobPresenterListener.updatePrintJobFragment(progressPrintJobDetails);
        }
    }

    public void getHistorySession() {
        if (SessionManager.getHistoryPrintJobDetail() != null) {
            Log.d(TAG,"history session saved" + SessionManager.getHistoryPrintJobDetail());
            historyPrintJobDetails = new ArrayList<>(SessionManager.getHistoryPrintJobDetail());
            printJobPresenterListener.updateHistoryJobFragment(historyPrintJobDetails);
        }
    }

    public void viewCreated() {
        clearTransactionSessions();
        progressPrintJobDetails = new ArrayList<>();
        Log.d(TAG,"View Created");
        if (SessionManager.getCurrentPrintJobDetail() != null) {
            printApi.startFileUpload(SessionManager.getCurrentPrintJobDetail(),this);
        }
    }

    public void cancelUpload(PrintJobDetail printJobDetail) {
        if (printJobDetail.getStatus().equals("Uploading")){
            SessionManager.clearCurrentPrintJob();
            progressPrintJobDetails.remove(0);
        } else {
            Log.d(TAG,"the tid is" + printJobDetail.gettId());
            printApi.cancelTransaction(printJobDetail.gettId(),printJobDetail.getUser().getUid());
        }
        printJobPresenterListener.updatePrintJobFragment(progressPrintJobDetails);
    }
}