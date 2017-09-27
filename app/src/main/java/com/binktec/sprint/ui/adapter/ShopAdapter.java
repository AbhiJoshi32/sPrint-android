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

    public interface ShopAdapterListener {
        void confirmShop(View v, int position);
    }


    private ShopAdapter.ShopAdapterListener shopClickListener;

    public ShopAdapter(List<PrintTransaction> retrievedTransactions, ShopAdapter.ShopAdapterListener listener) {
        this.printTransactions = retrievedTransactions;
        this.shopClickListener = listener;
    }

    class ShopViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.shopNameText)
        TextView shopNameText;
        //        @BindView(R.id.locationText)
        TextView locationText;
        //        @BindView(R.id.totalShopCost)
        TextView totalShopCost;

        TextView printCost;

        TextView bindingCost;

        TextView queueNumber;

        ShopViewHolder(View itemView) {
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
        String printCostString = "Printing Cost : " + printCost;
        String bindCostString = "Binding Cost : " + bindinCost;
        String totalCostString = "Total : " + totalCost;
        holder.printCost.setText(printCostString);
        holder.bindingCost.setText(bindCostString);
        holder.locationText.setText(printTransaction.getShop().getShopLocation());
        holder.shopNameText.setText(printTransaction.getShop().getShopName());
        String queueNumber = Integer.toString(printTransaction.getShop().getShopQueue());
        holder.queueNumber.setText(queueNumber);
        holder.totalShopCost.setText(totalCostString);
    }

    @Override
    public int getItemCount() {
        return this.printTransactions.size();
    }
}
