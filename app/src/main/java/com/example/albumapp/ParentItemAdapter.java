package com.example.albumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ParentItemAdapter extends RecyclerView.Adapter<ParentItemAdapter.ParentViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ParentItem> date_list;
    private Context mContext;

    ParentItemAdapter(Context context, List<ParentItem> date_list) {
        this.mContext = context;
        this.date_list = date_list;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.date_item_parent, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, int position) {
        ParentItem parentItem = date_list.get(position);
        parentViewHolder.ParentItemTitle.setText(parentItem.getParentDateHeader());
        GridLayoutManager layoutManager = new GridLayoutManager(parentViewHolder.ChildRecyclerView.getContext(),3);
        layoutManager.setInitialPrefetchItemCount( parentItem.getChildItemList().size());
        ChildItemAdapter childItemAdapter = new ChildItemAdapter(mContext, parentItem.getChildItemList());
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return date_list.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView) {
            super(itemView);
            ParentItemTitle = itemView.findViewById(R.id.parent_date_stamp);
            ChildRecyclerView = itemView.findViewById( R.id.child_recyclerview);
        }
    }
}