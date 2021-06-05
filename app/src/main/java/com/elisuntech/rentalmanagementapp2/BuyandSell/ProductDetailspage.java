package com.elisuntech.rentalmanagementapp2.BuyandSell;

import android.app.AlertDialog;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisuntech.rentalmanagementapp2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class ProductDetailspage extends AppCompatActivity implements View.OnClickListener{

    ImageView productImage;
    TextView productName,datepublished,Location,productPrice,Description;
    RelativeLayout openchat;
    CoordinatorLayout snackbarView;

    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        datepublished = findViewById(R.id.datepublished);
        Location = findViewById(R.id.Location);
        productPrice = findViewById(R.id.productPrice);
        Description = findViewById(R.id.Description);
        openchat = findViewById(R.id.openchat);
        snackbarView = findViewById(R.id.snackbarView);

        requestQueue = Volley.newRequestQueue(this);

        try {
            productName.setText(sharedPreference.getProductDetails().getString("productname"));
            datepublished.setText(sharedPreference.getProductDetails().getString("date"));
            Location.setText(sharedPreference.getProductDetails().getString("location"));
            productPrice.setText("Price : Ksh "+sharedPreference.getProductDetails().getString("price"));
            Description.setText(sharedPreference.getProductDetails().getString("desc"));

            Glide.with(ProductDetailspage.this).load(sharedPreference.getProductDetails().getString("productIMage")).into(productImage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        openchat.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.openchat:
               // Toast.makeText(this, "We will create a direct chat to the saller", Toast.LENGTH_SHORT).show();

                final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
                LayoutInflater inflater = LayoutInflater.from(this);
                final View view2 = inflater.inflate(R.layout.contact_productseller, null);

                final EditText message = (EditText) view2.findViewById(R.id.message);
                final Button submits = (Button) view2.findViewById(R.id.submit);
                submits.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         String messages = message.getText().toString().trim();

                         if (messages.equals("")||messages.length()<5){
                             Snackbar.make(snackbarView,"All fields are required",Snackbar.LENGTH_LONG).show();
                         }else{
                             dialogBuilder.dismiss();
                             try {
                                 SendMessageToSeller(sharedPreference.getProductDetails().getString("sellerPhone"), messages);
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }
                     }
                 });
                dialogBuilder.setView(view2);
                dialogBuilder.show();
                break;
        }
    }

    public void SendMessageToSeller(final String sellerPhone, final String message) {
        String url = URL_CONNECTOR + "sendMessagetoseller.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("ProductDetailspage.java SendMessageToSeller() response " + response);
                            JSONArray array =new JSONArray(response);
                            JSONObject result = array.getJSONObject(0);

                            if (result.getString("code").equals("success")) {
                                Snackbar.make(snackbarView, result.getString("message"), Snackbar.LENGTH_LONG)
                                       .show();
                            }

                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductDetailspage.this, "Poor Internet", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> getParams = new HashMap<>();
                try {
                    getParams.put("buyerphone", sharedPreference.getUSERDETAILS().getString("phoneNo"));
                    getParams.put("sellerPhone", sellerPhone);
                    getParams.put("message", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return getParams;
            }
        };
        requestQueue.add(stringRequest);

    }
}