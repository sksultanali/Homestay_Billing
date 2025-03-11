package com.developerali.bongbilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.bongbilling.Adapters.ReceiptAdapter;
import com.developerali.bongbilling.Helpers.ApiService;
import com.developerali.bongbilling.Helpers.Helpers;
import com.developerali.bongbilling.Helpers.RetrofitClient;
import com.developerali.bongbilling.Models.InvoiceDetails;
import com.developerali.bongbilling.databinding.ActivitySearchBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    int nextTokenKey = 0;
    private boolean isLoading = false;
    private boolean noMoreLoad = false;
    ApiService apiService;
    Activity activity;
    String searchKeyword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        activity = SearchActivity.this;

        setupRecyclerView();
        ProcessSearch(searchKeyword, 0);

        binding.refreshBtn.setOnClickListener(v->{
            binding.refreshBtn.animate()
                    .rotationBy(360)  // Rotate by 360 degrees
                    .setDuration(500) // Set duration for 500 milliseconds
                    .withEndAction(() -> {
                        // Once the rotation ends, recreate the activity
                        ProcessSearch(searchKeyword, 0);
                    })
                    .start();
        });

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });

        binding.moreDetails.setOnClickListener(v->{
            startActivity(new Intent(SearchActivity.this, MoreDetails.class));
        });

        binding.searchTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    ProcessSearch(charSequence.toString(), 0);
                    searchKeyword = charSequence.toString();
                    binding.closeBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.closeBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.closeBtn.setOnClickListener(v->{
            binding.searchTags.setText("");
            binding.closeBtn.setVisibility(View.GONE);
            searchKeyword = null;
            ProcessSearch(null, 0);
        });



    }

    private void ProcessSearch(String string, int i) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<InvoiceDetails> call = apiService.searchOrFetchInvoices(
                string, "/"+ Helpers.getTextFromSharedPref(SearchActivity.this, "code")+"/", i
        );

        call.enqueue(new Callback<InvoiceDetails>() {
            @Override
            public void onResponse(Call<InvoiceDetails> call, Response<InvoiceDetails> response) {
                if (response.isSuccessful() && response.body() != null){
                    InvoiceDetails apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")){
                        ReceiptAdapter adapter = new ReceiptAdapter(activity, apiResponse.getData());
                        binding.homeStayList.setAdapter(adapter);
                        binding.progressBar.setVisibility(View.GONE);

                        nextTokenKey = Integer.parseInt(apiResponse.getNextPageToken());
                        if (apiResponse.getData().size() < 15){
                            noMoreLoad = true;
                        }

                        isLoading = false;
                    }else {
                        binding.textView1.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InvoiceDetails> call, Throwable t) {
                Toast.makeText(SearchActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.homeStayList.setLayoutManager(layoutManager);

        binding.homeStayList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    // Load more data here
                    if (!noMoreLoad){
                        isLoading = true;
                        binding.progressBar.setVisibility(View.VISIBLE);
                        ProcessSearch(searchKeyword, nextTokenKey);
                    }
                }
            }
        });
    }
}