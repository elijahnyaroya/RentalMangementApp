package com.elisuntech.rentalmanagementapp2.Auths;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.MainContent.dashboard;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class login extends AppCompatActivity  implements View.OnClickListener{
    Button btnSignIn;
    int validator = 0,validator2 = 0;
    EditText atvPhoneNo,atvPassword;
    RelativeLayout mysnackbarView;
    RequestQueue requestQueue;
    TextView tvToRegister,tvForgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        requestQueue = Volley.newRequestQueue(this);
        atvPhoneNo = findViewById(R.id.atvPhoneNo);
        atvPassword = findViewById(R.id.atvPassword);
        mysnackbarView = findViewById(R.id.mysnackbarView);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvToRegister = findViewById(R.id.tvToRegister);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        btnSignIn.setOnClickListener(this);
        tvToRegister.setOnClickListener(this);
        tvForgotPass.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSignIn:
                String phone = atvPhoneNo.getText().toString().trim();
                String password = atvPassword.getText().toString().trim();
                loginCredentialSanitizer(phone,password);
                break;
            case R.id.tvToRegister:
                startActivity(new Intent(login.this,register.class));
                break;
            case R.id.tvForgotPass:
              startActivity(new Intent(login.this,resetpassword.class));
            break;
        }
    }

    public void loginCredentialSanitizer(String phone,String password){

        if (phone.equals("")|| phone.length()<10){
            atvPhoneNo.setError("Valid phone number is required");
            validator = 0;
        }else{
            validator = 1;
        }

        if (password.equals("")||  password.length()<3 ){
            atvPassword.setError("Valid password is required");
            validator2 = 0;
        }else{
            validator2 = 1;
        }

        if (validator+validator2 == 2){
            loginUsers(phone,password);
        }else{
            Toast.makeText(this, "Noop", Toast.LENGTH_SHORT).show();
        }


    }


    public void loginUsers(final String phone, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait... we are authenticating you to your account");
        progressDialog.show();
        String url = URL_CONNECTOR+"login.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("login.java loginUsers()"+response);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                sharedPreference mysessions = new sharedPreference(login.this);
                                mysessions.setIsLoggedIn(result.getString("idno"));
                                sharedPreference.setUSERDETAILS(result);
                                   progressDialog.dismiss();
                                Intent intent = new Intent(login.this, dashboard.class);
                                startActivity(intent);
                                finish();

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
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(login.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("phone",phone);
                getParams.put("password",password);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }
}