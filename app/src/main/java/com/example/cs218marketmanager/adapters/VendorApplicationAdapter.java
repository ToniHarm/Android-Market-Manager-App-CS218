package com.example.cs218marketmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cs218marketmanager.R;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.data.model.VendorApplication;

import java.util.List;

public class VendorApplicationAdapter extends RecyclerView.Adapter<VendorApplicationAdapter.ViewHolder> {

    private List<VendorApplication> applications;
    private Context context;

    public VendorApplicationAdapter(List<VendorApplication> applications, Context context) {
        this.applications = applications;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUsername, textViewVendorName, textViewEmail, textViewProductType;
        public Button buttonApprove, buttonReject;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewVendorName = view.findViewById(R.id.textViewVendorName);
            textViewEmail = view.findViewById(R.id.textViewEmail);
            textViewProductType = view.findViewById(R.id.textViewProductType);
            buttonApprove = view.findViewById(R.id.buttonApprove);
            buttonReject = view.findViewById(R.id.buttonReject);
        }
    }

    @Override
    public VendorApplicationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vendor_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VendorApplication application = applications.get(position);
        User user = application.getUser();

        holder.textViewUsername.setText("Username " + user.getUsername());
        holder.textViewVendorName.setText("Vendor: " + user.getFirstName() + " " + user.getLastName());
        holder.textViewEmail.setText("Email: " + user.getEmail());

        StringBuilder productListBuilder = new StringBuilder();
        for (String product : application.getProducts()) {
            productListBuilder.append(product).append(", ");
        }
        if (productListBuilder.length() > 0) {
            productListBuilder.setLength(productListBuilder.length() - 2); // Remove trailing comma
        }
        holder.textViewProductType.setText("Product Type: " + productListBuilder.toString());

        // Handle Approve/Reject buttons
        holder.buttonApprove.setOnClickListener(v -> {
            // Update the application status to 'Approved'
            application.setStatus("APPROVED");

            // Update the database
            updateApplicationStatus(application, "APPROVED");

            // Notify the user (optional)
            notifyVendor(user, "Your application has been approved!");

            // Refresh the item view (optional: remove or disable buttons, update UI)
            notifyItemChanged(position);
        });

        holder.buttonReject.setOnClickListener(v -> {
            // Update the application status to 'Rejected'
            application.setStatus("REJECTED");

            // Update the database
            updateApplicationStatus(application, "REJECTED");

            // Notify the user (optional)
            notifyVendor(user, "Your application has been rejected.");

            // Refresh the item view (optional: remove or disable buttons, update UI)
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void updateApplications(List<VendorApplication> newApplications) {
        applications.clear();
        applications.addAll(newApplications);
        notifyDataSetChanged();
    }

    private void updateApplicationStatus(VendorApplication application, String status) {
        // Get the database helper and update the application status
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        boolean isUpdated = databaseHelper.updateApplicationStatus(application.getId(), status);

        if (isUpdated) {
            Toast.makeText(context, "Application " + status, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyVendor(User user, String message) {
        // This is a placeholder. You could send an email, push notification, etc.
        Toast.makeText(context, user.getUsername() + ": " + message, Toast.LENGTH_SHORT).show();
    }




}
