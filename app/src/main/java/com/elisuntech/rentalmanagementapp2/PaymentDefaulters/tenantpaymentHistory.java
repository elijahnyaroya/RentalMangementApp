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

public class tenantpaymentHistory extends AppCompatActivity  implements tenanthistoryCA.OnItemClickListener  {
    TextView HouseName,name;
    ImageView thumbnail;
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private ArrayList<tenanthistoryDMC> tenantList;
    private tenanthistoryCA mAdapter;

    String tenantIDjson ;
    String tenantName ;
    String tenantRoomno  ;
    String tenantImage ;

    RelativeLayout relativelayoutDisplayNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tenantpayment_history);
 

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        recyclerView.setLayoutManager(new LinearLayoutManager(tenantpaymentHistory.this));
        recyclerView.setHasFixedSize(true);
        tenantList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObjectget = sharedPreference.getDEFAULTERUSERDATA();
        System.out.println("OOOO "+jsonObjectget);
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
            Glide.with(tenantpaymentHistory.this).load(tenantImage).into(thumbnail);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        Intent getIntent = getIntent();



      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tenantRoomNo",getIntent.getStringExtra("tenantRoomNo"));
            jsonObject.put("Name",getIntent.getStringExtra("Name"));
            jsonObject.put("tenantImage",getIntent.getStringExtra("tenantImage"));
            jsonObject.put("tenantId",getIntent.getStringExtra("tenantId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedPreference.setDEFAULTERUSERDATA(jsonObject);*/

        fetchTenantsHistory();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rent:
                        Intent openactivity = new Intent(tenantpaymentHistory.this,tenantpaymentHistory.class);
                         /*JSONObject jsonObjectget = sharedPreference.getDEFAULTERUSERDATA();
                        try {
                           openactivity.putExtra("tenantRoomNo",jsonObjectget.getString("tenantRoomNo"));
                            openactivity.putExtra("Name",jsonObjectget.getString("Name"));
                            openactivity.putExtra("tenantImage",jsonObjectget.getString("tenantImage"));
                            openactivity.putExtra("tenantId",jsonObjectget.getString("tenantId"));*/
                            finish();
                            startActivity(openactivity);
                       /* } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        break;
                    case R.id.water:
                        Intent openactivity4 = new Intent(tenantpaymentHistory.this,waterPaymentHistory.class);
                        /*JSONObject jsonObjectget4 = sharedPreference.getDEFAULTERUSERDATA();

                        try {
                            openactivity4.putExtra("tenantRoomNo",jsonObjectget4.getString("tenantRoomNo"));
                            openactivity4.putExtra("Name",jsonObjectget4.getString("Name"));
                            openactivity4.putExtra("tenantImage",jsonObjectget4.getString("tenantImage"));
                            openactivity4.putExtra("tenantId",jsonObjectget4.getString("tenantId"));*/
                            finish();
                            startActivity(openactivity4);
                      /*  } catch (JSONException e) {
                            e.printStackTrace();
                        }
*/

                        break;
                    case R.id.others:
                        Intent openactivity3 = new Intent(tenantpaymentHistory.this,otherspaymentHistory.class);
                     /*   JSONObject jsonObjectget3 = sharedPreference.getDEFAULTERUSERDATA();
                        try {
                            openactivity3.putExtra("Name",jsonObjectget3.getString("Name"));
                            openactivity3.putExtra("tenantImage",jsonObjectget3.getString("tenantImage"));
                            openactivity3.putExtra("tenantId",jsonObjectget3.getString("tenantId"));*/
                            finish();
                            startActivity(openactivity3);
                      /*  } catch (JSONException e) {
                            e.printStackTrace();
                        }
*/
                        break;
                }
                return true;
            }
        });

    }

    private void fetchTenantsHistory() {
        String url = URL_CONNECTOR+"pullindividualhousepaymentdefaulters.php";

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
                            mAdapter = new tenanthistoryCA("tenantpaymentHistory",tenantpaymentHistory.this,tenantpaymentHistory.this,tenantList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(tenantpaymentHistory.this);
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

                //Intent getTenantData = getIntent();
                params.put("landloadID", sharedPreference.getIsLoggedIn());
                //params.put("tenantID", getTenantData.getStringExtra("tenantId") );
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
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(tenantpaymentHistory.this);
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
        String url = URL_CONNECTOR+"makerentPay.php";

        StringRequest requestPay = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response "+response);
                            JSONObject object = responses.getJSONObject(0);
                            String result = object.getString("code");
                            if (result.equals("success")){
                                Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();

                                Intent intents = new Intent(context,tenantpaymentHistory.class);
                               /* JSONObject jsonObjectget = sharedPreference.getDEFAULTERUSERDATA();
                                intents.putExtra("tenantRoomNo",jsonObjectget.getString("tenantRoomNo"));
                                intents.putExtra("Name",jsonObjectget.getString("Name"));
                                intents.putExtra("tenantImage",jsonObjectget.getString("tenantImage"));
                                intents.putExtra("tenantId",jsonObjectget.getString("tenantId"));*/
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