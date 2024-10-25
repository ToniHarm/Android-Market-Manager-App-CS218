package com.example.cs218marketmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;

public class ManagerAddPayment extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bnv;

    private Spinner spinnerStallNumber;
    private EditText editTextPaymentAmount, editTextPaymentDate, editTextPaymentDescription, editTextFineAmount;
    private Button btnSubmitPayment, btnSubmitFine;

    private Long vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_addpayment);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        spinnerStallNumber = findViewById(R.id.spinnerStallNumber);
        editTextPaymentAmount = findViewById(R.id.editTextAddPayment);
        editTextPaymentDate = findViewById(R.id.editTextPaymentDate);
        editTextPaymentDescription = findViewById(R.id.editTextPaymentDescription);
        editTextFineAmount = findViewById(R.id.editTextAddFine);
        btnSubmitPayment = findViewById(R.id.buttonSubmitPayment);
        btnSubmitFine = findViewById(R.id.buttonSubmitFine);

        // Populate Spinner with Stall Numbers
        List<String> stallNumbers = databaseHelper.getAllStallNumbers();
        if (stallNumbers.isEmpty()) {
            // Show message if there are no stalls
            Toast.makeText(this, "No stalls available.", Toast.LENGTH_SHORT).show();
            // Optionally, disable the spinner
            spinnerStallNumber.setEnabled(false);
            // Optionally, set a default item (like a placeholder)
            ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"No stalls available"});
            emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStallNumber.setAdapter(emptyAdapter);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stallNumbers);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStallNumber.setAdapter(adapter);
        }

        spinnerStallNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem = spinnerStallNumber.getSelectedItem();

                if (selectedItem != null && !selectedItem.toString().equals("No stalls available")) {
                    String selectedStall = selectedItem.toString();
                    // Fetch the balance due for the selected vendor
                    double balanceDue = databaseHelper.getBalanceDueByStallNumber(selectedStall);

                    // Fetch the vendor ID for the selected stall
                    vendorId = databaseHelper.getVendorIdByStallNumber(selectedStall);

                    // Update the TextView to show the balance due
                    TextView textViewBalanceDue = findViewById(R.id.textBalancedue);
                    textViewBalanceDue.setText("Balance Due: $" + balanceDue);
                } else {
                    // If no stall is selected, show a default message
                    Toast.makeText(ManagerAddPayment.this, "Please select a stall", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional, but good for UX)
                TextView textViewBalanceDue = findViewById(R.id.textBalancedue);
                textViewBalanceDue.setText("Balance Due: N/A");
                Toast.makeText(ManagerAddPayment.this, "No stall selected. Please choose one.", Toast.LENGTH_SHORT).show();
            }
        });

        // DatePicker dialog
        editTextPaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ManagerAddPayment.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Format the date as MM/DD/YYYY and set it in the EditText
                                String formattedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                                editTextPaymentDate.setText(formattedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });


        // Handle Submit Payment Button Click
        btnSubmitPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedStall = spinnerStallNumber.getSelectedItem().toString();
                String paymentAmountStr = editTextPaymentAmount.getText().toString();
                String paymentDate = editTextPaymentDate.getText().toString();
                String paymentDescription = editTextPaymentDescription.getText().toString();

                // Check if stall is selected
                if (selectedStall == null || selectedStall.isEmpty()) {
                    Toast.makeText(ManagerAddPayment.this, "Please select a stall", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if required fields are filled
                if (!paymentAmountStr.isEmpty() && !paymentDate.isEmpty()) {
                    // Parse payment amount to double
                    double paymentAmount;
                    try {
                        paymentAmount = Double.parseDouble(paymentAmountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ManagerAddPayment.this, "Invalid payment amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add payment for vendor
                    boolean success = databaseHelper.addPaymentForVendor(selectedStall, paymentAmount, paymentDate, paymentDescription);
                    if (success) {
                        databaseHelper.createNotification(vendorId, "A payment of $" + paymentAmount + " has been received for stall " + selectedStall + ".");
                        Toast.makeText(ManagerAddPayment.this, "Payment added successfully", Toast.LENGTH_SHORT).show();
                        editTextPaymentAmount.setText("");
                        editTextPaymentDate.setText("");
                        editTextPaymentDescription.setText("");
                        recreate();
                    } else {
                        Toast.makeText(ManagerAddPayment.this, "Failed to add payment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManagerAddPayment.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle Submit Fine Button Click
        btnSubmitFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedStall = spinnerStallNumber.getSelectedItem().toString();
                String fineAmountStr = editTextFineAmount.getText().toString();

                // Check if stall is selected
                if (selectedStall == null || selectedStall.isEmpty()) {
                    Toast.makeText(ManagerAddPayment.this, "Please select a stall", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if fine amount is filled
                if (!fineAmountStr.isEmpty()) {
                    // Parse fine amount to double
                    double fineAmount;
                    try {
                        fineAmount = Double.parseDouble(fineAmountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ManagerAddPayment.this, "Invalid fine amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add fine for vendor
                    boolean success = databaseHelper.addFineForVendor(selectedStall, fineAmount);
                    if (success) {
                        databaseHelper.createNotification(vendorId, "A fine has been issued to you.");
                        Toast.makeText(ManagerAddPayment.this, "Fine added successfully", Toast.LENGTH_SHORT).show();
                        editTextFineAmount.setText("");
                        recreate();
                    } else {
                        Toast.makeText(ManagerAddPayment.this, "Failed to add fine", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManagerAddPayment.this, "Please enter fine amount", Toast.LENGTH_SHORT).show();
                }
            }
        });



        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    Intent intent = new Intent(ManagerAddPayment.this, ManagerHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_application) {
                    Intent intent = new Intent(ManagerAddPayment.this, ManagerApprovalActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_payment) {

                    return true;
                } else if (item.getItemId() == R.id.manager_vendors) {
                    Intent intent = new Intent(ManagerAddPayment.this, VendorListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_setting) {
                    Intent intent = new Intent(ManagerAddPayment.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });

    }
}
