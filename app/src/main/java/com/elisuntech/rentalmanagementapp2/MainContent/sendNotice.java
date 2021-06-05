package com.elisuntech.rentalmanagementapp2.MainContent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class sendNotice extends AppCompatActivity implements View.OnClickListener {
   Button submit;
   RelativeLayout mysnackbarView;
   EditText message,title;
   RequestQueue requestQueue;
    CheckBox sendsmstotenant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notice);

        requestQueue = Volley.newRequestQueue(this);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        submit = findViewById(R.id.submit);
        mysnackbarView = findViewById(R.id.mysnackbarView);
        sendsmstotenant = findViewById(R.id.sendsmstotenant);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                String Str_title = title.getText().toString().trim();
                String Str_message = message.getText().toString().trim();

                if (Str_message.equals("") || Str_message.length()<5||Str_title.equals("") || Str_title.length()<2){
                    Snackbar.make(mysnackbarView,"Title and Notice character length must be more than 5 charactes",Snackbar.LENGTH_LONG).show();
                }else{
                    sharedPreference object = new sharedPreference(sendNotice.this);
                    if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                        Snackbar.make(mysnackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                                .setAction("Pay now ", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sharedMethod.makePayments("appUserPayment", sendNotice.this);
                                    }
                                }).show();
                    } else {

                        if (sendsmstotenant.isChecked()){
                            String numberofsms = sharedMethod.getAppUsesAuthentication("SmsToken");
                            if (numberofsms.equals("0")){
                                Snackbar.make(mysnackbarView,"You do not have enough sms to send text messages",Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Buy Now", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                sharedMethod.makePayments("appUserPayment", sendNotice.this);
                                            }
                                        }).show();
                            }else{
                                sendNoticeToTenant(Str_title, Str_message, object.getIsLoggedIn(),numberofsms);
                            }

                        }else{
                            sendNoticeToTenant(Str_title, Str_message, object.getIsLoggedIn(),"0");

                        }
                    }
                }
                break;
        }
    }

    public void sendNoticeToTenant(final String title, final String message, final String landloardID, final String numberOfSmstobesent){
        String url = URL_CONNECTOR+"sendnoticetotenants.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("sendNotice.java sendNoticeToTenant() PPPP "+response);
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("sendNotice.java sendNoticeToTenant()"+response);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                sharedMethod.updateUserDataAfterUpadates(sendNotice.this);
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(sendNotice.this);
                                 mBuilder.setMessage(result.getString("message"))
                                         .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface, int i) {
                                                 Intent intent = new Intent(sendNotice.this, sendNotice.class);
                                                 startActivity(intent);
                                                 finish();
                                             }
                                         })
                                         .setCancelable(false)
                                         .create()
                                         .show();

                            }else{
                                Snackbar.make(mysnackbarView,result.getString("message"),Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Snackbar.make(mysnackbarView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(sendNotice.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("title",title);
                getParams.put("message",message);
                getParams.put("landloadID",landloardID);
                getParams.put("numberOfSmstobesent",numberOfSmstobesent);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }
}