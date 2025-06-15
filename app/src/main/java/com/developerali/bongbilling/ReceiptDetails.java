package com.developerali.bongbilling;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.developerali.bongbilling.Adapters.DetailsAdapter;
import com.developerali.bongbilling.Helpers.ApiService;
import com.developerali.bongbilling.Helpers.Helpers;
import com.developerali.bongbilling.Helpers.RetrofitClient;
import com.developerali.bongbilling.Helpers.SaveLayoutToPdf;
import com.developerali.bongbilling.Models.ApiResponse;
import com.developerali.bongbilling.Models.CompanyModel;
import com.developerali.bongbilling.Models.DetailItem;
import com.developerali.bongbilling.Models.DetailsModel;
import com.developerali.bongbilling.Models.InvoiceData;
import com.developerali.bongbilling.databinding.ActivityReceiptDetailsBinding;
import com.developerali.bongbilling.databinding.BottomAdditionalBinding;
import com.developerali.bongbilling.databinding.BottomSheetEntryBinding;
import com.developerali.bongbilling.databinding.DialogGuestBinding;
import com.developerali.bongbilling.databinding.DialogMenuBinding;
import com.developerali.bongbilling.databinding.DialogRulesEditBinding;
import com.developerali.bongbilling.databinding.DialogTextInputBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptDetails extends AppCompatActivity{

    ActivityReceiptDetailsBinding binding;
    InvoiceData invoiceData;
    ArrayList<CompanyModel> arrayList = new ArrayList<>();
    ArrayList<DetailsModel> detailsArray;
    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
    ProgressDialog progressDialog;
    int changeCount = 0;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int PDF_PERMISSION_CODE = 150;
    private static final int CREATE_PDF_REQUEST_CODE = 1001;
    SaveLayoutToPdf saveLayoutToPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        invoiceData = getIntent().getParcelableExtra("model");

        progressDialog = new ProgressDialog(ReceiptDetails.this);
        progressDialog.setMessage("connecting server...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        LinearLayoutManager lnm = new LinearLayoutManager(ReceiptDetails.this);
        binding.recyclerView.setLayoutManager(lnm);
        if (invoiceData != null) {
            String details = invoiceData.getDetails();

            // Normalize the JSON string
            if (details != null) {
                // Remove extra escape characters and outer quotes
                details = details.replace("\\\"", "\""); // Replace escaped quotes with regular quotes
                if (details.startsWith("\"") && details.endsWith("\"")) {
                    details = details.substring(1, details.length() - 1); // Remove outer quotes
                }
            }

            // Convert JSON string to ArrayList using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DetailsModel>>() {}.getType();
            try {
                detailsArray = gson.fromJson(details, listType);
                DetailsAdapter adapter = new DetailsAdapter(detailsArray, ReceiptDetails.this);
                binding.recyclerView.setAdapter(adapter);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Toast.makeText(this, "Failed to parse details", Toast.LENGTH_SHORT).show();
            }
        }


        binding.invoiceNo.setText(invoiceData.getInvoiceNo());
        binding.today.setText(Helpers.convertDateFormat(invoiceData.getCreated_at()));

        binding.name.setText("Name : " + invoiceData.getgName());
        binding.address.setText("Address : " + invoiceData.getgAddress());
        binding.phone.setText("Phone : " + invoiceData.getgName());
        binding.journeyDate.setText(invoiceData.getjDate());

        binding.rules1.setText(invoiceData.getRules1());
        binding.rules2.setText(invoiceData.getRules2());


        binding.totalAmount.setText(String.valueOf((int) invoiceData.getTotal()));
        binding.subTotalAmount.setText(String.valueOf((int) invoiceData.getSubTotal()));
        binding.dueAmount.setText(String.valueOf((int) invoiceData.getDue()));
        binding.advanceAmount.setText(String.valueOf((int) invoiceData.getAdvance()));

        if (invoiceData.getDiscount() > 0){
            binding.linearLayout18.setVisibility(View.VISIBLE);
            binding.discountText.setText(String.valueOf((int) invoiceData.getDiscount()));
        }else {
            binding.linearLayout18.setVisibility(View.GONE);
        }

        arrayList.clear();
        arrayList.add(new CompanyModel("Kolakham Silent Valley Homestay ",
                "Lingsaykha Khasmahal, West Bengal 734319", "+91 96223 18518", "KOL",
                ContextCompat.getDrawable(this, R.drawable.kolakham),
                ContextCompat.getDrawable(this, R.drawable.kolakham_up),
                ContextCompat.getDrawable(this, R.drawable.kolakham_side),
                ContextCompat.getColor(this, R.color.back_green_light)
        ));
        arrayList.add(new CompanyModel("Charkhole Dekiling Homestay ",
                "Charkhole, Lolegaon, West Bengal 734301", "+91 70019 69592", "CHAR",
                ContextCompat.getDrawable(this, R.drawable.charkholea),
                ContextCompat.getDrawable(this, R.drawable.char_up),
                ContextCompat.getDrawable(this, R.drawable.char_side),
                ContextCompat.getColor(this, R.color.light_red)
        ));
        arrayList.add(new CompanyModel("Sittong Silent Valley Resort  ",
                "Upper Sittong, Toryak, Darjeeling, WB 734224", "+91 70014 94158", "SIT",
                ContextCompat.getDrawable(this, R.drawable.sittong),
                ContextCompat.getDrawable(this, R.drawable.img_up),
                ContextCompat.getDrawable(this, R.drawable.img_side),
                ContextCompat.getColor(this, R.color.Yellow)
        ));
        arrayList.add(new CompanyModel("Kaffergaon Sailung Valley Homestay ",
                "Kaffergaon, Lolegaon, West Bengal", "+91 91037 99455", "KAFF",
                ContextCompat.getDrawable(this, R.drawable.kaffergaon11),
                ContextCompat.getDrawable(this, R.drawable.kaffer_up),
                ContextCompat.getDrawable(this, R.drawable.kaffer_side),
                ContextCompat.getColor(this, R.color.light_colour_orange)
        ));
        arrayList.add(new CompanyModel("Pedong Asmika Homestay ",
                "Kashyone GP, Pedong, West Bengal - 734311", "+91 70014 94158", "PED",
                ContextCompat.getDrawable(this, R.drawable.peddong_img),
                ContextCompat.getDrawable(this, R.drawable.peddong_back),
                ContextCompat.getDrawable(this, R.drawable.peddong_side),
                ContextCompat.getColor(this, R.color.light_real_purple)
        ));
        arrayList.add(new CompanyModel("Cash Receipt ",
                "We Are Happy to Serve You", "+91 70019 69592", "BSTAT",
                null,
                ContextCompat.getDrawable(this, R.drawable.norm_up),
                ContextCompat.getDrawable(this, R.drawable.norm_side),
                ContextCompat.getColor(this, R.color.light_color1_blue)
        ));

        if (invoiceData.getInvoiceNo().contains("SIT")){
            setCompany(2);
        }else if (invoiceData.getInvoiceNo().contains("KOL")){
            setCompany(0);
        }else if (invoiceData.getInvoiceNo().contains("CHAR")){
            setCompany(1);
        }else if (invoiceData.getInvoiceNo().contains("KAFF")){
            setCompany(3);
        }else if (invoiceData.getInvoiceNo().contains("PED")){
            setCompany(4);
        }else {
            setCompany(5);
        }

        binding.back.setOnClickListener(v->{
            if (changeCount > 0){
                showSaveDialog();
            }else {
                onBackPressed();
            }
        });


        binding.bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.reset){
                    Toast.makeText(ReceiptDetails.this, "Can't Reset Old Receipts..!", Toast.LENGTH_LONG).show();
                } else if (item.getItemId() == R.id.extras) {
                    showAdditional();
                } else if (item.getItemId() == R.id.guestInfo) {
                    showGuestDialog();
                }else if (item.getItemId() == R.id.whatsappShare){
                    String name = binding.name.getText().toString();
                    String pdfName = name.isEmpty() ? "" : name;
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            pdfName + "_output.pdf");
                    if (isPdfFileExists(file)) {
                        sharePdfViaAnyApp(file);
                    } else {
                        checkPermissionAndGeneratePdf();
                    }
                }else {
                    showBottomSheet();
                }
                return true;
            }
        });

        binding.rules1.setOnClickListener(v->{
            showRulesBox();
        });

        binding.rules2.setOnClickListener(v->{
            showRulesBox();
        });

        binding.guestDetailsLayout.setOnClickListener(v->{
            showGuestDialog();
        });

        binding.menuOptions.setOnClickListener(v->{
            showMenuOptions();
        });




    }

    void showSaveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?")
                .setMessage("Please save all changes in server.");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAllDataInServer();
            }
        });

        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCount = 0;
                dialog.dismiss();
                onBackPressed();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (changeCount > 0){
            showSaveDialog();
        }else {
            super.onBackPressed();
        }
    }

    private void showMenuOptions() {
        DialogMenuBinding sheetBinding = DialogMenuBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(sheetBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        sheetBinding.closeDialog.setOnClickListener(v->{
            dialog.dismiss();
        });

        sheetBinding.saveImg.setOnClickListener(v->{
            dialog.dismiss();
            checkPermissionAndConvertLayout();
        });

        sheetBinding.savePdf.setOnClickListener(v->{
            dialog.dismiss();
            checkPermissionAndGeneratePdf();
        });

        sheetBinding.saveTxt.setText("Update on Server");
        sheetBinding.saveServer.setOnClickListener(v->{
            dialog.dismiss();
            saveAllDataInServer();
        });

        sheetBinding.applyBtn.setOnClickListener(v->{
            String text = sheetBinding.edText.getText().toString();
            if (text.isEmpty()){
                sheetBinding.edText.setError("*");
            }else {
                changeCount++;
                Helpers.DISCOUNT = Integer.parseInt(text);
                dialog.dismiss();
                checkAndLoadRecyclearView();
            }
        });


        dialog.show();
    }

    private void saveAllDataInServer() {
        // Extracting input data
        String invoice_no = binding.invoiceNo.getText().toString();

        String gName = Helpers.getLastPartAfterSplit(binding.name.getText().toString(), ": ").trim();
        String gAddress = Helpers.getLastPartAfterSplit(binding.address.getText().toString(), ": ").trim();
        String gPhone = Helpers.getLastPartAfterSplit(binding.phone.getText().toString(), ": ").trim();

        String total = binding.totalAmount.getText().toString().trim();
        String jDate = binding.journeyDate.getText().toString();
        String subTotal = binding.subTotalAmount.getText().toString().trim();
        String due = binding.dueAmount.getText().toString().trim();
        String advance = binding.advanceAmount.getText().toString().trim();
        String discount = String.valueOf(Helpers.DISCOUNT);

        String checkIn = Helpers.getLastPartAfterSplit(binding.checkInTime.getText().toString(), "-");
        String checkOut = Helpers.getLastPartAfterSplit(binding.checkOutTime.getText().toString(), "-");

        String rules1 = binding.rules1.getText().toString();
        String rules2 = binding.rules2.getText().toString();

        // Creating Details Array
//        ArrayList<DetailsModel> detailsArray = new ArrayList<>();
//        detailsArray.add(new DetailsModel("Room Charge", "80.00"));
//        detailsArray.add(new DetailsModel("Tax", "10.00"));

        // Convert Details Array to JSON String
        Gson gson = new Gson();
        String detailsJson = gson.toJson(detailsArray);

        // Creating Invoice Data Model
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setInvoiceNo(invoice_no);
        invoiceData.setgName(gName);
        invoiceData.setgAddress(gAddress);
        invoiceData.setjDate(jDate);
        invoiceData.setgPhone(gPhone);
        invoiceData.setTotal(Double.parseDouble(total));
        invoiceData.setSubTotal(Double.parseDouble(subTotal));
        invoiceData.setDue(Double.parseDouble(due));
        invoiceData.setAdvance(Double.parseDouble(advance));
        invoiceData.setDiscount(Double.parseDouble(discount));
        invoiceData.setCheckIn(Helpers.START_DATE);
        invoiceData.setCheckOut(Helpers.END_DATE);
        invoiceData.setRules1(rules1);
        invoiceData.setRules2(rules2);
        invoiceData.setDetails(detailsJson);  // Storing JSON string in details

        // Making API Call
        Call<ApiResponse> call = apiService.updateInvoiceDetails(
                invoiceData);

        progressDialog.show();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    changeCount = 0;
                    Log.d("API_SUCCESS", "Status: " + apiResponse.getStatus());
                    Log.d("API_SUCCESS", "Message: " + apiResponse.getMessage());
                    Toast.makeText(getApplicationContext(), "Invoice Saved Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(getApplicationContext(), "Failed to save invoice: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API_FAILURE", "Failure: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Network Error! Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAdditional() {
        BottomAdditionalBinding sheetBinding = BottomAdditionalBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(sheetBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        sheetBinding.closeDialog.setOnClickListener(v->{
            dialog.dismiss();
        });

        sheetBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (sheetBinding.additionalEntry.isChecked()){
                    sheetBinding.falseLayout.setVisibility(View.GONE);
                    sheetBinding.additionalLayout.setVisibility(View.VISIBLE);
                }else {
                    sheetBinding.falseLayout.setVisibility(View.VISIBLE);
                    sheetBinding.additionalLayout.setVisibility(View.GONE);
                }
            }
        });

        sheetBinding.btnFalseSave.setOnClickListener(v->{
            String des = sheetBinding.nameFalse.getText().toString();
            if (des.isEmpty()){
                sheetBinding.nameFalse.setError("*");
            }else {
                detailsArray.add(new DetailsModel(
                        des, 0, 0, 0, 0, 0, 0
                ));
                checkAndLoadRecyclearView();
                dialog.dismiss();
            }
        });

        sheetBinding.btnSave.setOnClickListener(v->{
            String description = sheetBinding.name.getText().toString();
            String price = sheetBinding.amount.getText().toString();

            if (description.isEmpty()){
                sheetBinding.name.setError("*");
            }else if (price.isEmpty()){
                sheetBinding.amount.setError("*");
            }else {
                changeCount++;
                detailsArray.add(new DetailsModel(
                        description, 0, 0, 0, 0, 0, Integer.parseInt(price)
                ));
                checkAndLoadRecyclearView();
                dialog.dismiss();
            }
        });

        sheetBinding.barbe.setOnClickListener(v->{
            sheetBinding.name.setText("Barbeque");
            sheetBinding.amount.setText(String.valueOf(1050));
        });

        sheetBinding.heater.setOnClickListener(v->{
            sheetBinding.name.setText("Heater Rent");
            sheetBinding.amount.setText(String.valueOf(1050));
        });

        sheetBinding.kattle.setOnClickListener(v->{
            sheetBinding.name.setText("Kettle Rent");
            sheetBinding.amount.setText(String.valueOf(1050));
        });

        dialog.show();
    }

    private void showBottomSheet() {
        BottomSheetEntryBinding sheetBinding = BottomSheetEntryBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(sheetBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        sheetBinding.closeBtn.setOnClickListener(v->{
            dialog.dismiss();
        });


        sheetBinding.btnSave.setOnClickListener(v->{
            String description = sheetBinding.des.getText().toString();
            String price = sheetBinding.amount.getText().toString();
            String adult = sheetBinding.adultNum.getText().toString();
            String Child = sheetBinding.childNum.getText().toString();
            String room = sheetBinding.roomNum.getText().toString();
            String night = sheetBinding.nightdNum.getText().toString();
            String total = sheetBinding.total.getText().toString();

            if (description.isEmpty()){
                sheetBinding.des.setError("*");
            }else if (price.isEmpty()){
                sheetBinding.amount.setError("*");
            }else {
                changeCount++;
                detailsArray.add(new DetailsModel(
                        description + " " + Helpers.DESCRIPTION, Integer.parseInt(price), Integer.parseInt(adult),
                        Integer.parseInt(Child), Integer.parseInt(room),
                        Integer.parseInt(night), Integer.parseInt(total)
                ));
                checkAndLoadRecyclearView();
                dialog.dismiss();
            }
        });

        addFunctionalities(sheetBinding);
        dialog.show();
    }

    private void checkAndLoadRecyclearView() {
        if (!detailsArray.isEmpty()) {
            DetailsAdapter adapter1 = new DetailsAdapter(detailsArray, ReceiptDetails.this);
            binding.recyclerView.setVisibility(View.VISIBLE);

            adapter1.setOnItemLongClickListener(new DetailsAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClicked(int position) {
                    arrayList.remove(position);
                    adapter1.notifyItemRemoved(position);
                    // Update the UI
                    checkAndLoadRecyclearView();
                }
            });

            binding.recyclerView.setAdapter(adapter1);
            int total = 0;
            for (int i = 0; i < detailsArray.size(); i++) {
                total = total + detailsArray.get(i).getTotal();
            }
            int advance = 0;
            if (!binding.advanceAmount.getText().toString().isEmpty()) {
                advance = Integer.parseInt(binding.advanceAmount.getText().toString());
            }


            if (Helpers.DISCOUNT > 0){
                binding.linearLayout18.setVisibility(View.VISIBLE);
                binding.discountText.setText(String.valueOf(Helpers.DISCOUNT));
                total = total - Helpers.DISCOUNT;
            }else {
                binding.linearLayout18.setVisibility(View.GONE);
                binding.discountText.setText("0");
            }

            binding.dueAmount.setText(String.valueOf(total - advance));
            binding.subTotalAmount.setText(String.valueOf(total));
            binding.totalAmount.setText(String.valueOf(total));
        }else {
            binding.recyclerView.setVisibility(View.GONE);
        }
    }
    private void addFunctionalities(BottomSheetEntryBinding sheetBinding) {
//        Helpers.setCapitalizedTextWatcher(sheetBinding.des);

        //Minus Buttons
        sheetBinding.minAdult.setOnClickListener(v->{
            Helpers.minusBtn(ReceiptDetails.this, sheetBinding.adultNum);
        });
        sheetBinding.minChild.setOnClickListener(v->{
            Helpers.minusBtn(ReceiptDetails.this, sheetBinding.childNum);
        });
        sheetBinding.minNight.setOnClickListener(v->{
            Helpers.minusBtn(ReceiptDetails.this, sheetBinding.nightdNum);
        });
        sheetBinding.minRoom.setOnClickListener(v->{
            Helpers.minusBtn(ReceiptDetails.this, sheetBinding.roomNum);
        });

        //Plus Buttons
        sheetBinding.maxAdult.setOnClickListener(v->{
            Helpers.plusBtn(ReceiptDetails.this, sheetBinding.adultNum);
        });
        sheetBinding.maxChild.setOnClickListener(v->{
            Helpers.plusBtn(ReceiptDetails.this, sheetBinding.childNum);
        });
        sheetBinding.maxNight.setOnClickListener(v->{
            Helpers.plusBtn(ReceiptDetails.this, sheetBinding.nightdNum);
        });
        sheetBinding.maxRoom.setOnClickListener(v->{
            Helpers.plusBtn(ReceiptDetails.this, sheetBinding.roomNum);
        });

        //TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalAmount(sheetBinding);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        sheetBinding.amount.addTextChangedListener(textWatcher);
        sheetBinding.adultNum.addTextChangedListener(textWatcher);
        sheetBinding.nightdNum.addTextChangedListener(textWatcher);
        sheetBinding.roomNum.addTextChangedListener(textWatcher);

        //Radio Group
        sheetBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                calculateTotalAmount(sheetBinding);
            }
        });
    }
    private void calculateTotalAmount(BottomSheetEntryBinding sheetBinding) {
        String amt = sheetBinding.amount.getText().toString();
        if (amt.isEmpty()){
            return;
        }

        int price = Integer.parseInt(amt);
        int adult = Integer.parseInt(sheetBinding.adultNum.getText().toString());
        int child = Integer.parseInt(sheetBinding.childNum.getText().toString());
        int room = Integer.parseInt(sheetBinding.roomNum.getText().toString());
        int night = Integer.parseInt(sheetBinding.nightdNum.getText().toString());
        if (price == 0){
            //Nothing to do!
        }else {
            int totalAmount;
            if (sheetBinding.headPerNight.isChecked()){
                int halfPrice = (int)(price/2);
                totalAmount = (int) (price * adult * night) + (int) (halfPrice * child * night);
                Helpers.DESCRIPTION = "(For Adult: " + price + " x " + adult + " x " + night + ") " +
                        "+ (For Child: "+ halfPrice + " x " + child + " x " + night + ")";
            }else {
                totalAmount = (int) (price * room * night);
                Helpers.DESCRIPTION = "(" + price + " x " + room + " x " + night + ")";
            }
            sheetBinding.total.setText(String.valueOf(totalAmount));
        }
    }

    private void showGuestDialog() {
        DialogGuestBinding des = DialogGuestBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(des.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

//        Helpers.setCapitalizedTextWatcher(des.name);
//        Helpers.setCapitalizedTextWatcher(des.address);

        String name1 = binding.name.getText().toString();
        String phone1 = binding.phone.getText().toString();
        String journey1 = binding.journeyDate.getText().toString();
        String amount1 = binding.advanceAmount.getText().toString();
        String address1 = binding.address.getText().toString();

        String dbName = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cus_name");
        String dbPhone = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cus_phone");
        String dbAddress = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cus_address");
        String dbAmount = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cus_amount");
        String dbDate = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cus_date");

        if (!name1.isEmpty()){
            String a = Helpers.getLastPartAfterSplit(name1, ":");
            if (a.length() > 3){
                des.name.setText(a);
            }else if (dbName != null) {
                des.name.setText(dbName);
            }
        }
        if (!journey1.isEmpty()){
            des.date.setText(journey1);
        } else if (dbDate != null) {
            des.date.setText(dbDate);
        }
        if (!address1.isEmpty()){
            String a = Helpers.getLastPartAfterSplit(address1, ":");
            if (a.length() > 3){
                des.address.setText(a);
            }else if (dbAddress != null) {
                des.address.setText(dbAddress);
            }
        }
        if (!phone1.isEmpty()) {
            String a = Helpers.getLastPartAfterSplit(phone1, ":");
            if (a.length() > 3){
                des.phone.setText(a);
            }else if (dbName != null) {
                des.phone.setText(dbPhone);
            }
        }
        if (!amount1.isEmpty() && !amount1.equalsIgnoreCase("0")) {
            des.advanceAmount.setText(amount1);
        }else if (dbAmount != null) {
            des.advanceAmount.setText(dbAmount);
        }


        des.btnChange.setOnClickListener(v->{
            String name = des.name.getText().toString();
            String phone = des.phone.getText().toString();
            String amount = des.advanceAmount.getText().toString();
            String address = des.address.getText().toString();
            String date = des.date.getText().toString();

            if (date.isEmpty()){
                des.date.setError("*");
            }else if (name.isEmpty()){
                des.name.setError("*");
            } else if (address.isEmpty()){
                des.address.setError("*");
            }else if (phone.isEmpty()) {
                des.phone.setError("*");
            }else if (amount.isEmpty()) {
                des.advanceAmount.setError("*");
            }else {

                Helpers.saveTextToSharedPref(ReceiptDetails.this, "cus_name", name);
                Helpers.saveTextToSharedPref(ReceiptDetails.this, "cus_phone", phone);
                Helpers.saveTextToSharedPref(ReceiptDetails.this, "cus_amount", amount);
                Helpers.saveTextToSharedPref(ReceiptDetails.this, "cus_address", address);
                Helpers.saveTextToSharedPref(ReceiptDetails.this, "cus_date", date);

                binding.name.setText(Html.fromHtml("<b>Name : </b>" + Helpers.capitalizeSentences(name)));
                binding.advanceAmount.setText(amount);
                binding.phone.setText(Html.fromHtml("<b>Phone : </b>" + phone));
                binding.address.setText(Html.fromHtml("<b>Address : </b>" + Helpers.capitalizeSentences(address)));
                binding.journeyDate.setText(date);
                changeCount++;
                checkAndLoadRecyclearView();
                dialog.dismiss();
            }
        });

        des.openCalender.setOnClickListener(v->{
            MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Journey Date Range")
                    .setCalendarConstraints(
                            new CalendarConstraints.Builder()
                                    .setValidator(DateValidatorPointForward.now()) // Only allow future dates
                                    .build()
                    )
                    .build();

            dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

            dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    // Convert selected timestamps to formatted date
                    SimpleDateFormat first = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat second = new SimpleDateFormat("dd LLL, yyyy", Locale.getDefault());
                    SimpleDateFormat sqlDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    String startDate = first.format(selection.first);
                    String endDate = second.format(selection.second);
                    Helpers.START_DATE = sqlDate.format(selection.first);
                    Helpers.END_DATE = sqlDate.format(selection.second);

                    // Display selected date range
                    des.date.setText(startDate + " to " + endDate);
                }
            });
        });

        des.closeDialog.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showRulesBox() {
        DialogRulesEditBinding sheetBinding = DialogRulesEditBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(sheetBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        sheetBinding.closeDialog.setOnClickListener(v->{
            dialog.dismiss();
        });

        if (Helpers.getTextFromSharedPref(ReceiptDetails.this, "rules1") != null){
            sheetBinding.fRules.setText(Helpers.getTextFromSharedPref(ReceiptDetails.this, "rules1"));
        }else {
            sheetBinding.fRules.setText(binding.rules1.getText().toString());
        }

        if (Helpers.getTextFromSharedPref(ReceiptDetails.this, "rules2") != null){
            sheetBinding.sRules.setText(Helpers.getTextFromSharedPref(ReceiptDetails.this, "rules2"));
        }else {
            sheetBinding.sRules.setText(binding.rules2.getText().toString());
        }

        if (Helpers.getTextFromSharedPref(ReceiptDetails.this, "check_in") != null){
            sheetBinding.checkIn.setText(Helpers.getTextFromSharedPref(ReceiptDetails.this, "check_in"));
        }else {
            sheetBinding.checkIn.setText("12:00 PM");
        }

        if (Helpers.getTextFromSharedPref(ReceiptDetails.this, "check_out") != null){
            sheetBinding.checkOut.setText(Helpers.getTextFromSharedPref(ReceiptDetails.this, "check_out"));
        }else {
            sheetBinding.checkOut.setText("11:00 AM");
        }

        sheetBinding.btnSave.setOnClickListener(v->{
            String fRules = sheetBinding.fRules.getText().toString();
            String sRules = sheetBinding.sRules.getText().toString();
            String checkIn = sheetBinding.checkIn.getText().toString();
            String checkOut = sheetBinding.checkOut.getText().toString();

            Helpers.saveTextToSharedPref(ReceiptDetails.this, "rules1", fRules);
            Helpers.saveTextToSharedPref(ReceiptDetails.this, "rules2", sRules);
            Helpers.saveTextToSharedPref(ReceiptDetails.this, "check_in", checkIn);
            Helpers.saveTextToSharedPref(ReceiptDetails.this, "check_out", checkOut);

            binding.rules1.setText(fRules);
            binding.rules2.setText(sRules);
            binding.checkInTime.setText("Check In - " + checkIn);
            binding.checkOutTime.setText("Check Out - " + checkOut);

            dialog.dismiss();
        });


        dialog.show();
    }

    void setCompany(int position){
        CompanyModel selectedCompany = arrayList.get(position);
        setColors(selectedCompany.getColor());
        binding.companyName.setText(selectedCompany.getName());
        binding.coAddress.setText(selectedCompany.getAddress());
        binding.coPhone.setText(Html.fromHtml("<b>Contact:</b> " + selectedCompany.getContact()));

        if (selectedCompany.getCode().equalsIgnoreCase("PED")
                || selectedCompany.getCode().equalsIgnoreCase("KOL")
                || selectedCompany.getCode().equalsIgnoreCase("BSTAT")){
            binding.coAddress.setTextColor(getColor(R.color.black));
        }else {
            binding.coAddress.setTextColor(getColor(R.color.red));
        }

        if (selectedCompany.getCode().equalsIgnoreCase("BSTAT")){
            String coName = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cash_receipt_name");
            String coPhone = Helpers.getTextFromSharedPref(ReceiptDetails.this, "cash_receipt_phone");
            if (coName != null){
                binding.companyName.setText(coName);
                binding.companyName2.setText(coName);
            }else {
                binding.companyName.setText(selectedCompany.getName());
                binding.companyName2.setText(selectedCompany.getName());
            }

            if (coPhone != null){
                binding.coPhone.setText(Html.fromHtml("<b>Contact:</b> " + coPhone));
            }else {
                binding.coPhone.setText(Html.fromHtml("<b>Contact:</b> " + selectedCompany.getContact()));
            }



            binding.companyName.setOnClickListener(v->{
                editDialog("cash_receipt_name", binding.companyName.getText().toString());
            });

            binding.companyName2.setOnClickListener(v->{
                binding.companyName.performClick();
            });

            binding.coPhone.setOnClickListener(v->{
                editDialog("cash_receipt_phone", binding.coPhone.getText().toString());
            });
        }

        if (selectedCompany.getImg() == null) {
            binding.coImg.setVisibility(View.GONE);
        } else {
            binding.coImg.setVisibility(View.VISIBLE);
            binding.coImg.setImageDrawable(selectedCompany.getImg());
        }

        if (selectedCompany.getBack_img() == null) {
            binding.imageView.setVisibility(View.GONE);
            binding.imageView2.setVisibility(View.GONE);
        } else {
            binding.imageView.setVisibility(View.VISIBLE);
            binding.imageView2.setVisibility(View.VISIBLE);
            binding.cashFooter.setVisibility(View.GONE);
            binding.imageView.setImageDrawable(selectedCompany.getBack_img());
            binding.imageView2.setImageDrawable(selectedCompany.getBack_img());
        }

        if (selectedCompany.getSide_img() == null) {
            binding.sideView.setVisibility(View.GONE);
        } else {
            binding.sideView.setVisibility(View.VISIBLE);
            binding.sideView.setBackground(selectedCompany.getSide_img());
        }

        if (selectedCompany.getImg() == null) {
            binding.textView.setVisibility(View.GONE);
            binding.textView2.setVisibility(View.GONE);
        } else {
            binding.textView.setVisibility(View.VISIBLE);
            binding.textView2.setVisibility(View.VISIBLE);
        }

        if (selectedCompany.getImg() == null && selectedCompany.getBack_img() != null){
            binding.imageView2.setVisibility(View.GONE);
            binding.cashFooter.setVisibility(View.VISIBLE);
        }
    }
    private void setColors(int color) {
        int resolvedColor;
        try {
            resolvedColor = ContextCompat.getColor(this, color);
        } catch (Resources.NotFoundException e) {
            resolvedColor = color;
        }
        View[] backgrounds = {
                binding.back1, binding.back2, binding.back3, binding.coAddress,
                binding.back4, binding.back5, binding.back6,
                binding.back7, binding.back8, binding.back9
        };
        for (View background : backgrounds) {
            background.setBackgroundColor(resolvedColor);
        }
    }

    private void editDialog(String key, String value) {
        DialogTextInputBinding dialogBinding = DialogTextInputBinding.inflate(getLayoutInflater());
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());
        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();

//        dialogBinding.textInputL.setInputType(InputType.TYPE_CLASS_PHONE);
        dialogBinding.textInputL.setText(value);
        dialogBinding.btn.setOnClickListener(v->{
            String text = dialogBinding.textInputL.getText().toString();
            if (text.isEmpty()){
                dialogBinding.textInputL.setError("*");
            }else {
                Helpers.saveTextToSharedPref(ReceiptDetails.this, key, text);
                if (key.equalsIgnoreCase("cash_receipt_name")){
                    binding.companyName.setText(text);
                    binding.companyName2.setText(text);
                }else {
                    binding.coPhone.setText(Html.fromHtml("<b>Contact:</b> " + text));
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void convertLayoutToImage() {
        // Step 1: Layout ke Bitmap e convert kora
        binding.receiptLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(binding.receiptLayout.getWidth(), binding.receiptLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        binding.receiptLayout.draw(canvas);
        binding.receiptLayout.setDrawingCacheEnabled(false);

        // Step 2: Image save kora
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "layout_image.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // PNG format e save kora
            fos.flush();
            fos.close();
            Toast.makeText(this, "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            // Step 3: Image open kora
            openImage(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPdfFileExists(File file) {
        return file.exists();
    }

    private void sharePdfViaAnyApp(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            // Create a chooser to show all apps that support PDF sharing
            Intent chooser = Intent.createChooser(intent, "Share PDF via");
            startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(this, "No app found to share PDF!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImage(File file) {
        // FileProvider use kore file er URI generate kora
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

        // Intent create kora image file open korar jonno
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // URI permission grant kora
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // History te add na korar jonno

        // Intent start kora
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No image viewer app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionAndConvertLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                convertLayoutToImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                convertLayoutToImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 and above
                    convertLayoutToImage(); // or generatePdf()
                } else {
                    // Android 12 and below
                    convertLayoutToImage(); // or generatePdf()
                }
            } else {
                Toast.makeText(this, "Permission denied! Cannot save image.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PDF_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveLayoutToPdf.savePdfToDownloads();
            } else {
                Toast.makeText(this, "Permission denied! Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePdfUsingSAF() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "output.pdf"); // Default file name
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Log.d("SAF_URI", "Selected URI: " + uri.toString());
                savePdfToUri(uri);
            }
        }
    }

    private void savePdfToUri(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                // Generate the PDF and save it to the outputStream
                saveLayoutToPdf.generatePdf(outputStream, uri); // Pass the URI to open the PDF later
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF.", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkPermissionAndGeneratePdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above: Use SAF
            savePdfUsingSAF();
        } else {
            // Android 9 and below: Request WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveLayoutToPdf.savePdfToDownloads();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PDF_PERMISSION_CODE);
            }
        }
    }


}