package com.elisuntech.rentalmanagementapp2.MainContent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.elisuntech.rentalmanagementapp2.Auths.login;
import com.elisuntech.rentalmanagementapp2.BuyandSell.BuyAndSell;
import com.elisuntech.rentalmanagementapp2.BuyandSell.SalesContact;
import com.elisuntech.rentalmanagementapp2.Messages.MessageContactList;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedMethod;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisuntech.rentalmanagementapp2.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener  {
    NavigationView navigationView;
    View v;
    TextView open;
    CardView noticetotenant,addrooms,addtenants,rentpayment,buyandsell,deliverytruckcardview,otherpaymentcardview,waterbilcardview;
    List<String> productList = new ArrayList<>();
    RequestQueue requestQueue;
    TextView appsubscriptionTv,totalTenantsTV,smstokenTV;
    RelativeLayout snackbarView,relative_tennt,relative_appsubscription,relative_smstoken;

    ArrayAdapter<String> adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Dashbord");
        setSupportActionBar(toolbar);

        productList.add("Electronic");
        productList.add("Software");
        productList.add("Cars");
        productList.add("Land");
        productList.add("Foods");

        requestQueue= Volley.newRequestQueue(this);

        Random rand = new Random();
        productList.get(rand.nextInt(productList.size()));
        sharedPreference.setproductSelected(productList.get(rand.nextInt(productList.size())));


        noticetotenant = findViewById(R.id.noticetotenant);
        buyandsell = findViewById(R.id.buyandsell);
        addrooms = findViewById(R.id.addrooms);
        addtenants = findViewById(R.id.addtenants);
        rentpayment = findViewById(R.id.rentpayment);
        waterbilcardview = findViewById(R.id.waterbilcardview);
        otherpaymentcardview = findViewById(R.id.otherpaymentcardview);
        deliverytruckcardview = findViewById(R.id.deliverytruckcardview);


        smstokenTV = findViewById(R.id.smstokenTV);
        totalTenantsTV = findViewById(R.id.totalTenantsTV);
        appsubscriptionTv = findViewById(R.id.appsubscriptionTv);
        snackbarView = findViewById(R.id.constraintLayout);

        relative_tennt = findViewById(R.id.relative_tennt);
        relative_appsubscription = findViewById(R.id.relative_appsubscription);
        relative_smstoken = findViewById(R.id.relative_smstoken);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(dashboard.this);
        v = navigationView.getHeaderView(0);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getLandlordData();
        //showing navigation drawer item based on user login previlleges
        JSONObject jsonObject = sharedPreference.getUSERDETAILS();
        Menu nav_Menu = navigationView.getMenu();
        TextView myphoneNumber = v.findViewById(R.id.myphoneNumber);
        TextView landlordName = v.findViewById(R.id.landlordName);
        ImageView imageView = v.findViewById(R.id.imageView);
        try {
            myphoneNumber.setText(jsonObject.getString("phoneNo"));
            landlordName.setText(jsonObject.getString("fname"));
            Glide.with(dashboard.this).load(jsonObject.getString("image")).into(imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_recents:
                        Intent dashboardAC = new Intent(dashboard.this,dashboard.class);
                        finish();
                        startActivity(dashboardAC);
                        break;
                    case R.id.action_favorites:
                        //TODO create chat layout for the contact from sales
                         Intent notification = new Intent(dashboard.this, SalesContact.class);
                         startActivity(notification);
                        break;
                    case R.id.action_nearby:
                        Intent messagesaC = new Intent(dashboard.this, MessageContactList.class);
                        startActivity(messagesaC);
                        break;

                }
                return true;
            }
        });
        noticetotenant.setOnClickListener(this);
        buyandsell.setOnClickListener(this);
        addrooms.setOnClickListener(this);
        addtenants.setOnClickListener(this);
        rentpayment.setOnClickListener(this);


        deliverytruckcardview.setOnClickListener(this);
        otherpaymentcardview.setOnClickListener(this);
        waterbilcardview.setOnClickListener(this);

        relative_tennt.setOnClickListener(this);
        relative_appsubscription.setOnClickListener(this);
        relative_smstoken.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:

                break;
            case R.id.addhouse:
                if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                    Snackbar.make(snackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                            .setAction("Pay now ", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sharedMethod.makePayments("appUserPayment", dashboard.this);
                                }
                            }).show();
                } else {
                    sharedMethod.addBuilding(dashboard.this);
                }
                break;
            case R.id.payment:

                break;
            case R.id.otherpayment:
                sharedMethod.addOtherPayments(dashboard.this);
            break;
            case R.id.nav_waterprice:
                sharedMethod.setwatepricePerUnit(dashboard.this);
            break;
            case R.id.nav_watermeterreading:
              startActivity(new Intent(dashboard.this,waterMeterReading.class));
                break;
            case R.id.notification:

                break;
                case R.id.userReport:

                    if (sharedMethod.getAppUsesAuthentication("appUse").equals("notpaid")) {
                        Snackbar.make(snackbarView, "You need to renew your subscription", Snackbar.LENGTH_LONG)
                                .setAction("Pay now ", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        sharedMethod.makePayments("appUserPayment", dashboard.this);
                                    }
                                }).show();
                    } else {
                        getMyTenatList();
                    }
                break;
            case R.id.nav_aboutus:
                Intent about = new Intent(dashboard.this, termsandconditionaboutus.class);
                about.putExtra("type","aboutus");
                startActivity(about);
                break;
            case R.id.nav_terms:
                Intent terms = new Intent(dashboard.this, termsandconditionaboutus.class);
                terms.putExtra("type","termsandcondition");
                startActivity(terms);
                break;
            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                JSONObject object = sharedPreference.getAUTHENTICATEAPPUSES();
                try {
                    String sharedBody  = object.getString("sharedBody");
                    String shareSubject = object.getString("shereSubject");
                    String applink =object.getString("applink");
                    share.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    share.putExtra(Intent.EXTRA_TEXT, sharedBody + " "+applink);
                    startActivity(Intent.createChooser(share, "Share App"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.nav_logut:
                sharedPreference.setIsLoggedIn("0");
                Intent logout = new Intent(dashboard.this, login.class);
                startActivity(logout);
                finish();
                break;

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.noticetotenant:
                startActivity(new Intent(dashboard.this,sendNotice.class));
                break;
            case R.id.addrooms:
                startActivity(new Intent(dashboard.this,addroom.class));
                break;
            case R.id.addtenants:
                startActivity(new Intent(dashboard.this,addtenant.class));
                break;
            case R.id.rentpayment:
                startActivity(new Intent(dashboard.this, TenantPayment.class));
                break;
            case R.id.buyandsell:
                startActivity(new Intent(dashboard.this, BuyAndSell.class));
                break;
            case R.id.waterbilcardview:
                startActivity(new Intent(dashboard.this, WaterBillPayment.class));
                break;
            case R.id.otherpaymentcardview:
                startActivity(new Intent(dashboard.this, OtherPayments.class));
                break;
            case R.id.deliverytruckcardview:
                startActivity(new Intent(dashboard.this, DeliveryTruck.class));
                break;
            case R.id.relative_appsubscription:
                Snackbar.make(snackbarView,"Your subscription has expired",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Pay Now", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sharedMethod.makePayments("appUserPayment", dashboard.this);
                            }
                        }).show();;
                break;
            case R.id.relative_tennt:
                AlertDialog.Builder smstoken = new AlertDialog.Builder(this);
                smstoken.setMessage("This shows the total number of tenant you have. You have a total of "+totalTenantsTV.getText().toString()+" Tenants.")
                        .setTitle("Your Total Tenants")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                break;
            case R.id.relative_smstoken:
                AlertDialog.Builder tenant = new AlertDialog.Builder(this);
                tenant.setMessage("These are sms token, that allows you to send sms to your tenants. If you want to topup your sms token, click below button to buy.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sharedMethod.makePayments("smstoken", dashboard.this);
                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();
                break;
        }
    }

    public void getLandlordData(){
        String url = URL_CONNECTOR+"getlandlorddetails.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("dashboard.java getlandlorddetails.php response "+response);
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("dashboard.java getlandlorddetails.php response "+response);
                            //appsubscriptionTv.setText(result.getString("appUse"));
                            sharedPreference.setAUTHENTICATEAPPUSES(result);
                            totalTenantsTV.setText(result.getString("totalTenant"));
                            smstokenTV.setText(result.getString("SmsToken"));
                            if (result.getString("appUse").equals("notpaid")){
                                appsubscriptionTv.setVisibility(View.VISIBLE);
                                Snackbar.make(snackbarView,"Your subscription has expired",Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Pay Now", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                sharedMethod.makePayments("appUserPayment", dashboard.this);
                                            }
                                        }).show();
                            }

                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Poor Internet", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landloadID",sharedPreference.getIsLoggedIn());
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void LoadCustomAlertDialog(ArrayList<String> tenantList){


        final android.app.AlertDialog dialogBuilder = new android.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.tenant_generate_report_listview, null);

        final EditText SearchTenant = (EditText) view.findViewById(R.id.SearchTenant);
        final ListView Tenatlists = (ListView) view.findViewById(R.id.Tenatlists);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tenantList);
        Tenatlists.setAdapter(adapter);

        SearchTenant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Tenatlists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
               System.out.println("Selected Item is = "+Tenatlists.getItemAtPosition(position));
               generateuserReport(Tenatlists.getItemAtPosition(position));
            }
        });

        dialogBuilder.setView(view);
        dialogBuilder.show();
    }

    public void getMyTenatList(){
        String url = URL_CONNECTOR+"getMyTenantList.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("dashboard.java getMyTenatList.php response "+response);
                        try {
                            ArrayList<String> mytenants = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            ArrayList<JSONObject> object = new ArrayList<>();
                            for (int i=0;i<array.length();i++){
                                JSONObject objects = array.getJSONObject(i);
                                mytenants.add(objects.getString("name")+objects.getString("phoneNo")+
                                        objects.getString("houseNo")+objects.getString("idNo"));
                            }

                            LoadCustomAlertDialog(mytenants);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(dashboard.this, "Poor Internet", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landloadID",sharedPreference.getIsLoggedIn());
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void  generateuserReport(Object tenantDetails){
        Toast.makeText(this, "Value Selected "+tenantDetails, Toast.LENGTH_SHORT).show();
    }

}