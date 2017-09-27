package com.binktec.sprint.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.shop.Shop;

import java.util.List;


public class AvailShopAdapter extends RecyclerView.Adapter<AvailShopAdapter.AvailShopViewHolder> {
    private List<Shop> shops;

    public interface AvailShopAdapterListener {
        void detailShop(View v, int position);
    }


    private AvailShopAdapterListener shopClickListener;

    public AvailShopAdapter(List<Shop> retrievedShops, AvailShopAdapterListener listener) {
        this.shops = retrievedShops;
        this.shopClickListener = listener;
    }

    class AvailShopViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.shopNameText)
        TextView shopNameText;
//        @BindView(R.id.locationTextn)
        TextView locationText;
//        @BindView(R.id.availablePaperSize)
        TextView availablePaperSize;
//        @BindView(R.id.colourAvailableText)
        TextView colourAvailableText;
//        @BindView(R.id.bindingTypeText)
        TextView bindingTypeText;

        AvailShopViewHolder(View itemView) {
            super(itemView);
            shopNameText = itemView.findViewById(R.id.shopNameText);
            locationText = itemView.findViewById(R.id.location);
            availablePaperSize = itemView.findViewById(R.id.paperSizesText);
            colourAvailableText = itemView.findViewById(R.id.colourAvailableText);
            bindingTypeText = itemView.findViewById(R.id.bindingTypeText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shopClickListener.detailShop(view, getAdapterPosition());
                }
            });
        }
    }

    @Override
    public AvailShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_available_shops, parent, false);
        return new AvailShopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AvailShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        String pageTypeArray = "";
        String bindArray = "";
        for (String type:shop.getShopAvailPaperType()) {
            pageTypeArray += type;
            pageTypeArray += "  ";
        }
        for (String bind:shop.getAvailBinding()) {
            bindArray += bind;
            bindArray += "  ";
        }
        holder.shopNameText.setText(shop.getShopName());
        holder.locationText.setText(shop.getShopLocation());
        holder.availablePaperSize.setText(pageTypeArray);
        holder.bindingTypeText.setText(bindArray);
        holder.colourAvailableText.setText(shop.getShopAvailColor());
    }

    @Override
    public int getItemCount() {
        return this.shops.size();
    }
}
