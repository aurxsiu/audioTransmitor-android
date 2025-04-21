package com.aurxsiu.audiotransmitor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionViewHolder> {

    public interface Listener {
        void onItemClick(OptionData item);

        void onEditClick(OptionData item);
    }

    private final List<OptionData> items;
    private final Listener listener;

    public OptionAdapter(List<OptionData> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        OptionData data = items.get(position);
        holder.textView.setText(data.toString());
        holder.textView.setOnClickListener(v -> listener.onItemClick(data));
        holder.editButton.setOnClickListener(v -> listener.onEditClick(data));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton editButton;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.optionText);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
