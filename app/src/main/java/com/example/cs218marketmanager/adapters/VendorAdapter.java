package com.example.cs218marketmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cs218marketmanager.R;
import com.example.cs218marketmanager.data.model.User;

import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<User> vendorList;

    public VendorAdapter(List<User> vendorList) {
        this.vendorList = vendorList;
    }

    @Override
    public VendorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_manager_viewvendors, parent, false); // Inflate your vendor item layout
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendorViewHolder holder, int position) {
        User vendor = vendorList.get(position);
        holder.usernameTextView.setText(vendor.getUsername());
        holder.emailTextView.setText(vendor.getEmail());
        // Set other vendor properties as needed
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;

        public VendorViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textViewUsername1);
            emailTextView = itemView.findViewById(R.id.textViewEmail1);
        }
    }
}
//not done will finish when full vendor process finished