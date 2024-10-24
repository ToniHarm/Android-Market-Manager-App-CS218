package com.example.cs218marketmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cs218marketmanager.R;
import com.example.cs218marketmanager.data.model.Vendor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private Context context;
    private List<Vendor> vendorList;

    // Constructor for the adapter
    public VendorAdapter(Context context, List<Vendor> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    // ViewHolder class to represent each item in the RecyclerView
    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView, fullNameTextView, emailTextView, productTypeTextView, stallNumberTextView, balanceTextView;

        public VendorViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textViewUsername1);
            fullNameTextView = itemView.findViewById(R.id.textViewFullName);
            emailTextView = itemView.findViewById(R.id.textViewEmail1);
            productTypeTextView = itemView.findViewById(R.id.textViewProductType1);
            stallNumberTextView = itemView.findViewById(R.id.textViewStallNumber);
            balanceTextView = itemView.findViewById(R.id.textViewBalance);
        }
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_list, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        // Get the current vendor
        Vendor vendor = vendorList.get(position);

        // Bind vendor data to the views
        holder.usernameTextView.setText("Username: " + vendor.getUsername());
        holder.fullNameTextView.setText("Vendor: " + vendor.getFirstName() + " " + vendor.getLastName());
        holder.emailTextView.setText("Email: " + vendor.getEmail());

        // Concatenate product list
        List<String> products = vendor.getProducts();
        if (products != null && !products.isEmpty()) {
            holder.productTypeTextView.setText("Product Type: " + String.join(", ", products));
        } else {
            holder.productTypeTextView.setText("Product Type: N/A");
        }

        holder.stallNumberTextView.setText("Stall Number: " + vendor.getStallNumber());
        holder.balanceTextView.setText("Balance: $" + vendor.getBalance());
    }

    @Override
    public int getItemCount() {
        return vendorList != null ? vendorList.size() : 0;
    }
}
