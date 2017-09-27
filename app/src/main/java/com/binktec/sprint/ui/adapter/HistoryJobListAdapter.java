package com.binktec.sprint.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.PrintJobDetail;

import java.util.List;

public class HistoryJobListAdapter extends RecyclerView.Adapter<HistoryJobListAdapter.HistoryJobViewHolder> {

    private List<PrintJobDetail> userTransactions;

    public interface HistoryJobListListener {
        void showPrintDetail(View v, int position);
    }

    private HistoryJobListAdapter.HistoryJobListListener printJobListListener;

    public HistoryJobListAdapter(List<PrintJobDetail> retrievedTransactions, HistoryJobListAdapter.HistoryJobListListener listener) {
        this.userTransactions = retrievedTransactions;
        this.printJobListListener = listener;
    }

    class HistoryJobViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.shopNameText)
        TextView file1;
        //        @BindView(R.id.locationText)
        TextView location;
        TextView fileListText;
        TextView shopName;
        //        @BindView(R.id.totalShopCost)
        TextView dateText;
        TextView timeText;
        TextView statusText;
        TextView totalCostText;

        ImageButton showFiles;
        ImageButton hideFiles;
        TextView pinNumber;

        HistoryJobViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(itemView,itemView);
            file1 = itemView.findViewById(R.id.mainFile);
            location = itemView.findViewById(R.id.location);
            fileListText = itemView.findViewById(R.id.fileListText);
            shopName = itemView.findViewById(R.id.shopName);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            totalCostText = itemView.findViewById(R.id.totalCostText);
            statusText = itemView.findViewById(R.id.statusText);
            showFiles = itemView.findViewById(R.id.showFiles);
            hideFiles = itemView.findViewById(R.id.hideFiles);
            pinNumber = itemView.findViewById(R.id.pinNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    printJobListListener.showPrintDetail(view, getAdapterPosition());
                }
            });

        }
    }

    @Override
    public HistoryJobListAdapter.HistoryJobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_print_history, parent, false);
        return new HistoryJobListAdapter.HistoryJobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HistoryJobListAdapter.HistoryJobViewHolder holder, int position) {
        PrintJobDetail detail = userTransactions.get(position);
        if (detail != null) {
            String listOfFiles = "";
            String pin = Integer.toString(detail.getPin());
            holder.dateText.setText(detail.getIssuedDate());
            holder.timeText.setText(detail.getIssuedTime());
            holder.shopName.setText(detail.getPrintTransaction().getShop().getShopName());
            holder.location.setText(detail.getPrintTransaction().getShop().getShopLocation());
            holder.pinNumber.setText(pin);
            int size = detail.getPrintTransaction().getFileDetails().size();
            if (size>1) {
                for (int i=1;i<size;i++) {
                    listOfFiles += detail.getPrintTransaction().getFileDetails().get(i).getFilename() + "\n";
                }
                listOfFiles = listOfFiles.trim();
                holder.showFiles.setVisibility(View.VISIBLE);
                holder.hideFiles.setVisibility(View.GONE);
                listOfFiles = listOfFiles.trim();
                holder.fileListText.setText(listOfFiles);
            } else {
                holder.showFiles.setVisibility(View.GONE);
                holder.hideFiles.setVisibility(View.GONE);
            }

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
            holder.statusText.setText(detail.getStatus());
            holder.file1.setText(detail.getPrintTransaction().getFileDetails().get(0).getFilename());
            float totalCost = detail.getPrintTransaction().getBindingCost() + detail.getPrintTransaction().getPrintCost();
            String totalCostString = "Total Cost: "+totalCost;
            holder.totalCostText.setText(totalCostString);
        }
    }

    @Override
    public int getItemCount() {
        return this.userTransactions.size();
    }
}
