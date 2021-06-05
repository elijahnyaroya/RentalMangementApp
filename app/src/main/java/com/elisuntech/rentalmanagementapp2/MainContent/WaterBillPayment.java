package com.elisuntech.rentalmanagementapp2.MainContent;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.waterbillCA;
import com.elisuntech.rentalmanagementapp2.DMC.waterbillDMC;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.otherPaymentDefaulters;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.rentDefaulter;
import com.elisuntech.rentalmanagementapp2.PaymentDefaulters.waterDefaulters;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyDividerItemDecoration;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class WaterBillPayment extends AppCompatActivity implements waterbillCA.waterAdapterListener{
    private static final String TAG = WaterBillPayment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<waterbillDMC> tenantList;
    private waterbillCA mAdapter;
    private SearchView searchView;
    CoordinatorLayout mysnackbarView;
    RelativeLayout relativelayoutDisplayNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.water_bill_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.waterusers);

        recyclerView = findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        mysnackbarView = findViewById(R.id.mysnackbarView);
        tenantList = new ArrayList<>();
        mAdapter = new waterbillCA(this, tenantList, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchTenants();

        FabSpeedDial fabSpeedDial = (FabSpeedDial)findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                int id2 = menuItem.getGroupId();

                if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                    Snackbar.make(mysnackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                            .setAction("Pay now ", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sharedMethod.makePayments("appUserPayment", WaterBillPayment.this);
                                }
                            }).show();
                } else {
                    if (menuItem.getTitle().equals("Rent Defaulters")) {
                        startActivity(new Intent(WaterBillPayment.this, rentDefaulter.class));
                    } else if (menuItem.getTitle().equals("Water bill Defaulters")) {
                        startActivity(new Intent(WaterBillPayment.this, waterDefaulters.class));
                    } else if (menuItem.getTitle().equals("Other Payment Defaulter")) {
                        startActivity(new Intent(WaterBillPayment.this, otherPaymentDefaulters.class));
                    } else {
                        //
                    }
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });
    }

    /**
     * fetches json by making http calls
     */
    private void fetchTenants() {
        String URL = URL_CONNECTOR+"pullwaterbillpayments.php";

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

                        List<waterbillDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<waterbillDMC>>() {
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
    public void onTenantSelected(final waterbillDMC tenant) {
        //Toast.makeText(getApplicationContext(), "Selected: " + tenant.getTenantID() + ", " + tenant.getTenantName(), Toast.LENGTH_LONG).show();

        class StoreValue {
            int value;
            public void set(int i) {
                value = i;
            }
            public int get() {
                return value;
            }
        }

        final StoreValue valueselected = new StoreValue();
        String[] paymentmethodslist = {"Make Payment"};
        AlertDialog.Builder mBuilder;
        mBuilder = new AlertDialog.Builder(WaterBillPayment.this);
        mBuilder.setTitle("Payment");


        mBuilder.setSingleChoiceItems(paymentmethodslist, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueselected.set(which);
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                which = valueselected.get();
                switch (which) {
                    case 0:
                        System.out.println("AMOUNT "+tenant.getAmount()+" paid amount "+tenant.getPaidamount());
                        if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                            Snackbar.make(mysnackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                                    .setAction("Pay now ", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sharedMethod.makePayments("appUserPayment", WaterBillPayment.this);
                                        }
                                    }).show();
                        } else {
                            makepaymentOtherpayment(tenant.getPaidamount(), tenant.getWaterId(), tenant.getAmount(), WaterBillPayment.this);
                        }
                        break;
                    default:
                        break;
                }
            }
        }).setCancelable(true);
        mBuilder.create();
        mBuilder.show();
    }

    public void makepaymentOtherpayment(String paidamount, final String defaultID, final String amount, final Activity context){
        final int remainingAmount = Integer.parseInt(amount) - Integer.parseInt(paidamount);

        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = context.getLayoutInflater().from(context);
        final View dialogView = inflater.inflate(R.layout.makepayment_layout, null);

        final EditText amount_et = (EditText) dialogView.findViewById(R.id.amount);
        final TextView amountRemaining = (TextView) dialogView.findViewById(R.id.amountRemaining);
        final TextView totalAmount = (TextView) dialogView.findViewById(R.id.totalAmount);
        final Button submit = (Button) dialogView.findViewById(R.id.submit);

        amountRemaining.setText("Remaining Amount : Ksh. "+(String.valueOf(remainingAmount)));
        totalAmount.setText("Total Amount : Ksh. "+amount);
        amount_et.setText(String.valueOf(remainingAmount));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountPaid = amount_et.getText().toString().trim();

                if (amountPaid.equals("")||amountPaid.length()<1){
                    Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show();
                }else{
                    if (Integer.parseInt(amountPaid) >Integer.parseInt(amount) || Integer.parseInt(amountPaid) >remainingAmount){
                        Toast.makeText(context, "Amount to be paid cannot be more than item amount", Toast.LENGTH_SHORT).show();
                    }else {
                        proceedToPayment(amountPaid,defaultID,context);
                        dialogBuilder.dismiss();
                    }
                }
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void proceedToPayment(final String amountToPay, final String defaultID , final Context context){
        String url = URL_CONNECTOR+"makewaterBillPay.php";

        StringRequest requestPay = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response waterPaymentHistory "+response);
                            JSONObject object = responses.getJSONObject(0);
                            String result = object.getString("code");
                            if (result.equals("success")){
                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();

                                Intent intents = new Intent(context, WaterBillPayment.class);
                                finish();
                                context.startActivity(intents);
                            }else{
                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "Connectivity problem", Toast.LENGTH_SHORT).show();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error:  " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<String, String>();

                params.put("landloadID", sharedPreference.getIsLoggedIn());
                params.put("paymentID", defaultID);
                params.put("amountTopay", amountToPay);
                System.out.println("PARAM "+params);
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(requestPay);
    }
}