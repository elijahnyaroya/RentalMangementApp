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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class addroom extends AppCompatActivity implements View.OnClickListener {

    EditText roomhouse,HouseName,roomNo,roomPrice,meterNo,roomCondition;
    Button submit;
    Spinner spinner,spinnerHouse;
    ArrayList<String> roomsSpinnerList,housenamespinnerlist;

    RelativeLayout mysnackbarView;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addroom);
        roomhouse = findViewById(R.id.roomhouse);
        HouseName = findViewById(R.id.HouseName);
        roomNo = findViewById(R.id.roomNo);
        roomPrice = findViewById(R.id.roomPrice);
        meterNo = findViewById(R.id.meterNo);
        roomCondition = findViewById(R.id.roomCondition);
        submit = findViewById(R.id.submit);
        mysnackbarView = findViewById(R.id.mysnackbarView);
        requestQueue = Volley.newRequestQueue(this);
        submit.setOnClickListener(this);

       //Spinner for rooms
        roomsSpinnerList=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.spinner);
        String urlroomHouse= URL_CONNECTOR+"pullfreeRoom.php";
        sharedMethod.pullSpinnerData(addroom.this,urlroomHouse, sharedPreference.getIsLoggedIn(),roomsSpinnerList ,spinner,"Addroom.java");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String room_house=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                roomhouse.setText(room_house);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        //Spinner for housename
        housenamespinnerlist=new ArrayList<>();
        spinnerHouse=(Spinner)findViewById(R.id.spinnerHouse);
        String urlhouses = URL_CONNECTOR+"pullhouses.php";
        sharedMethod.pullSpinnerData(addroom.this,urlhouses, sharedPreference.getIsLoggedIn(),housenamespinnerlist ,spinnerHouse,"Addroom.java");
        spinnerHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String housename=   spinnerHouse.getItemAtPosition(spinnerHouse.getSelectedItemPosition()).toString();

                HouseName.setText(housename);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
       CheckIfHouseHasBeenAdded();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                String roomhouse1=roomhouse.getText().toString().trim();
                String HouseName1=HouseName.getText().toString().trim();
                String roomNo1=roomNo.getText().toString().trim();
                String roomPrice1=roomPrice.getText().toString().trim();
                String meterNo1=meterNo.getText().toString().trim();
                String roomCondition1=roomCondition.getText().toString().trim();

                System.out.println("roomhouse1 "+roomhouse1+" HouseName1 "+HouseName1+" roomNo1 " +
                        ""+roomNo1+" roomPrice1 "+roomPrice1+" meterNo1 "+meterNo1+" roomCondition1 "+roomCondition1);
                if (roomhouse1.equals("") || roomhouse1.length()<1 ||HouseName1.equals("") || HouseName1.length()<1 ||roomNo1.equals("") || roomNo1.length()<1 ||
                        roomPrice1.equals("") || roomPrice1.length()<3 ||meterNo1.equals("") || meterNo1.length()<1 ||
                        roomCondition1.equals("") || roomCondition1.length()<5 ){
                    Snackbar.make(mysnackbarView,"Check your details and try again",Snackbar.LENGTH_LONG).show();
                }else{
                    if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                        Snackbar.make(mysnackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                                .setAction("Pay now ", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sharedMethod.makePayments("appUserPayment", addroom.this);
                                    }
                                }).show();
                    } else {
                        addRoomToTheHouse(roomhouse1, HouseName1, roomNo1, roomPrice1, meterNo1, roomCondition1);
                    }
                }
                break;
        }
    }

    public void addRoomToTheHouse(final String roomhouse1, final String HouseName1, final String roomNo1, final String roomPrice1, final String meterNo1, final String roomCondition1){
        String url = URL_CONNECTOR+"addRooms.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("addroom.java addRoomToTheHouse()"+response);
                            String requestType = result.getString("code");
                            if (requestType.equals("success")){
                                roomhouse.setText("");HouseName.setText("");roomNo.setText("");roomPrice.setText("");meterNo.setText("");roomCondition.setText("");
                                Snackbar.make(mysnackbarView,result.getString("message"),Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(addroom.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landloadID",sharedPreference.getIsLoggedIn());
                getParams.put("roomDescription",roomCondition1);
                getParams.put("roomName",roomNo1);
                getParams.put("houseName",HouseName1);
                getParams.put("price",roomPrice1);
                getParams.put("meterNo",meterNo1);
                getParams.put("houseType",roomhouse1);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void CheckIfHouseHasBeenAdded(){

        String url = URL_CONNECTOR+"checkiflandloadhashousetoaddroom.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("addroom.java CheckIfHouseHasBeenAdded() 9 "+response);
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("addroom.java CheckIfHouseHasBeenAdded()"+response);
                            if(result.getString("totalHouseNo").equals("0")){
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(addroom.this);
                                mBuilder.setMessage("You have no flats/building to add room. You have to add a building/house to add room")
                                        .setTitle("Add Building Name")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                sharedMethod.addBuilding(addroom.this);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent dash = new Intent(addroom.this,dashboard.class);
                                                startActivity(dash);
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            System.out.println("Error "+e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mysnackbarView,sharedMethod.genericVollyError(addroom.this,error),Snackbar.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                //TODO 5 remember to change landlord id to pick from the server
                getParams.put("landlordID",sharedPreference.getIsLoggedIn());
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }
}