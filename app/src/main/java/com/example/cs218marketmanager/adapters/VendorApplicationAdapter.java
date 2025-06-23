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

import java.util.ArrayList;
import java.util.List;

public class VendorApplicationAdapter extends RecyclerView.Adapter<VendorApplicationAdapter.ViewHolder> {

    private List<VendorApplication> applications;
    private Context context;

    public VendorApplicationAdapter(List<VendorApplication> applications, Context context) {
        // Filter the applications to only include those that are pending
        this.applications = filterPendingApplications(applications);
        this.context = context;
    }

    private List<VendorApplication> filterPendingApplications(List<VendorApplication> applications) {
        List<VendorApplication> pendingApplications = new ArrayList<>();
        for (VendorApplication application : applications) {
            // Include only applications that are PENDING or have no status
            if (application.getStatus() == null || application.getStatus().equals("PENDING")) {
                pendingApplications.add(application);
            }
        }
        return pendingApplications;
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

        holder.textViewUsername.setText("Username: " + user.getUsername());
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

            // Save user details and product details, set stall number to null in the vendor table
            saveApprovedVendor(application);

            // Notify the user (optional)
            notifyVendor(user, "Your application has been approved!");

            // Remove the application from the list
            applications.remove(position);
            notifyItemRemoved(position); // Notify adapter about the item removal
            notifyItemRangeChanged(position, applications.size()); // Update remaining items
        });

        holder.buttonReject.setOnClickListener(v -> {
            // Update the application status to 'Rejected'
            application.setStatus("REJECTED");

            // Update the database
            updateApplicationStatus(application, "REJECTED");

            // Notify the user (optional)
            notifyVendor(user, "Your application has been rejected.");

            // Remove the application from the list
            applications.remove(position);
            notifyItemRemoved(position); // Notify adapter about the item removal
            notifyItemRangeChanged(position, applications.size()); // Update remaining items
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void updateApplications(List<VendorApplication> newApplications) {
        // Update the applications with the new list, filtered for pending statuses
        applications.clear();
        applications.addAll(filterPendingApplications(newApplications));
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

    private void saveApprovedVendor(VendorApplication application) {
        // Get the database helper
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        // Save user details and product details in the vendor table
        User user = application.getUser();
        boolean isSaved = databaseHelper.saveVendorDetails(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), application.getProducts());

        // Set stall number to null
        boolean isStallNullified = databaseHelper.setStallNumberToNull(user.getId());

        if (isSaved && isStallNullified) {
            Toast.makeText(context, "Vendor details saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to save vendor details.", Toast.LENGTH_SHORT).show();
        }

        // Update the application status to 'APPROVED'
        updateApplicationStatus(application, "APPROVED");
    }
}
