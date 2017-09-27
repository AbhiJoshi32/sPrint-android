package com.binktec.sprint.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.PrintTransaction;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<PrintTransaction> printTransactions;
    private static String TAG = "Adapter shop";

    public interface ShopAdapterListener {
        void confirmShop(View v, int position);
    }


    public ShopAdapter.ShopAdapterListener shopClickListener;

    public ShopAdapter(List<PrintTransaction> retrievedTransactions, ShopAdapter.ShopAdapterListener listener) {
        this.printTransactions = retrievedTransactions;
        this.shopClickListener = listener;
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.shopNameText)
        TextView shopNameText;
        //        @BindView(R.id.locationText)
        TextView locationText;
        //        @BindView(R.id.totalShopCost)
        TextView totalShopCost;

        TextView printCost;

        TextView bindingCost;

        TextView queueNumber;

        public ShopViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(itemView,itemView);
            shopNameText = itemView.findViewById(R.id.shopNameText);
            locationText = itemView.findViewById(R.id.location);
            totalShopCost = itemView.findViewById(R.id.totalShopCost);
            printCost = itemView.findViewById(R.id.printingCostText);
            bindingCost = itemView.findViewById(R.id.bindingCostText);
            queueNumber = itemView.findViewById(R.id.queueNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shopClickListener.confirmShop(view, getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_choose_shop, parent, false);
        return new ShopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, int position) {
        PrintTransaction printTransaction = printTransactions.get(position);
        float bindinCost = printTransaction.getBindingCost();
        float printCost = printTransaction.getPrintCost();
        float totalCost = bindinCost + printCost;
        holder.printCost.setText("Printing Cost : " + printCost);
        holder.bindingCost.setText("Binding Cost : " + bindinCost );
        holder.locationText.setText(printTransaction.getShop().getShopLocation());
        holder.shopNameText.setText(printTransaction.getShop().getShopName());
        String queueNumber = Integer.toString(printTransaction.getShop().getShopQueue());
        holder.queueNumber.setText(queueNumber);
        holder.totalShopCost.setText("Total : " + totalCost);
    }

    @Override
    public int getItemCount() {
        return this.printTransactions.size();
    }
}
