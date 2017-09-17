package com.binktec.sprint.modal.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
//import android.util.Log;

import com.binktec.sprint.interactor.modal.PrintJobModalListener;
import com.binktec.sprint.interactor.modal.TransactionModalListener;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public void getShopInfoApi(final TransactionModalListener callback) {
        DatabaseReference myref = database.getReference("shop-client/shop-info");
        myref.addValueEventListener(new ValueEventListener() {
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
            DatabaseReference userRef = database.getReference("userTransaction/" + printJobDetail.getPrintTransaction().getUid());
            String key = userRef.push().getKey();
            printJobDetail.settId(key);
            userRef.child(key).setValue(printJobDetail);
            DatabaseReference printRef = database.getReference("printTransaction/" + printJobDetail.getPrintTransaction().getShop().getShopId());
            printJobDetail.getPrintTransaction().setShop(null);
            printRef.child(key).setValue(printJobDetail);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startFileUpload(final PrintJobDetail printJobDetail, final PrintJobModalListener callback) {
        StorageReference storageRef = storage.getReference();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        try {
                final int numOfFiles = printJobDetail.getPrintTransaction().getFileDetails().size();
                final List<FileDetail> uploadFiles = new ArrayList<>();
                for (final FileDetail file : printJobDetail.getPrintTransaction().getFileDetails()) {
                    Uri uri = Uri.parse(file.getUri());
                   final UploadTask uploadTask = storageRef.
                            child("docs/" + printJobDetail.getPrintTransaction()
                                    .getUid() + "/" + file.getFilename() + uri.getLastPathSegment())
                            .putFile(uri,metadata);
                    StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                            Log.d(TAG,"Upload is " + progress + "% done");
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
                                    Log.d(TAG,"File Uploaded");
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

    public void getTransactionInfo(final PrintJobModalListener callback,String uid) {
        DatabaseReference myref = database.getReference("userTransaction/"+uid);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG,"Value got from firebase");
//                Log.d(TAG,dataSnapshot.toString());
                List<PrintJobDetail> apiPrintJobDetail = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PrintJobDetail printJobDetail = snapshot.getValue(PrintJobDetail.class);
                    apiPrintJobDetail.add(printJobDetail);
                }
                callback.apiPrintTransactionRetrievalSuccessful(apiPrintJobDetail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "Failed to read value" + databaseError.toString());
                callback.apiPrintTransactionRetrievalUnsuccessful(databaseError.toString());
            }
        });
    }

    public void cancelTransaction(String tId, String uid) {
        DatabaseReference userRef = database.getReference("userTransaction/" + uid);
        userRef.child(tId).child("status").setValue("Cancelled");
    }
}