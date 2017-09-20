package com.binktec.sprint.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionPresenter implements TransactionModalListener {

    private static final String TAG = "Transaction Presenter";
    private Handler handler;

    private static final int FILE_PARSED = 0;

    private TransactionPresenterListener transactionPresenterListener;
    private FirebaseAuth firebaseAuth;
    private List<FileDetail> chosenFiles = null;
    private PrintApi printApi;

    public TransactionPresenter(final TransactionPresenterListener transactionPresenterListener) {
        this.transactionPresenterListener = transactionPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case FILE_PARSED:
                        transactionPresenterListener.enableDone();
                        break;
                }
            }
        };
    }

    public void appStart() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            printApi = new PrintApi(firebaseUser.getUid());
            if (SessionManager.getCurrentPrintJobDetail() != null) {
                transactionPresenterListener.ongoingUpload();
            } else {
                if (firebaseUser.getPhotoUrl() != null) {
                    transactionPresenterListener.initTransactionActivity(firebaseUser.getDisplayName(),
                            firebaseUser.getPhotoUrl().toString());
                } else {
                    transactionPresenterListener.initTransactionActivity(firebaseUser.getDisplayName(),
                            "");
                }
            }

        }
        if (SessionManager.getFileDetail() != null) {
            chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
        }
    }

    public void doneChooseFile() {
        try {
            if (chosenFiles == null) {
                Log.d(TAG,"Null chosen File");
                transactionPresenterListener.showFileError("empty");
            } else if (chosenFiles.isEmpty()) {
                Log.d(TAG,"Empty chosed file");
                transactionPresenterListener.showFileError("empty");
            } else {
                Log.d(TAG,"Updating");
                Log.d(TAG,"Size is " +chosenFiles.size());
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
        try {
            String[] nameAndPath = Misc.getFileNameAndPath(selectedUri, context);
            final FileDetail fileAdded = new FileDetail();
            fileAdded.setUri(selectedUri.toString());
            fileAdded.setFilename(nameAndPath[0]);
            fileAdded.setPath(nameAndPath[1]);
            Log.d(TAG,fileAdded.getPath());
            chosenFiles.add(fileAdded);
            transactionPresenterListener.disableDone();
            new Thread() {
                public void run() {
                    Log.d(TAG,"Thhread ran");
                    fileAdded.setNumberOfPages(Misc.getNumPages(fileAdded.getPath()));
                    chosenFiles.remove(chosenFiles.size()-1);
                    chosenFiles.add(fileAdded);
                    SessionManager.fileDetailSave(chosenFiles);
                    Log.d(TAG,"number of pages updated +"+SessionManager.getFileDetail().get(0).getNumberOfPages());
                    Message msg = Message.obtain();
                    msg.what = FILE_PARSED;
                    handler.sendMessage(msg);
                }
            }.start();
            Log.d(TAG, "Added from non thread");
            Log.d(TAG, "The size of the file list is " + chosenFiles.size());
            SessionManager.fileDetailSave(chosenFiles);
            transactionPresenterListener.updateChooseFileFragment(chosenFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSelectedFile(int position) {
        if (chosenFiles == null){
            if (SessionManager.getFileDetail() == null) {
                transactionPresenterListener.showPrintDetailError("empty File List");
            } else {
                chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
            }
        }
        if (!chosenFiles.isEmpty()) {
            chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
            chosenFiles.remove(position);
            SessionManager.fileDetailSave(chosenFiles);
            Log.d(TAG, Integer.toString(chosenFiles.size()));
        } else {
            Log.d(TAG,"Nothong to remove in list");
        }
        transactionPresenterListener.updateChooseFileFragment(chosenFiles);
    }

    public void confirmPrintDetail(PrintDetail detail) {
        try {
            int totalPages = 0;
            int pagesToPrint;
            if (chosenFiles == null) {
                if (SessionManager.getFileDetail() == null) {
                    transactionPresenterListener.showFileError("empty File List");
                } else {
                    chosenFiles = new ArrayList<>(SessionManager.getFileDetail());
                }
            }
            for (FileDetail fileDetail : chosenFiles) {
                totalPages += fileDetail.getNumberOfPages();
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
                        detail.setPagesToPrint(pagesToPrint * detail.getCopies());
                        SessionManager.savePrintDetail(detail);
                        transactionPresenterListener.proceedToShopSelection();
                        getShopList();
                    }
                }
            } else {
                pagesToPrint = totalPages * detail.getCopies();
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
    }

    @Override
    public void apiShopRetrievalSuccessful(List<Shop> apiShops) {
        try {
            List<PrintTransaction> printTransactions = new ArrayList<>();
            PrintDetail printDetail = SessionManager.getPrintDetail();
            List<FileDetail> fileDetail = SessionManager.getFileDetail();
            if (fileDetail != null && printDetail != null) {
                for (Shop shop : apiShops) {
                    Log.d(TAG,"shop binding"+shop.getAvailBinding().contains(printDetail.getBindingType()));
                    Log.d(TAG,"shop Paper type"+shop.getShopAvailPaperType().contains(printDetail.getPrintPaperType()));
                    if (((printDetail.getPrintColor().equals("Color") && shop.getShopAvailColor().equals("yes"))
                            ||printDetail.getPrintColor().equals("Grayscale"))
                            && shop.getAvailBinding().contains(printDetail.getBindingType())
                            && shop.getShopAvailPaperType().contains(printDetail.getPrintPaperType())
                            && shop.getShopAvailability().equals("yes")) {
                        PrintTransaction printTransaction = new PrintTransaction();
                        printTransaction.setFileDetails(fileDetail);
                        printTransaction.setShop(shop);
                        printTransaction.setPrintDetail(printDetail);
                        printTransaction.setPrintCost(Misc.getPrintCost(printDetail, shop));
                        printTransaction.setBindingCost(Misc.getBindCost(shop, printDetail));
                        Log.d(TAG, "the detail from api" + printDetail + SessionManager.getFileDetail() + printTransactions);

                        printTransactions.add(printTransaction);
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
            Log.d(TAG, "uploading done");
            transactionPresenterListener.openPrintJobActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}