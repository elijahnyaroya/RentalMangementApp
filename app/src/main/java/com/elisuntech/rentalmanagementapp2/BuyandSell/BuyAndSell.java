package com.elisuntech.rentalmanagementapp2.BuyandSell;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.elisuntech.rentalmanagementapp2.CA.buysellCA;
import com.elisuntech.rentalmanagementapp2.DMC.buysellDMC;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyApplication;
import com.elisuntech.rentalmanagementapp2.commonMethods.MyDividerItemDecoration;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.elisuntech.rentalmanagementapp2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class BuyAndSell extends AppCompatActivity implements buysellCA.TenantssAdapterListener  {
    private static final String TAG = BuyAndSell.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<buysellDMC> tenantList;
    private buysellCA mAdapter;
    private SearchView searchView;
    RelativeLayout relativelayoutDisplayNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_and_sell);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (sharedPreference.getproductSelected()) {
            case "Electronic":
                getSupportActionBar().setTitle("Electronic ");
                break;
            case "Software":
                getSupportActionBar().setTitle("Software ");
                break;
            case "Cars":
                getSupportActionBar().setTitle("Cars ");
                break;
            case "Land":
                getSupportActionBar().setTitle("Land ");
                break;
            case "Foods":
                getSupportActionBar().setTitle("Foods ");
                break;

        }
        recyclerView = findViewById(R.id.recycler_view);
        relativelayoutDisplayNothing = findViewById(R.id.relativelayoutDisplayNothing);
        tenantList = new ArrayList<>();
        mAdapter = new buysellCA(this, tenantList, this);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchProduct();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_buysel);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Software:
                        sharedPreference.setproductSelected("Software");
                        Intent dashboardAC = new Intent(BuyAndSell.this,BuyAndSell.class);
                        finish();
                        startActivity(dashboardAC);
                        break;
                    case R.id.land:
                        sharedPreference.setproductSelected("Land");
                        Intent notification = new Intent(BuyAndSell.this,BuyAndSell.class);
                        startActivity(notification);
                        finish();
                        break;
                    case R.id.foodstaffs:
                        sharedPreference.setproductSelected("Foods");
                        Intent messagesaC = new Intent(BuyAndSell.this, BuyAndSell.class);
                        startActivity(messagesaC);
                        finish();
                        break;
                    case R.id.Electronics:
                        sharedPreference.setproductSelected("Electronic");
                        Intent Electronic = new Intent(BuyAndSell.this,BuyAndSell.class);
                        startActivity(Electronic);
                        finish();
                        break;
                    case R.id.Cars:
                        sharedPreference.setproductSelected("Cars");
                        Intent cars = new Intent(BuyAndSell.this,BuyAndSell.class);
                        startActivity(cars);
                        finish();
                        break;

                }
                return true;
            }
        });

    }
    private void fetchProduct() {
        String URL = URL_CONNECTOR+"pullproducts.php";

        StringRequest request = new StringRequest(StringRequest.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("REsponse fetchProduct "+response);
                        if (response.length()<5){
                            recyclerView.setVisibility(View.GONE);
                            relativelayoutDisplayNothing.setVisibility(View.VISIBLE);
                        }
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<buysellDMC> items = new Gson().fromJson(response.toString(), new TypeToken<List<buysellDMC>>() {
                        }.getType());

                        // adding contacts to contacts list
                        tenantList.clear();
                        tenantList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error:  " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<String, String>();
                params.put("product", sharedPreference.getproductSelected());
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onTenantSelected(final buysellDMC tenant) {
       //Toast.makeText(getApplicationContext(), "Selected: " + tenant.getPrice() + ", " + tenant.getName(), Toast.LENGTH_LONG).show();

        JSONObject object = new JSONObject();
        try {
            object.put("sellerPhone",tenant.getSellerphone());
            object.put("productID",tenant.getProductID());
            object.put("price",tenant.getPrice());
            object.put("desc",tenant.getDescription());
            object.put("paidfor",tenant.getPaidfor());
            object.put("location",tenant.getLocation());
            object.put("date",tenant.getTime());
            object.put("productname",tenant.getName());
            object.put("productIMage",tenant.getImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
       sharedPreference.setProductDetails(object);
        startActivity(new Intent(BuyAndSell.this,ProductDetailspage.class));

    }

}