package com.elisuntech.rentalmanagementapp2.MainContent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class addtenant extends AppCompatActivity implements View.OnClickListener {
    Spinner spinner;
    ArrayList<String> roomsSpinnerList;
    RelativeLayout mysnackbarView;
    RequestQueue requestQueue;
    EditText roomhouseNo,tenantIDNo,phone,fname,lname;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtenant);
        requestQueue = Volley.newRequestQueue(this);
        roomhouseNo= findViewById(R.id.roomhouseNo);
        lname= findViewById(R.id.lname);
        fname= findViewById(R.id.fname);
        tenantIDNo= findViewById(R.id.tenantIDNo);
        phone= findViewById(R.id.phone);
        submit= findViewById(R.id.submit);
        mysnackbarView= findViewById(R.id.mysnackbarView);

        //Spinner for rooms
        roomsSpinnerList=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.spinner);
        String urlroomHouse= URL_CONNECTOR+"getroomNo.php";
        sharedMethod.pullSpinnerData(addtenant.this,urlroomHouse, sharedPreference.getIsLoggedIn(),roomsSpinnerList ,spinner,"addtenant.java");
         spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String room_house=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                roomhouseNo.setText(room_house);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                String roomhouseNo1 = roomhouseNo.getText().toString().trim();
                String tenantIDNo1 = tenantIDNo.getText().toString().trim();
                String phone1 = phone.getText().toString().trim();
                String fname1 = fname.getText().toString().trim();
                String lname1 = lname.getText().toString().trim();
                if (roomhouseNo1.equals("")|| roomhouseNo1.length()<0 ||tenantIDNo1.equals("")|| tenantIDNo1.length()!=8 ||
                        phone1.equals("")|| phone1.length()<10 ||fname1.equals("")|| fname1.length()<3 ||
                        lname1.equals("")|| lname1.length()<3 ){
                    Snackbar.make(mysnackbarView,"Check your details and try again",Snackbar.LENGTH_LONG).show();
                }else{
                    if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                        Snackbar.make(mysnackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                                .setAction("Pay now ", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sharedMethod.makePayments("appUserPayment", addtenant.this);
                                    }
                                }).show();
                    } else {
                        addtenantToRoom(roomhouseNo1, tenantIDNo1, phone1, fname1, lname1);
                    }
                }


                break;
        }
    }

    public void addtenantToRoom(final String roomhouseNo1, final String tenantIDNo1, final String phone1, final String fname1, final String lname1){
        String url = URL_CONNECTOR+"addedTenants.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RESSS "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject result = jsonArray.getJSONObject(0);
                            System.out.println("addtenant.java addtenantToRoom()"+response);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(addtenant.this);
                                builder.setMessage(result.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(addtenant.this,addtenant.class);
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
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(addtenant.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                String[] splitroonHouse = roomhouseNo1.split(" : ", 2);
                getParams.put("landloadID",sharedPreference.getIsLoggedIn());
                getParams.put("roomNo",splitroonHouse[0]);
                getParams.put("HouseNo",splitroonHouse[1]);
                getParams.put("lname",lname1);
                getParams.put("fname",fname1);
                getParams.put("phone",phone1);
                getParams.put("idNo",tenantIDNo1);

                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

}