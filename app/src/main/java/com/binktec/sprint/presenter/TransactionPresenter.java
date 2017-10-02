package com.binktec.sprint.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.binktec.sprint.interactor.modal.TransactionModalListener;
import com.binktec.sprint.interactor.presenter.TransactionPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintDetail;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.modal.pojo.PrintTransaction;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.utility.Constants;
import com.binktec.sprint.utility.Misc;
import com.binktec.sprint.utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionPresenter implements TransactionModalListener {
    private static final int FILE_ERROR = 1;
    private static Handler handler;

    private static final int FILE_PARSED = 0;

    private TransactionPresenterListener transactionPresenterListener;
    private FirebaseAuth firebaseAuth;
    private List<FileDetail> chosenFiles = null;
    private PrintApi printApi;

    public TransactionPresenter(final TransactionPresenterListener transactionPresenterListener) {
        this.transactionPresenterListener = transactionPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        handler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case FILE_PARSED:
                        transactionPresenterListener.showToastError("File Processed");
                        transactionPresenterListener.enableDone();
                        break;
                    case FILE_ERROR:
                        SessionManager.getFileDetail();
                        transactionPresenterListener.FileError(chosenFiles);
                }
            }
        };
    }

    private void clearTransactionSession() {
        SessionManager.clearFileDetail();
        SessionManager.clearPrintDetail();
    }

    public void appStart() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        clearTransactionSession();
        if (firebaseUser != null) {
            printApi = new PrintApi(firebaseUser.getUid());
            if (SessionManager.getCurrentPrintJobDetail() != null) {
                transactionPresenterListener.ongoingUpload();
            } else {
                transactionPresenterListener.initTransactionActivity();
            }
        }
    }

    public void doneChooseFile() {
        try {
            if (chosenFiles == null) {
                transactionPresenterListener.showFileError("empty");
            } else if (chosenFiles.isEmpty()) {
                transactionPresenterListener.showFileError("empty");
            } else {
                transactionPresenterListener.updatePrintDetails(chosenFiles.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFileDetailList(final Uri selectedUri, Context context) {
        if (SessionManager.getFileDetail() != null) {
            chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
        }
        else {
            chosenFiles = new ArrayList<>();
        }
        String[] nameAndPath = Misc.getFileNameAndPath(selectedUri, context);
        final FileDetail fileAdded = new FileDetail();
        final int fileListSize;
        fileAdded.setUri(selectedUri.toString());
        fileAdded.setFilename(nameAndPath[0]);
        fileAdded.setPath(nameAndPath[1]);
        chosenFiles.add(fileAdded);
        fileListSize = chosenFiles.size();
        transactionPresenterListener.disableDone();
        new Thread() {
            public void run() {
                fileAdded.setNumberOfPages(Misc.getNumPages(fileAdded.getPath()));
                if (fileListSize == chosenFiles.size()) {
                    chosenFiles.remove(chosenFiles.size() - 1);
                    chosenFiles.add(fileAdded);
                    SessionManager.fileDetailSave(chosenFiles);
                    Message msg = Message.obtain();
                    if (fileAdded.getNumberOfPages() < 1) {
                        msg.what = FILE_ERROR;
                        chosenFiles.remove(chosenFiles.size() - 1);
                        SessionManager.fileDetailSave(chosenFiles);
                    }
                    else
                        msg.what = FILE_PARSED;
                    handler.sendMessage(msg);
                }
            }
        }.start();
        SessionManager.fileDetailSave(chosenFiles);
        transactionPresenterListener.updateChooseFileFragment(chosenFiles);
    }

    public void removeSelectedFile(int position) {
        if (chosenFiles == null){
            if (SessionManager.getFileDetail() == null) {
                transactionPresenterListener.showPrintDetailError("empty File List");
            } else {
                chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
            }
        }
        if (position == chosenFiles.size() - 1){
            transactionPresenterListener.enableDone();
        }
        chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
        chosenFiles.remove(position);
        SessionManager.fileDetailSave(chosenFiles);

        transactionPresenterListener.updateChooseFileFragment(chosenFiles);
    }

    public void confirmPrintDetail(PrintDetail detail) {
        try {
            int totalPages = 0;
            int pagesToPrint = 0;
            if (chosenFiles == null) {
                if (SessionManager.getFileDetail() == null) {
                    transactionPresenterListener.showFileError("empty File List");
                } else {
                    chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
                }
            }
            for (FileDetail fileDetail : chosenFiles) {
                int filePages = fileDetail.getNumberOfPages();
                totalPages +=  filePages;
                if (detail.getPagesPerSheet() == 2) {
                    if (filePages%2 == 0)
                        filePages = filePages/2;
                    else
                        filePages = filePages/2+1;
                }
                pagesToPrint += filePages;
            }
            if (detail.getPagesText() != null && !detail.getPagesText().isEmpty()) {

                Pattern p = Pattern.compile(Constants.pagesTextRegex);
                Matcher m;
                m = p.matcher(detail.getPagesText());
                if (!m.matches()) {
                    transactionPresenterListener.showPrintDetailError("Invalid Entry");
                } else {
                    pagesToPrint = Misc.getFromPagesText(detail.getPagesText(), totalPages);
                    if (pagesToPrint == -1) {
                        transactionPresenterListener.showPrintDetailError("Invalid Entry");
                    } else {
                        if (detail.getPagesPerSheet() == 2) {
                            if (pagesToPrint%2 == 0)
                                pagesToPrint = pagesToPrint/2;
                            else
                                pagesToPrint = pagesToPrint/2+1;
                        }
                        detail.setPagesToPrint(pagesToPrint * detail.getCopies());
                        SessionManager.savePrintDetail(detail);
                        transactionPresenterListener.proceedToShopSelection();
                        getShopList();
                    }
                }
            } else {
                pagesToPrint = pagesToPrint * detail.getCopies();
                detail.setPagesToPrint(pagesToPrint);
                SessionManager.savePrintDetail(detail);
                transactionPresenterListener.proceedToShopSelection();
                getShopList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getShopList() {
        printApi.getShopInfoApi(this);
        try {
            if (!isConnected()) {
                transactionPresenterListener.shopRetrivalError();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiShopRetrievalSuccessful(List<Shop> apiShops) {
        try {
            List<PrintTransaction> printTransactions = new ArrayList<>();
            PrintDetail printDetail = SessionManager.getPrintDetail();
            List<FileDetail> fileDetail = SessionManager.getFileDetail();
            if (fileDetail != null && printDetail != null) {
                for (Shop shop : apiShops) {
                    if (((printDetail.getPrintColor().equals("Color") && shop.getShopAvailColor().equals("yes"))
                            ||printDetail.getPrintColor().equals("Grayscale"))
                            && shop.getAvailBinding().contains(printDetail.getBindingType())
                            && shop.getShopAvailPaperType().contains(printDetail.getPrintPaperType())
                            && shop.getShopAvailability().equals("yes")) {
                        float printCost = Misc.getPrintCost(printDetail, shop);
                        if (printCost != 0) {
                            PrintTransaction printTransaction = new PrintTransaction();
                            printTransaction.setFileDetails(fileDetail);
                            printTransaction.setShop(shop);
                            printTransaction.setPrintDetail(printDetail);
                            printTransaction.setPrintCost(printCost);
                            printTransaction.setBindingCost(Misc.getBindCost(shop, printDetail));
                            printTransactions.add(printTransaction);
                        }
                    }
                }
                transactionPresenterListener.updateShopFragment(printTransactions);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiShopRetrievalUnsuccessful(String s) {
        transactionPresenterListener.shopRetrivalError();
    }

    public void confirmTransaction(PrintTransaction printTransaction) {
        try {
            PrintJobDetail printJobDetail = new PrintJobDetail();
            printJobDetail.setPrintTransaction(printTransaction);
            printJobDetail.setStatus("Uploading");
            printJobDetail.setUser(SessionManager.getUser());
            printJobDetail.setIssuedDate(Misc.getDate());
            printJobDetail.setIssuedTime(Misc.getTime());
            SessionManager.saveCurrentPrintJob(printJobDetail);
            transactionPresenterListener.openPrintJobActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStopCalled() {
        if (printApi != null) {
            printApi.removeShopListener();
        }
    }

    public void onDestroyCalled() {
        transactionPresenterListener = null;
    }

    public void onStartCalled() {
    }

    private boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}