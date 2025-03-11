package com.developerali.bongbilling.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.developerali.bongbilling.Adapters.ReceiptAdapter;
import com.developerali.bongbilling.Helpers.ApiService;
import com.developerali.bongbilling.Helpers.Helpers;
import com.developerali.bongbilling.Helpers.RetrofitClient;
import com.developerali.bongbilling.Models.InvoiceDetails;
import com.developerali.bongbilling.R;
import com.developerali.bongbilling.databinding.FragmentTomorrowBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TomorrowFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentTomorrowBinding binding;
    int nextTokenKey = 0;
    private boolean isLoading = false;
    private boolean noMoreLoad = false;
    ApiService apiService;
    Activity activity;
    String searchKeyword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTomorrowBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        activity = getActivity();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        searchKeyword = dateFormat.format(tomorrow);

        setupRecyclerView();
        ProcessSearch(searchKeyword, 0);


        return binding.getRoot();
    }

    private void ProcessSearch(String string, int i) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<InvoiceDetails> call = apiService.searchOrFetchInvoices(
                string, "/"+ Helpers.getTextFromSharedPref(getActivity(), "code")+"/", i
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
                        binding.noData.setVisibility(View.GONE);

                        nextTokenKey = Integer.parseInt(apiResponse.getNextPageToken());
                        if (apiResponse.getData().size() < 15){
                            noMoreLoad = true;
                        }

                        isLoading = false;
                    }else {
                        binding.noData.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InvoiceDetails> call, Throwable t) {
                binding.noData.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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