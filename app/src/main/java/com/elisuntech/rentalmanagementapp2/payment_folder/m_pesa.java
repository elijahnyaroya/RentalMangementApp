package com.elisuntech.rentalmanagementapp2.payment_folder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class m_pesa {

    public static void mPesaPayment(final Context context, final String typeofpayment, final String price) {
        AlertDialog.Builder mb = new AlertDialog.Builder(context);
        mb.setTitle("You are paying Ksh. "+price);
        final EditText phone = new EditText(context);

        phone.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
        phone.setPadding(40,20,10,20);
        mb.setView(phone);
        mb.setCancelable(false)
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phonenumber = phone.getText().toString().trim();
                        if (phonenumber.equals("") || phonenumber.length() < 10) {
                            mPesaPayment(context, typeofpayment, price);
                        } else {
                            proceedToPaayment(context, typeofpayment, price, phonenumber);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }


    public static void proceedToPaayment(final Context context, final String typeofpayment, final String price, String phonenumber) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setMessage("Processing payment...");
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String url = "Payments/mpesa/stk_initiate.php";

        final String phoneno_paid = sharedMethod.cleanPhoneNumber(phonenumber);
        System.out.println("MakePayment.java MakepaymentforSubscription() (A) Phoneno_paid: (" + phoneno_paid + ") url "+URL_CONNECTOR + url);

        StringRequest requestingServer = new StringRequest(Request.Method.POST, URL_CONNECTOR + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("MakePayment.java MakepaymentforSubscription() (B) " + response);
                        try {
                            final JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("ResponseCode").equals("0")) {
                                try {
                                    progressDialog.dismiss();
                                    System.out.println("MakePayment.java MakepaymentforSubscription() (C) " + jsonObject.getString("CheckoutRequestID"));
                                    confirmPayment(jsonObject.getString("CheckoutRequestID"), phoneno_paid, price, typeofpayment,context);
                                    System.out.println("We have returned from ConfirmPayment");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "(A) An error occurred while making payment, Please try again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "(B) JSONException - An error occurred while making payment, Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Internet connection problem", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("landloard", sharedPreference.getIsLoggedIn());
                params.put("typeofpayment", typeofpayment);
                params.put("phonenopaid", phoneno_paid);
                params.put("price", price);

                System.out.println("PARAMS "+params);
                return params;
            }
        };
        mRequestQueue.add(requestingServer);
        requestingServer.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    /// passing payment data and updating it on our systems
    public static void confirmPayment(final String CheckoutRequestID, final String phoneno_paid, final String price, final String typeofpayment, final Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setMessage("Confirming payment...");
        String url = "Payments/mpesa/confirmpayment.php";
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest requestingServer = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("MakePayment.java : confirmPayment() " + response);

                        try {
                            JSONArray responses = new JSONArray(response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");
                            if (result.equals("success")) {
                                progressDialog.dismiss();
                                    Toast.makeText(context, "Success " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                //lead to recovery page
                                androidx.appcompat.app.AlertDialog.Builder xBuilder;
                                xBuilder = new androidx.appcompat.app.AlertDialog.Builder(context)
                                        .setMessage(jsonObject.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                xBuilder.create()
                                        .show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                           progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, "Internet connection problem", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("checkoutID", CheckoutRequestID);
                params.put("payerPhone", phoneno_paid);
                params.put("amount", price);
                params.put("typeofpayment", typeofpayment);
                params.put("landloard", typeofpayment);

                return params;
            }
        };

        mRequestQueue.add(requestingServer);
        requestingServer.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
