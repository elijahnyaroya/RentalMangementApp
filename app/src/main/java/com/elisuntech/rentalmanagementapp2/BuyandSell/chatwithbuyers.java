package com.elisuntech.rentalmanagementapp2.BuyandSell;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.CA.chatwithbuyersCA;
import com.elisuntech.rentalmanagementapp2.DMC.messagechatDMC;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class chatwithbuyers extends AppCompatActivity implements chatwithbuyersCA.OnItemClickListener, View.OnClickListener  {
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private ArrayList<messagechatDMC> tenantList;
    private chatwithbuyersCA mAdapter;
    ImageView sendMessage;
    EditText et_sendmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatwithbuyers);


        et_sendmessage = findViewById(R.id.et_sendmessage);
        sendMessage = findViewById(R.id.sendMessage);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(chatwithbuyers.this));
        recyclerView.setHasFixedSize(true);
        tenantList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);

        sendMessage.setOnClickListener(this);
        fetchMessages();
        updeteMessageReadStatus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendMessage:
                String etMessage = et_sendmessage.getText().toString().trim();
                if (etMessage.equals("")||etMessage.length()<2){
                    Toast.makeText(this, "Message is to short", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessageToserver(etMessage);
                }
                break;
        }
    }

    private void fetchMessages() {
        String url = URL_CONNECTOR+"pullotherNotificationChat.php";

        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPP "+response);
                        try {

                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response "+response);
                            for (int i=0;i<responses.length();i++){
                                JSONObject hit = responses.getJSONObject(i);
                                String tenantID = hit.getString("tenantID");
                                String messageID = hit.getString("messageID");
                                String message = hit.getString("message");
                                String sender = hit.getString("sender");

                                tenantList.add(new messagechatDMC(tenantID,messageID,message,sender));


                            }
                            mAdapter = new chatwithbuyersCA( chatwithbuyers.this,tenantList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(chatwithbuyers.this);
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
                try {
                    params.put("phoneno", sharedPreference.getUSERDETAILS().getString("phoneNo"));
                    params.put("receiverphone", sharedPreference.getChatcontact() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onItemClick(int position) {
        messagechatDMC pos = tenantList.get(position);

        String message = pos.getMessage();
        String messageID = pos.getMessageID();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(chatwithbuyers.this);
        mBuilder.setMessage(message)
                .setCancelable(true)
                .create()
                .show();

    }
    public void  sendMessageToserver(final String etMessage){
        String url = URL_CONNECTOR+"sendMessagetoSalescontact.php";

        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response "+response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");
                            if (result.equals("success")){
                                Intent intent = new Intent(chatwithbuyers.this,chatwithbuyers.class);
                                startActivity(intent);
                                finish();
                            }

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
                try {
                    params.put("phoneno", sharedPreference.getUSERDETAILS().getString("phoneNo"));
                    params.put("receiverphone", sharedPreference.getChatcontact() );
                    params.put("message", etMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }

    public void  updeteMessageReadStatus(){
        String url = URL_CONNECTOR+"updateMessageSales.php";

        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){

                        try {

                            JSONArray responses = new JSONArray(response);
                            System.out.println("Response "+response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");
                            if (result.equals("success")){

                            }

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
                //params.put("landloadID", sharedPreference.getIsLoggedIn());
                //params.put("tenantID", sharedPreference.getChatcontact() );

                try {
                    params.put("phoneno", sharedPreference.getUSERDETAILS().getString("phoneNo"));
                    params.put("receiverphone", sharedPreference.getChatcontact() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }

}