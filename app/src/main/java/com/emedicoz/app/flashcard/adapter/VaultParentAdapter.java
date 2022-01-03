package com.emedicoz.app.flashcard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.model.MainDeck;
import com.emedicoz.app.utilso.EqualSpacingItemDecoration;

import java.util.List;

public class VaultParentAdapter extends RecyclerView.Adapter<VaultParentAdapter.VaultParentHolder> {

    Context context;
    List<MainDeck> mainDeckArrayList;

    public VaultParentAdapter(Context context, List<MainDeck> mainDeckArrayList) {
        this.context = context;
        this.mainDeckArrayList = mainDeckArrayList;
    }

    @NonNull
    @Override
    public VaultParentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.vault_parent_adapter_item, viewGroup, false);
        return new VaultParentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultParentHolder vaultParentHolder, int i) {
        MainDeck mainDeck = mainDeckArrayList.get(i);
        vaultParentHolder.tvTitle.setText(mainDeck.getTitle());

        vaultParentHolder.rvVaultChild.setAdapter(new VaultChildAdapter(context,mainDeck.getSubdeck()));
    }

    @Override
    public int getItemCount() {
        return null != mainDeckArrayList ? mainDeckArrayList.size() : 0;
    }

    class VaultParentHolder extends RecyclerView.ViewHolder {

        RecyclerView rvVaultChild;
        TextView tvTitle;

        public VaultParentHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            rvVaultChild = itemView.findViewById(R.id.rv_vault_child);
            rvVaultChild.setLayoutManager(new LinearLayoutManager(context));
            rvVaultChild.addItemDecoration(new EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.VERTICAL));
        }
    }
}
