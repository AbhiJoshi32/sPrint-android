package com.binktec.sprint.modal.api;

import android.net.Uri;
import android.support.annotation.NonNull;
////import android.util.Log;

import com.binktec.sprint.interactor.modal.PrintJobModalListener;
import com.binktec.sprint.interactor.modal.TransactionModalListener;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class PrintApi {
    private int numFileUploded = 0;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String TAG="ShopApi";
    private DatabaseReference shopRef = database.getReference("shop-client/shop-info");
    private DatabaseReference userTransactionRef;
    private DatabaseReference userCompletedRef;

    private DatabaseReference printTransactionRef = database.getReference("printTransaction");
    private DatabaseReference printCompletedRef = database.getReference("printCompleted");
    private String uid;

    private ChildEventListener transactionInfoListener;
    private ChildEventListener completedInfoListener;

    public PrintApi(String uid) {
        this.uid = uid;
        userCompletedRef = database.getReference("userCompleted/" + uid);
        userTransactionRef = database.getReference("userTransaction/" + uid);
    }

    public void getShopInfoApi(final TransactionModalListener callback) {
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Shop> apiShops = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Shop shop = snapshot.getValue(Shop.class);
                    apiShops.add(shop);
                }
                callback.apiShopRetrievalSuccessful(apiShops);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.apiShopRetrievalUnsuccessful(databaseError.toString());
            }
        });
    }

    public void enterTransaction(PrintJobDetail printJobDetail) {
        try {
            String key = userTransactionRef.push().getKey();
            printJobDetail.settId(key);
            userTransactionRef.child(key).setValue(printJobDetail);
            DatabaseReference printRef = printTransactionRef.child(printJobDetail.getPrintTransaction().getShop().getShopId());
            printJobDetail.getPrintTransaction().setShop(null);
            printRef.child(key).setValue(printJobDetail);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startFileUpload(final PrintJobDetail printJobDetail, final PrintJobModalListener callback) {
        StorageReference storageRef = storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/pdf")
                .build();
        try {
                final int numOfFiles = printJobDetail.getPrintTransaction().getFileDetails().size();
                final List<FileDetail> uploadFiles = new ArrayList<>();
                for (final FileDetail file : printJobDetail.getPrintTransaction().getFileDetails()) {
                    Uri uri = Uri.parse(file.getUri());
                   final UploadTask uploadTask = storageRef.
                            child("docs/" + printJobDetail.getUser()
                                    .getUid() + "/" + file.getFilename() + uri.getLastPathSegment())
                            .putFile(uri,metadata);
                    StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
//                            Log.d(TAG,"Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            callback.uploadFailed(printJobDetail);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getDownloadUrl() != null) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                file.setDownloadUrl(downloadUrl.toString());
                                uploadFiles.add(file);
                                numFileUploded++;
                                if (numFileUploded == numOfFiles) {
//                                    Log.d(TAG,"File Uploaded");
                                    printJobDetail.getPrintTransaction().setFileDetails(uploadFiles);
                                    callback.filesUploaded(printJobDetail);
                                }
                            } else {
                                callback.uploadFailed(printJobDetail);
                            }
                        }
                    });
                }
        }catch (Exception e) {
            callback.uploadFailed("Exception Ocurred");
            e.printStackTrace();
        }
    }


    public void cancelTransaction(PrintJobDetail cancelledDetail) {
        String key = cancelledDetail.gettId();
        String shopId = cancelledDetail.getPrintTransaction().getShop().getShopId();
        userTransactionRef.child(key).setValue(null);
        userCompletedRef.child(key).setValue(cancelledDetail);
        printTransactionRef.child(shopId).child(key).setValue(null);
        printCompletedRef.child(shopId).child(key).setValue(cancelledDetail);
    }

    public void prepareHistoryListeners(final PrintJobModalListener callback) {
        completedInfoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG,"history child added");
                PrintJobDetail historyDetail = dataSnapshot.getValue(PrintJobDetail.class);
                callback.apiHistoryAdded(historyDetail);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userCompletedRef.addChildEventListener(completedInfoListener);
    }

    public void prepareTransactionListeners(final PrintJobModalListener callback) {
        transactionInfoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevKey) {
//                Log.d(TAG,"child added");
                PrintJobDetail transactionDetail = dataSnapshot.getValue(PrintJobDetail.class);
                callback.apiPrintTransactionAdded(transactionDetail,dataSnapshot.getKey(),prevKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG,"child changed");
                PrintJobDetail changedTransaction = dataSnapshot.getValue(PrintJobDetail.class);
                callback.apiPrintTransactionChanged(changedTransaction,dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG,"child removed");
//                PrintJobDetail deletedTransaction = dataSnapshot.getValue(PrintJobDetail.class);
//                callback.apiPrintTransactionRemoved(deletedTransaction,dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.connectionFaliure(databaseError.toString());
            }
        };

        userTransactionRef.addChildEventListener(transactionInfoListener);
    }

    public void removeListeners() {
        if (transactionInfoListener != null) {
            userTransactionRef.removeEventListener(transactionInfoListener);
            userCompletedRef.removeEventListener(completedInfoListener);
        }
    }

    public void dummyEntry() {
        userTransactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    String key = userCompletedRef.push().getKey();
                    userTransactionRef.child(key).child("tId").setValue(key);
                    userTransactionRef.child(key).child("status").setValue("Dummy");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}