package com.example.albumapp.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;

import java.util.ArrayList;
import java.util.List;

public class NewParentItemAdapter extends RecyclerView.Adapter<NewParentItemAdapter.NewParentViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<NewParentItem> date_list;
    private Context mContext;

    public NewParentItemAdapter(Context context, List<NewParentItem> date_list) {
        this.mContext = context;
        this.date_list = date_list;
    }

    @NonNull
    @Override
    public NewParentItemAdapter.NewParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_item_parent, viewGroup, false);
        return new NewParentItemAdapter.NewParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewParentItemAdapter.NewParentViewHolder holder, int position) {
        NewParentItem parentItem = date_list.get(position);
        holder.ParentItemTitle.setText(parentItem.getParentDateHeader());
        GridLayoutManager layoutManager = new GridLayoutManager(holder.ChildRecyclerView.getContext(),3);
        layoutManager.setInitialPrefetchItemCount(parentItem.getMixedItemList().size());
        NewChildItemAdapter childItemAdapter = new NewChildItemAdapter(mContext, parentItem.getMixedItemList());

        holder.ChildRecyclerView.setLayoutManager(layoutManager);
        holder.ChildRecyclerView.setAdapter(childItemAdapter);
        holder.ChildRecyclerView.setRecycledViewPool(viewPool);
        holder.ChildRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Section " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                System.out.println("Section " + String.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return date_list.size();
    }

    class NewParentViewHolder extends RecyclerView.ViewHolder {
        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;

        NewParentViewHolder(final View itemView) {
            super(itemView);
            ParentItemTitle = itemView.findViewById(R.id.parent_date_stamp);
            ChildRecyclerView = itemView.findViewById( R.id.child_recyclerview);
        }
    }


}