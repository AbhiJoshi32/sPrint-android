package com.binktec.sprint.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.modal.pojo.FileDetail;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder>{

    private List<FileDetail> files;

    private MyAdapterListener onClickListener;

    public interface MyAdapterListener {

        void deleteRow(View v, int position);
    }

    @Override
    public FileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public FileAdapter(List<FileDetail> fileList, MyAdapterListener listener) {
        this.files = fileList;
        this.onClickListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FileDetail file = files.get(position);
        holder.fileName.setText(file.getFilename());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private ImageButton deleteBtn;

        MyViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.shopNameText);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.deleteRow(view, getAdapterPosition());
                }
            });
        }
    }
}

