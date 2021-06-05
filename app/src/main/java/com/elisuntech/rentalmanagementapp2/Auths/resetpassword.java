package com.elisuntech.rentalmanagementapp2.Auths;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class resetpassword extends AppCompatActivity implements View.OnClickListener{

    EditText atvPhone,atvPassword,atvnewPassword,tvOtp;
    Button btnRequestreset,btnResetpassword;
    TextView tvToLogin,tvToLogin2;
    LinearLayout enterPhonenumber,resetpasswordLayout,mysnackbarView;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
        getSupportActionBar().hide();

        requestQueue = Volley.newRequestQueue(this);
        //tvToLogin2 = findViewById(R.id.tvToLogin2);
        tvToLogin = findViewById(R.id.tvToLogin);

        atvPhone = findViewById(R.id.atvPhone);
        atvPassword = findViewById(R.id.atvPassword);
        atvnewPassword = findViewById(R.id.atvnewPassword);
        tvOtp = findViewById(R.id.tvOtp);


        btnRequestreset = findViewById(R.id.btnRequestreset);
        btnResetpassword = findViewById(R.id.btnResetpassword);

        enterPhonenumber = findViewById(R.id.enterPhonenumber);
        resetpasswordLayout = findViewById(R.id.resetpasswordLayout);
        mysnackbarView = findViewById(R.id.mysnackbarView);

        btnRequestreset.setOnClickListener(this);
        btnResetpassword.setOnClickListener(this);
        tvToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRequestreset:
                String atvPhone1 = atvPhone.getText().toString().trim();
                if (atvPhone1.equals("")||atvPhone1.length()<9){
                    Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "We have send you OTP", Toast.LENGTH_SHORT).show();
                    requestPasswordReset(atvPhone1);
                }
                break;
            case  R.id.btnResetpassword:
                String newpassword = atvnewPassword.getText().toString().trim();
                String pass = atvPassword.getText().toString().trim();
                String tvotp = tvOtp.getText().toString().trim();

                if (newpassword.equals("") ||newpassword.length()<3||pass.equals("") ||pass.length()<3||tvotp.equals("")||tvotp.length()<3){
                    Snackbar.make(mysnackbarView,"All fields are required ",Snackbar.LENGTH_LONG).show();
                }else {
                    if (newpassword.equals(pass)){
                        resetPasswordS(pass,tvotp);
                    }else {
                        Snackbar.make(mysnackbarView,"Your password are not matching",Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.tvToLogin:
                Intent intent = new Intent(resetpassword.this,login.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void  requestPasswordReset(final String phone){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait... we are processing your request");
        progressDialog.show();
        String url = URL_CONNECTOR+"resetpassword.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        System.out.println("RESETPASSWORD oooooo "+response);
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject result = array.getJSONObject(0);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                sharedPreference.setphonereset(phone);
                                enterPhonenumber.setVisibility(View.GONE);
                                resetpasswordLayout.setVisibility(View.VISIBLE);
                            }else{
                                Snackbar.make(mysnackbarView,result.getString("message"),Snackbar.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            //Snackbar.make(mysnackbarView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(resetpassword.this, "Internet connection problem", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("phone",phone);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void resetPasswordS(final String newpassword, final String otp){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("We are reseting your password");
        progressDialog.show();
        String url = URL_CONNECTOR+"changepassword.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        System.out.println("RESETPASSWORD oooooo "+response);
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject result = array.getJSONObject(0);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                AlertDialog.Builder mB =new AlertDialog.Builder(resetpassword.this);
                                mB.setMessage(result.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(resetpassword.this,login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }else{
                                Snackbar.make(mysnackbarView,result.getString("message"),Snackbar.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            //Snackbar.make(mysnackbarView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(resetpassword.this, "Internet connection problem", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("phone",sharedPreference.getphonereset());
                getParams.put("newpassword",newpassword);
                getParams.put("otp",otp);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }
}