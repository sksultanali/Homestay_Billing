package com.developerali.bongbilling.Helpers;

import com.developerali.bongbilling.Models.ApiResponse;
import com.developerali.bongbilling.Models.InvoiceData;
import com.developerali.bongbilling.Models.InvoiceDetails;
import com.developerali.bongbilling.Models.LoginRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("testApis/bong_billing.php")
    Call<LoginRes> getLoginDetails(
            @Query("action") String action,
            @Query("phone") String phone,
            @Query("password") String password
    );

    @POST("testApis/bong_billing.php?action=saveInvoice")
    Call<ApiResponse> saveInvoice(
            @Body InvoiceData invoiceData
    );

    @POST("testApis/bong_billing.php?action=updateInvoiceDetails")
    Call<ApiResponse> updateInvoiceDetails(
            @Body InvoiceData invoiceData
    );

    // Fetch Invoices
    @GET("testApis/bong_billing.php?action=fetchInvoices")
    Call<ApiResponse> fetchInvoices(
            @Query("offset") int offset
    );

    // Search Invoices
    @GET("testApis/bong_billing.php?action=searchInvoices")
    Call<ApiResponse> searchInvoices(
            @Query("keyword") String keyword,
            @Query("offset") int offset
    );

    @GET("testApis/bong_billing.php?action=fetchOrSearchInvoices")
    Call<InvoiceDetails> searchOrFetchInvoices(
            @Query("keyword") String keyword,
            @Query("code") String code,
            @Query("offset") int offset
    );

    @GET("testApis/bong_billing.php?action=fetchInvoicesByDateRange")
    Call<InvoiceDetails> fetchInvoicesByDateRange(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("code") String code,
            @Query("offset") int offset
    );

    @GET("testApis/bong_billing.php?action=getCountByName")
    Call<ApiResponse> getCountByName(
            @Query("name") String name
    );

}
