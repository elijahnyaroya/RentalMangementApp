package com.elisuntech.rentalmanagementapp2.MainContent;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.watermeterReadingCA;
import com.elisuntech.rentalmanagementapp2.DMC.watermeterReadingDMC;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyDividerItemDecoration;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisuntech.rentalmanagementapp2.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class waterMeterReading extends AppCompatActivity implements watermeterReadingCA.meterReadingAdapterListener{
    private static final String TAG = WaterBillPayment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<watermeterReadingDMC> tenantList;
    private watermeterReadingCA mAdapter;
    private SearchView searchView;
    CoordinatorLayout snackBar;
    RelativeLayout relativelayoutDisplayNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_meter_reading);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meter Reading");

        snackBar = findViewById(R.id.snackBar);
        recyclerView = findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        tenantList = new ArrayList<>();
        mAdapter = new watermeterReadingCA(this, tenantList, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchTenants();

    }

    private void fetchTenants() {
        String URL = URL_CONNECTOR+"pullallTenantss.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REsponse pullwaterbillpayments.php "+response);
                        if (response.length()<5){
                            recyclerView.setVisibility(View.GONE);
                            relativelayoutDisplayNothing.setVisibility(View.VISIBLE);
                        }
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the data! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<watermeterReadingDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<watermeterReadingDMC>>() {
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
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error:  " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<String, String>();
                params.put("landloadID", sharedPreference.getIsLoggedIn());
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
    public void onTenantSelected(final watermeterReadingDMC tenant) {
        if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
            Snackbar.make(snackBar, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                    .setAction("Pay now ", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedMethod.makePayments("appUserPayment", waterMeterReading.this);
                        }
                    }).show();
        } else {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.meterreadingcustomalert, null);

        final TextView name = (TextView) view.findViewById(R.id.name);
        final TextView meterNo = (TextView) view.findViewById(R.id.meterNo);
        name.setText(tenant.getTenantName());
        meterNo.setText(tenant.getMeterNo());

        final EditText newReadings = (EditText) view.findViewById(R.id.newReadings);
        final Button submit = (Button) view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newReadings1 = newReadings.getText().toString().trim();
                if (newReadings1.equals("0") || newReadings1.length() < 2) {
                    Toast.makeText(waterMeterReading.this, "Check your readings and try again.", Toast.LENGTH_SHORT).show();
                } else {
                    AddMeterReadingtoDatabase(newReadings1, tenant.getHouseNo(), tenant.getId(), tenant.getTenantPhone());
                }
            }
        });

        dialogBuilder.setView(view);
        dialogBuilder.show();

    }
       }

       public void AddMeterReadingtoDatabase(final String meterreadings, final String HouseNo, final String TenantID, final String TenantPhone){

        String URL = URL_CONNECTOR+"addMeterreadings.php";

           StringRequest request2 = new StringRequest(StringRequest.Method.POST,URL,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           System.out.println("WRRWRWRWRRWRWRRW "+response);
                           try {
                               JSONArray jsonArray = new JSONArray(response);
                               JSONObject result = jsonArray.getJSONObject(0);

                               if (result.getString("code").equals("success")){
                                   AlertDialog.Builder mb = new AlertDialog.Builder(waterMeterReading.this);
                                   mb.setMessage(result.getString("message"))
                                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           })
                                           .setCancelable(false)
                                           .create()
                                           .show();
                               }else{
                                   AlertDialog.Builder mb = new AlertDialog.Builder(waterMeterReading.this);
                                   mb.setMessage(result.getString("message"))
                                           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                               }
                                           })
                                           .setCancelable(false)
                                           .create()
                                           .show();
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                       }
                   }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   Toast.makeText(getApplicationContext(), "Error: Network error " + error.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }){
               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   Map<String ,String > params = new HashMap<String, String>();
                   params.put("landlordID", sharedPreference.getIsLoggedIn());
                   params.put("meterreadings",meterreadings);
                   params.put("HouseNo",HouseNo);
                   params.put("TenantID",TenantID);
                   params.put("TenantPhone",TenantPhone);

                   System.out.println("Params 34344 <<<>>>"+params);
                   return params;
               }
           };

           MyApplication.getInstance().addToRequestQueue(request2);
       }
    }