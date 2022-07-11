package com.vudrag.prefablok;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PeerRecyclerAdapter extends RecyclerView.Adapter<PeerRecyclerAdapter.ViewHolder> {

    List<String> names;
    OnPeerClickListener onPeerClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        OnPeerClickListener onPeerClickListener;

        public ViewHolder(@NonNull View itemView, OnPeerClickListener onPeerClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.peer_name);
            itemView.setOnClickListener(this);
            this.onPeerClickListener = onPeerClickListener;
        }

        @Override
        public void onClick(View view) {
            onPeerClickListener.onClick(getAdapterPosition());
        }
    }

    public PeerRecyclerAdapter(List<String> names, OnPeerClickListener onPeerClickListener) {
        this.names = names;
        this.onPeerClickListener = onPeerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_peer_recycler, parent, false);
        return new ViewHolder(view, this.onPeerClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public void setList(List<String> names){
        this.names = names;
        notifyDataSetChanged();
    }

    public interface OnPeerClickListener {
        void onClick(int position);
    }
}
