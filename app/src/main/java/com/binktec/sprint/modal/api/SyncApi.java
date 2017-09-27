package com.binktec.sprint.modal.api;

import com.binktec.sprint.interactor.modal.SyncListener;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SyncApi {

    private SyncListener syncListener;
    private DatabaseReference progressRef;
    private DatabaseReference historyRef;

    public SyncApi(SyncListener syncListener, String uid) {
        this.syncListener = syncListener;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        progressRef = database.getReference("userTransaction/"+uid);
        historyRef = database.getReference("userCompleted/"+uid);
    }

    public void syncProgressJobs() {
        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PrintJobDetail> printJobDetailList = new ArrayList<>();
                List<String> transactionIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PrintJobDetail printJobDetail = snapshot.getValue(PrintJobDetail.class);
                    printJobDetailList.add(0,printJobDetail);
                    transactionIds.add(0,snapshot.getKey());
                }
                syncListener.setProgressSession(printJobDetailList,transactionIds);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void syncHistoryJobs() {
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PrintJobDetail> printJobDetailList = new ArrayList<>();
                List<String> historyIds = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PrintJobDetail printJobDetail = snapshot.getValue(PrintJobDetail.class);
                        if (printJobDetail != null && !printJobDetail.getStatus().equals("Cancelled")) {
                            printJobDetailList.add(0, printJobDetail);
                            historyIds.add(0, snapshot.getKey());
                        }
                    }
                }
                syncListener.setHistorySession(printJobDetailList,historyIds);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
