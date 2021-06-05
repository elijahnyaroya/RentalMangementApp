package com.elisuntech.rentalmanagementapp2.BuyandSell;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.saleContactCA;
import com.elisuntech.rentalmanagementapp2.DMC.saleContactDMC;
import com.elisuntech.rentalmanagementapp2.Messages.MessageContactList;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyDividerItemDecoration;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.elisuntech.rentalmanagementapp2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class SalesContact extends AppCompatActivity implements saleContactCA.TenantssAdapterListener{
    private static final String TAG = MessageContactList.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<saleContactDMC> tenantList;
    private saleContactCA mAdapter;
    private SearchView searchView;
    CoordinatorLayout snackbarView;
    RelativeLayout relativelayoutDisplayNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        recyclerView = findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        snackbarView = findViewById(R.id.snackbarView);
        tenantList = new ArrayList<>();
        mAdapter = new saleContactCA(this, tenantList, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchsalesChatContact();
    }

    private void fetchsalesChatContact() {
        String URL = URL_CONNECTOR+"pullsalecontact.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REsponse fetchTenantsChatContact "+response);
                        if (response.length()<5){
                            recyclerView.setVisibility(View.GONE);
                            relativelayoutDisplayNothing.setVisibility(View.VISIBLE);
                        }
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<saleContactDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<saleContactDMC>>() {
                        }.getType());

                        // adding contacts to contacts list
                        tenantList.clear();
                        if (items==null){

                        }else{
                            tenantList.addAll(items);
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error:  " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<String, String>();
                try {
                    params.put("receiverphoneno", sharedPreference.getUSERDETAILS().getString("phoneNo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

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
    public void onTenantSelected(saleContactDMC tenantS) {
        sharedPreference.setChatcontact(tenantS.getUserID());//phone number
        if (sharedMethod.getAppUsesAuthentication("inappsales").equals("notpaid")) {
            Snackbar.make(snackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                    .setAction("Pay now ", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedMethod.makePayments("sellingInApp", SalesContact.this);
                        }
                    }).show();
        } else {
            Intent rent= new Intent(SalesContact.this, chatwithbuyers.class);
            startActivity(rent);
        }


    }
}