package com.binktec.sprint.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public class PrintJobListAdapter extends RecyclerView.Adapter<PrintJobListAdapter.PrintJobViewHolder> {

    private List<PrintJobDetail> userTransactions;

    public interface PrintJobListListener {
        void showPrintDetail(View v, int position);
        void cancelPrintDetail(View v,int position);
    }

    private PrintJobListAdapter.PrintJobListListener printJobListListener;

    public PrintJobListAdapter(List<PrintJobDetail> retrievedTransactions, PrintJobListAdapter.PrintJobListListener listener) {
        this.userTransactions = retrievedTransactions;
        this.printJobListListener = listener;
    }

    class PrintJobViewHolder extends RecyclerView.ViewHolder {
        TextView file1;
        TextView location;
        TextView fileListText;
        TextView dateText;
        TextView timeText;
        TextView statusText;
        TextView totalCostText;
        Button cancelButton;
        ImageButton showFiles;
        ImageButton hideFiles;

        PrintJobViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(itemView,itemView);
            file1 = itemView.findViewById(R.id.mainFile);
            location = itemView.findViewById(R.id.location);
            fileListText = itemView.findViewById(R.id.fileListText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            totalCostText = itemView.findViewById(R.id.totalCostText);
            statusText = itemView.findViewById(R.id.statusText);
            cancelButton = itemView.findViewById(R.id.cancel);
            showFiles = itemView.findViewById(R.id.showFiles);
            hideFiles = itemView.findViewById(R.id.hideFiles);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    printJobListListener.showPrintDetail(view, getAdapterPosition());
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    printJobListListener.cancelPrintDetail(view,getAdapterPosition());
                }
            });
        }
    }

    @Override
    public PrintJobListAdapter.PrintJobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_print_job, parent, false);
        return new PrintJobListAdapter.PrintJobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PrintJobListAdapter.PrintJobViewHolder holder, final int position) {
        PrintJobDetail detail = userTransactions.get(position);
        if (detail != null) {
            String listOfFiles = "";
            holder.dateText.setText(detail.getIssuedDate());
            holder.timeText.setText(detail.getIssuedTime());
            holder.location.setText(detail.getPrintTransaction().getShop().getShopLocation());
            int size = detail.getPrintTransaction().getFileDetails().size();
            if (size>1) {
                for (int i=1;i<size;i++) {
                    listOfFiles += detail.getPrintTransaction().getFileDetails().get(i).getFilename() + "\n";
                }
                holder.showFiles.setVisibility(View.VISIBLE);
                holder.hideFiles.setVisibility(View.GONE);
                listOfFiles = listOfFiles.trim();
                holder.fileListText.setText(listOfFiles);
            } else {
                holder.showFiles.setVisibility(View.GONE);
                holder.hideFiles.setVisibility(View.GONE);
            }
            holder.statusText.setText(detail.getStatus());
            holder.hideFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.fileListText.setVisibility(View.GONE);
                    holder.hideFiles.setVisibility(View.GONE);
                    holder.showFiles.setVisibility(View.VISIBLE);
                }
            });
            holder.showFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.fileListText.setVisibility(View.VISIBLE);
                    holder.showFiles.setVisibility(View.GONE);
                    holder.hideFiles.setVisibility(View.VISIBLE);
                }
            });
            holder.file1.setText(detail.getPrintTransaction().getFileDetails().get(0).getFilename());
            float totalCost = detail.getPrintTransaction().getBindingCost() + detail.getPrintTransaction().getPrintCost();
            String totalCostString = "Total Cost: "+totalCost;
            holder.totalCostText.setText(totalCostString);
        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG,"shop obj is"+shops.toString());
        return this.userTransactions.size();
    }
}
