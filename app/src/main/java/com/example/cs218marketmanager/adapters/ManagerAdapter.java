package com.example.cs218marketmanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs218marketmanager.AdminHomeActivity;
import com.example.cs218marketmanager.R;
import com.example.cs218marketmanager.data.DatabaseHelper;

import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {
    private List<Manager> managerList;
    private DatabaseHelper databaseHelper;
    private Context context; // Add context to access DatabaseHelper

    public ManagerAdapter(List<Manager> managerList, AdminHomeActivity adminHomeActivity) {
        this.managerList = managerList;
        this.context = adminHomeActivity;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager, parent, false);
        return new ManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {
        Manager manager = managerList.get(position);
        holder.textViewManagerName.setText(manager.getName());
        holder.textViewEmail.setText(manager.getEmail());

        // Handle Deregister button click
        holder.deregisterButton .setOnClickListener(v -> {
            // Log the email we are trying to deregister
            Log.d("ManagerAdapter", "Deregister button clicked for manager: " + manager.getEmail());

            // Check if the email is not null or empty before proceeding
            if (manager.getEmail() == null || manager.getEmail().isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "Error: Email is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try to delete the manager by email from the database
            boolean isDeleted;
            try {
                isDeleted = databaseHelper.deleteManagerById(manager.getEmail()); // Call deleteManagerById
                Log.d("ManagerAdapter", "Manager deletion success: " + isDeleted);
            } catch (Exception e) {
                Log.e("ManagerAdapter", "Error deleting manager", e);
                Toast.makeText(holder.itemView.getContext(), "Error deregistering manager", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isDeleted) {
                // If deletion was successful, remove the manager from the list and update the UI
                managerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, managerList.size());
                Toast.makeText(holder.itemView.getContext(), "Manager deregistered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Error deregistering manager", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return managerList.size();
    }

    public static class ManagerViewHolder extends RecyclerView.ViewHolder {
        public View deregisterButton;
        TextView textViewManagerName;
//        TextView textViewPhoneNumber;
//        TextView textViewDob;
        TextView textViewEmail;

        public ManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewManagerName = itemView.findViewById(R.id.textViewManagerName);
//            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
//            textViewDob = itemView.findViewById(R.id.textViewDob);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            deregisterButton = itemView.findViewById(R.id.buttonDeregister);
        }
    }
}
