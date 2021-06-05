package com.elisuntech.rentalmanagementapp2.MainContent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.termsandcondition_aboutCA;
import com.elisuntech.rentalmanagementapp2.DMC.termsandcondition_aboutDMC;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class termsandconditionaboutus extends AppCompatActivity   {
    private RecyclerView recyclerView;
    private List<termsandcondition_aboutDMC> tenantList;
    private termsandcondition_aboutCA mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsandconditionaboutus);

        recyclerView = findViewById(R.id.recycler_view);
        tenantList = new ArrayList<>();
        mAdapter = new termsandcondition_aboutCA(this, tenantList);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchtermsandconditionAboutus();
    }
    private void fetchtermsandconditionAboutus() {
        String URL = URL_CONNECTOR + "pulltermsandcondition_aboutus.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REsponse deliveryTruck.java  " + response);
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<termsandcondition_aboutDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<termsandcondition_aboutDMC>>() {
                        }.getType());

                        // adding contacts to contacts list
                        tenantList.clear();
                        tenantList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Internet Connection problem  ", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > params = new HashMap<>();
                Intent intent = getIntent();
                params.put("type",intent.getStringExtra("type"));
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }

}