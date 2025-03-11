package com.developerali.bongbilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developerali.bongbilling.Helpers.ApiService;
import com.developerali.bongbilling.Helpers.Helpers;
import com.developerali.bongbilling.Helpers.RetrofitClient;
import com.developerali.bongbilling.Models.LoginRes;
import com.developerali.bongbilling.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiService = RetrofitClient.getClient().create(ApiService.class);


        binding.btn.setOnClickListener(v->{
            String phone = binding.phone.getText().toString();
            String password = binding.password.getText().toString();

            if (phone.isEmpty()){
                binding.phone.setError("*");
            } else if (password.isEmpty()) {
                binding.password.setError("*");
            }else {

                binding.progressBar.setVisibility(View.VISIBLE);
                Call<LoginRes> call = apiService.getLoginDetails(
                        "checkUserCred", phone, password
                );

                call.enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                        if (response.isSuccessful() && response.body() != null){
                            binding.progressBar.setVisibility(View.GONE);
                            LoginRes apiResponse = response.body();
                            if (apiResponse.getStatus().equalsIgnoreCase("success")){
                                Helpers.saveTextToSharedPref(LoginActivity.this, "code", apiResponse.getData().getCode());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else {
                                Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}