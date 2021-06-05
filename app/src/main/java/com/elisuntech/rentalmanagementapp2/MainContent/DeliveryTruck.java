package com.elisuntech.rentalmanagementapp2.MainContent;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.DeliveryTruckCA;
import com.elisuntech.rentalmanagementapp2.DMC.DeliveryTruckDMC;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyDividerItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class DeliveryTruck extends AppCompatActivity implements DeliveryTruckCA.DelivertTruckAdapterListener {
    private static final String TAG = TenantPayment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<DeliveryTruckDMC> tenantList;
    private DeliveryTruckCA mAdapter;
    private SearchView searchView;
    String phoneNumber;
    private static  final int  REQUEST_CALL =1;
    RelativeLayout relativelayoutDisplayNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_truck);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery Truck");

        recyclerView = findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        tenantList = new ArrayList<>();
        mAdapter = new DeliveryTruckCA(this, tenantList, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchTrucksData();


    }


    private void fetchTrucksData() {
        String URL = URL_CONNECTOR + "pulldeliverytrucks.php";

        StringRequest request = new StringRequest(StringRequest.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REsponse deliveryTruck.java  " + response);
                        if (response.length()<5){
                            recyclerView.setVisibility(View.GONE);
                            relativelayoutDisplayNothing.setVisibility(View.VISIBLE);
                        }
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<DeliveryTruckDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<DeliveryTruckDMC>>() {
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
                // error in getting json
                Toast.makeText(getApplicationContext(), "Internet Connection problem  "  , Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onTenantSelected(final DeliveryTruckDMC tenant) {
        phoneNumber = tenant.getPhone();
        Toast.makeText(DeliveryTruck.this, "Making call ", Toast.LENGTH_SHORT).show();


            if (ContextCompat.checkSelfPermission(DeliveryTruck.this,
                    Manifest.permission.CALL_PHONE )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DeliveryTruck.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else{
                String dial = "tel:"+phoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }


    }

   private void makePhonecall(String phone){
        if (phone.trim().length()>8){

            if (ContextCompat.checkSelfPermission(DeliveryTruck.this,
                    Manifest.permission.CALL_PHONE )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DeliveryTruck.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else{
                String dial = "tel:"+phone;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }else{
            Toast.makeText(this, "We can not make phone call now..", Toast.LENGTH_SHORT).show();
        }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode== REQUEST_CALL){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhonecall(phoneNumber);
            }else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}