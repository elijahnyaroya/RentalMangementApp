package com.elisuntech.rentalmanagementapp2.PaymentDefaulters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.elisuntech.rentalmanagementapp2.CA.tenanthistoryCA;
import com.elisuntech.rentalmanagementapp2.DMC.tenanthistoryDMC;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class otherspaymentHistory extends AppCompatActivity implements tenanthistoryCA.OnItemClickListener {
    TextView HouseName,name;
    ImageView thumbnail;
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private ArrayList<tenanthistoryDMC> tenantList;
    private tenanthistoryCA mAdapter;
    JSONObject jsonObjectget = sharedPreference.getDEFAULTERUSERDATA();
    String tenantIDjson ;
    String tenantName ;
    String tenantRoomno ;
    String tenantImage ;
    RelativeLayout relativelayoutDisplayNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherspayment_history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        recyclerView.setLayoutManager(new LinearLayoutManager(otherspaymentHistory.this));
        recyclerView.setHasFixedSize(true);
        tenantList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        name = findViewById(R.id.name);
        HouseName = findViewById(R.id.HouseName);
        thumbnail = findViewById(R.id.thumbnail);
        try {
            tenantIDjson = jsonObjectget.getString("tenantId");
            tenantName = jsonObjectget.getString("Name");
            tenantRoomno = jsonObjectget.getString("tenantRoomNo");
            tenantImage = jsonObjectget.getString("tenantImage");

            HouseName.setText("House No : "+tenantRoomno);
            name.setText(tenantName);
            Glide.with(otherspaymentHistory.this).load(tenantImage).into(thumbnail);

        } catch (JSONException e) {
            e.printStackTrace();
        }




        fetchTenantsHistory();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rent:
                        Intent openactivity = new Intent(otherspaymentHistory.this,tenantpaymentHistory.class);

                        finish();
                        startActivity(openactivity);
                        break;
                    case R.id.water:
                        Intent openactivity4 = new Intent(otherspaymentHistory.this,waterPaymentHistory.class);
                        finish();
                        startActivity(openactivity4);
                        break;
                    case R.id.others:
                        Intent openactivity3 = new Intent(otherspaymentHistory.this,otherspaymentHistory.class);
                        finish();
                        startActivity(openactivity3);
                        break;
                }
                return true;
            }
        });
    }
    private void fetchTenantsHistory() {
        String url = URL_CONNECTOR+"pullindividualOtherDefaulters.php";

        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()<5){
                            recyclerView.setVisibility(View.GONE);
                            relativelayoutDisplayNothing.setVisibility(View.VISIBLE);
                        }

                        try {

                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response "+response);
                            for (int i=0;i<responses.length();i++){
                                JSONObject hit = responses.getJSONObject(i);
                                String id = hit.getString("id");
                                String dayspassed = hit.getString("dayspassed");
                                String datedue = hit.getString("datedue");
                                String amount = hit.getString("amount");
                                String paidamount = hit.getString("paidamount");

                                tenantList.add(new tenanthistoryDMC(amount,datedue,dayspassed,id,paidamount));


                            }
                            mAdapter = new tenanthistoryCA("otherspaymentHistory",otherspaymentHistory.this,otherspaymentHistory.this,tenantList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(otherspaymentHistory.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error:  " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<String, String>();
                params.put("landloadID", sharedPreference.getIsLoggedIn());
                params.put("tenantID", tenantIDjson);
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }
    @Override
    public void onItemClick(int position) {
        tenanthistoryDMC pos = tenantList.get(position);

        int amountPaid = Integer.parseInt(pos.getPaidamount());
        int totalAmount = Integer.parseInt(pos.getAmount());
        if (amountPaid==totalAmount){

        }else{
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(otherspaymentHistory.this);
            mBuilder.setMessage("Amount Paid : Ksh. "+pos.getPaidamount())
                    .setCancelable(true)
                    .create()
                    .show();
        }
    }


    public void makepayments(String paidamount, final String defaultID, final String amount, final Activity context){
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
        String url = URL_CONNECTOR+"makeOtherBillPay.php";

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

                                Intent intents = new Intent(context,otherspaymentHistory.class);
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