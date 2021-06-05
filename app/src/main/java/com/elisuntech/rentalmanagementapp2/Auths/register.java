package com.elisuntech.rentalmanagementapp2.Auths;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class register extends AppCompatActivity  implements View.OnClickListener{

    EditText atvfname,atvlname,atvPhone,atvPasswordReg,atvPasswordReg2;
    Button btnSignUp;
    TextView tvSignIn;
    RelativeLayout mysnackbarView;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        requestQueue = Volley.newRequestQueue(this);
        atvfname = findViewById(R.id.atvFname);
        atvlname = findViewById(R.id.atvLname);
        atvPhone = findViewById(R.id.atvPhone);
        atvPasswordReg = findViewById(R.id.atvPasswordReg);
        atvPasswordReg2 = findViewById(R.id.atvPasswordReg2);

        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
        mysnackbarView = findViewById(R.id.mysnackbarView);

        btnSignUp.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.tvSignIn:
                Intent intent = new Intent(register.this,login.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSignUp:
                String fname = atvfname.getText().toString().trim();
                String lname = atvlname.getText().toString().trim();
                String phone = atvPhone.getText().toString().trim();
                String password = atvPasswordReg.getText().toString().trim();
                String pass2 = atvPasswordReg2.getText().toString().trim();

                if (fname.equals("") || fname.length()<2 || lname.equals("") || lname.length()<2 ||phone.equals("") || phone.length()<9 ||
                        password.equals("") || password.length()<3 ||pass2.equals("") || pass2.length()<3 ){
                    Snackbar.make(mysnackbarView,"All fields are required",Snackbar.LENGTH_LONG).show();
                }else{
                   if (password.equals(pass2)){
                       registerUser(fname,lname,phone,password);
                   }else{
                       Snackbar.make(mysnackbarView,"Your password are not matching",Snackbar.LENGTH_LONG).show();
                       atvPasswordReg.setError("");
                       atvPasswordReg2.setError("");
                   }
                }
                break;
        }
    }


    public void registerUser(final String fname, final String lname, final String phone, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait... we are adding your details to the system");
        progressDialog.show();

        String url = URL_CONNECTOR+"register.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("login.java loginUsers()"+response);
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);

                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder mB =new android.app.AlertDialog.Builder(register.this);
                                mB.setMessage(result.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(register.this, login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();

                            }else if (requestType.equals("recoverAcount")){
                                progressDialog.dismiss();
                                recoverAccount(result.getString("message"));
                            }else if (requestType.equals("fail")){
                                progressDialog.dismiss();
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(register.this);
                                mBuilder.setMessage(result.getString("message"))
                                         .setCancelable(true)
                                        .create()
                                        .show();
                            }else{
                                progressDialog.dismiss();
                                Snackbar.make(mysnackbarView,result.getString("message"),Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //Snackbar.make(mysnackbarView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(register.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("phone",phone);
                getParams.put("lname",lname);
                getParams.put("fname",fname);
                getParams.put("password",password);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void recoverAccount(String message){
        AlertDialog.Builder mBuilders = new AlertDialog.Builder(register.this);
        mBuilders.setTitle("**** Recover Account ****")
                 .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(mysnackbarView,"Recover Account Function here",Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(register.this,register.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}