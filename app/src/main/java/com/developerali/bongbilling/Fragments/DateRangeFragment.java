package com.developerali.bongbilling.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.developerali.bongbilling.databinding.FragmentDateRangeBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DateRangeFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentDateRangeBinding binding;
    int nextTokenKey = 0;
    private boolean isLoading = false;
    private boolean noMoreLoad = false;
    ApiService apiService;
    Activity activity;
    String startDate, endDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDateRangeBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        activity = getActivity();

        binding.date.setOnClickListener(v->{
            MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Journey Date Range")
                    .setCalendarConstraints(
                            new CalendarConstraints.Builder()
                                    .setValidator(DateValidatorPointForward.now()) // Only allow future dates
                                    .build()
                    )
                    .build();

            dateRangePicker.show(getActivity().getSupportFragmentManager(), "DATE_RANGE_PICKER");

            dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    // Convert selected timestamps to formatted date
                    SimpleDateFormat first = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat second = new SimpleDateFormat("dd LLL, yyyy", Locale.getDefault());

                    SimpleDateFormat sqlDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    String startD = first.format(selection.first);
                    String endD = second.format(selection.second);

                    startDate = sqlDate.format(selection.first);
                    endDate = sqlDate.format(selection.second);

                    // Display selected date range
                    binding.date.setText(startD + " to " + endD);
                }
            });
        });

        SimpleDateFormat first = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat second = new SimpleDateFormat("dd LLL, yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        startDate = dateFormat.format(new Date());
        String startD = first.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        endDate = dateFormat.format(tomorrow);
        String endD = second.format(tomorrow);

        binding.date.setText(startD + " to " + endD);

        setupRecyclerView();
        ProcessSearch(startDate, endDate, 0);

        binding.date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 5){
                    ProcessSearch(startDate, endDate, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }


    private void ProcessSearch(String startDate, String endDate, int i) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<InvoiceDetails> call = apiService.fetchInvoicesByDateRange(
                startDate, endDate, "/"+ Helpers.getTextFromSharedPref(getActivity(), "code")+"/", i
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
                        ProcessSearch(startDate, endDate, nextTokenKey);
                    }
                }
            }
        });
    }
}