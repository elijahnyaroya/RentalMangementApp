/*
package com.crypsol.rentalmanagementapp.payment_folder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crypsol.rentalmanagementapp.MainContent.dashboard;
import com.crypsol.rentalmanagementapp.R;
import com.crypsol.rentalmanagementapp.commonMethods.sharedPreference;
import com.crypsol.rentalmanagementapp.connection.connect;
import com.google.android.material.snackbar.Snackbar;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.crypsol.rentalmanagementapp.connection.connect.URL_CONNECTOR;

public class paypalPayment extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7777;

    private static PayPalConfiguration config = new PayPalConfiguration()
            //.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    Button btnPayNow;
    EditText edtAmount;


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
    String typeofpayment,userID,amount;
    TextView showreelpaid;
    StringRequest requestingServer;
    LinearLayout mysnackbarView;
    RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypal_payment);
        //start paypal service
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        mRequestQueue = Volley.newRequestQueue(this);

        btnPayNow = findViewById(R.id.btnPayNow);
        edtAmount = findViewById(R.id.edtAmount);
        showreelpaid = findViewById(R.id.showreelpaid);
        mysnackbarView = findViewById(R.id.mysnackbarView);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });


        Intent getShowreel_id  = getIntent();
        amount = getShowreel_id.getStringExtra("amount");
        userID = getShowreel_id.getStringExtra("userID");
        typeofpayment = getShowreel_id.getStringExtra("typeofpayment");


        edtAmount.setText("Ksh. "+amount);
        showreelpaid.setText("Make payment to continue enjoying our services");


    }
    //TODO 1050 Remember to convert currency to what is accepted by PAYPAL for now its 'USD'
    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)),"USD",
                "App Usage Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        JSONObject paypalObject = confirmation.toJSONObject();
                        System.out.println("RESPONSE ELIJAH " + paymentDetails);

                        String paypalID ="";
                        String paypalState ="";
                        String paypalcreatedtime="";
                        JSONObject  getPayPalResponse = paypalObject.getJSONObject("response");
                        for (int i = 0;i<getPayPalResponse.length();i++){
                            paypalcreatedtime = getPayPalResponse.getString("create_time");
                            paypalID = getPayPalResponse.getString("id");
                            paypalState  = getPayPalResponse.getString("state");
                            // System.out.println("RESPONSE ELIJAH 5 " + "TIME "+paypalcreatedtime+" ID "+paypalID+" State "+paypalState );
                        }

                        if (paypalState.equals("approved")){
                            ProgressDialog progressDialog = new ProgressDialog(this);
                            progressDialog.setMessage("Please wait...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            Intent getShowreel_id  = getIntent();
                            String amount = getShowreel_id.getStringExtra("amount");
                            String userID = getShowreel_id.getStringExtra("userID");
                            String typeofpayment = getShowreel_id.getStringExtra("typeofpayment");
                            progressDialog.dismiss();
                            confirmPayment(paypalState,paypalID,paypalcreatedtime, amount, userID, typeofpayment);

                        }else {
                            AlertDialog.Builder mBuildercancel = new AlertDialog.Builder(paypalPayment.this);
                            mBuildercancel.setMessage("Payment is not successful. Please try later. Thank you")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent openDashboardActivity = new Intent(paypalPayment.this, dashboard.class);
                                            startActivity(openDashboardActivity);
                                            finish();
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
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

    public void  confirmPayment(final String paypalState, final String paypalID, final String paypalcreatedtime, final String amount, final String userID, final String typeofpayment) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait we are confirming payment.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        String url = URL_CONNECTOR+"Payments/Paypal/confirmpayment.php";
        requestingServer = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("payPalPayment.java : confirmPayment() " + response);

                        try {
                            JSONArray responses = new JSONArray(response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");
                            if (result.equals("success")) {
                                progressDialog.dismiss();
                                Snackbar.make(mysnackbarView, jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();
                                androidx.appcompat.app.AlertDialog.Builder xBuilder;
                                xBuilder = new AlertDialog.Builder(paypalPayment.this)
                                        .setMessage(jsonObject.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {;
                                                dialog.dismiss();
                                            }
                                        });
                                xBuilder.setCancelable(false)
                                        .create()
                                        .show();
                            } else {
                                progressDialog.dismiss();
                                Snackbar.make(mysnackbarView, jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(paypalPayment.this, "Internet connection problem", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String> params = new HashMap<String,String>();

                params.put("paypalState", paypalState);
                params.put("paypalID", paypalID);
                params.put("paypalcreatedtime", paypalcreatedtime);
                params.put("userID", userID);
                params.put("amount", amount);
                params.put("typeofpayment",typeofpayment);

                return params;
            }
        };
        mRequestQueue = Volley.newRequestQueue(paypalPayment.this);
        mRequestQueue.add(requestingServer);
        requestingServer.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}

*/
