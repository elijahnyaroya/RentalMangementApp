package com.elisuntech.rentalmanagementapp2.commonMethods;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elisuntech.rentalmanagementapp2.MainContent.addroom;
import com.elisuntech.rentalmanagementapp2.R;
import com.elisuntech.rentalmanagementapp2.payment_folder.m_pesa;
//import com.crypsol.rentalmanagementapp.payment_folder.paypalPayment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.elisuntech.rentalmanagementapp2.connection.connect.URL_CONNECTOR;

public class sharedMethod {


     public static String genericVollyError(Context context, VolleyError error){
            String text = "";
          if (error instanceof TimeoutError) {
               text = " The Internet has timeout errors";
          }else if (error instanceof NoConnectionError){
               text = " No network Connection";
          }else if (error instanceof AuthFailureError){
               text = " Error Authenticating";
          }else if (error instanceof ServerError){
               text = "There is an error on the Server side, please try again later. The system reports this error automatically";
          }else if (error instanceof NetworkError){
               text = "Please check your internet connection";
          }else {
               text = " Unexpected error occurred";
          }

          return text;
     }

     public static JSONObject ResponseFromServer(String response,int objectIndex) throws JSONException {
          JSONObject jsonObject = null;
          try {
               JSONArray jsonArray = new JSONArray(response);
               jsonObject = jsonArray.getJSONObject(objectIndex);

          }catch (JSONException e){
               jsonObject.put("jsonError",e.getMessage());
          }

          return  jsonObject;
     }

  public static void pullSpinnerData(final Context context, String url, final String landlordID, final ArrayList<String> arrayListType , final Spinner spinner2, final String activityname){

            RequestQueue requestQueue= Volley.newRequestQueue(context);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {

                      try{
                           System.out.println("sharedMethod.java pullSpinnerData() "+response);
                           if (response.length()<5){

                               if (activityname.equals("addtenant.java")){
                                   AlertActivity(context,activityname,"No available rooms to add tenant");
                               }else if (activityname.equals("Addroom.java")){
                                   //AlertActivity(context,activityname,"");
                               }
                           }
                           JSONArray responses = new JSONArray(response);

                           for (int i=0;i<responses.length();i++){
                                JSONObject responseObject= responses.getJSONObject(i);
                               String name = responseObject.getString("data");

                                arrayListType.add(name);
                           }
                           spinner2.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayListType));

                          /* switch (spinnerType){
                                case "spinner":
                                     spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayListType));
                                     break;
                                case "spinner2":
                                     spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayListType));
                                     break;
                           }*/


                      }catch (JSONException e){e.printStackTrace();}
                 }
            }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                      error.printStackTrace();
                 }
            })
            {
                 @Override
                 protected Map<String, String> getParams() throws AuthFailureError {
                      Map<String, String> map = new HashMap<String, String>();
                      map.put("landlordID", landlordID);
                      return map;
                 }
            };

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);

       }

       public static void AlertActivity(final Context context, String activityname, String message){
         AlertDialog.Builder mB = new AlertDialog.Builder(context);
         mB.setMessage(message)
                 .setPositiveButton("Add room first", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         Intent intent = new Intent(context,addroom.class);
                         context.startActivity(intent);
                     }
                 })
                 .setCancelable(false)
                 .create()
                 .show();
       }

       public static void addBuilding(final Context context){
           final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
           LayoutInflater inflater = LayoutInflater.from(context);
           final View view = inflater.inflate(R.layout.addhouse_building, null);

           final EditText buildingname = (EditText) view.findViewById(R.id.buildingname);
           final EditText county = (EditText) view.findViewById(R.id.county);
           final EditText Town = (EditText) view.findViewById(R.id.Town);
           final EditText placename = (EditText) view.findViewById(R.id.placename);
           final Button addHouseBtn = (Button) view.findViewById(R.id.addHouseBtn);
           county.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   showCounties(context,county);
               }
           });
           addHouseBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String buildingname1 = buildingname.getText().toString().trim();
                   String county1 = county.getText().toString().trim();
                   String Town1 = Town.getText().toString().trim();
                   String placename1 = placename.getText().toString().trim();

                   if (buildingname1.length()<3 ||county1.length()<3 || Town1.length()<3||placename1.length()<3){
                       Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                   }else{
                       addBuldingToDatabase(context,buildingname1,county1, Town1,placename1);
                       buildingname.setText("");
                       county.setText("");
                       Town.setText("");
                       placename.setText("");
                   }
               }
           });

           dialogBuilder.setView(view);
           dialogBuilder.show();
       }
    public  static void addBuldingToDatabase(final Context context, final String buildingname1, final String county1, final String Town1, final String placename1){
         RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = URL_CONNECTOR+"addhouse_building.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                            System.out.println("addroom.java addBuldingToDatabase()"+response);
                            Toast.makeText(context,response,Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landlordID",sharedPreference.getIsLoggedIn());
                getParams.put("buildingName",buildingname1);
                getParams.put("county",county1);
                getParams.put("town",Town1);
                getParams.put("placename",placename1);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static  void showCounties(Context context,final EditText county){
        final CharSequence[] items =
                {"Nairobi", "Mombasa","Kisumu","Kwale", "Kilifi", "Tana River", "Lamu", "Taita/Taveta", "Garissa", "Wajir", "Mandera",
                "Marsabit", "Isiolo", "Meru", "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua", "Nyeri",
                "Kirinyaga","Murang'a", "Kiambu", "Turkana", "West Pokot", "Samburu", "Trans Nzoia", "Uasin Gishu", "Elgeyo/Marakwet",
                "Nandi", "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho", "Bomet", "Kakamega", "Vihiga", "Bungoma",
                "Busia", "Siaya",  "Homa Bay", "Migori", "Kisii", "Nyamira"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select County");
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        county.setText(items[which]);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    public static String getAppUsesAuthentication(String valuetoBereturned){
        JSONObject jsonObject =  sharedPreference.getAUTHENTICATEAPPUSES();

        try {
            return jsonObject.getString(valuetoBereturned);
        } catch (JSONException e) {
            e.printStackTrace();
            return valuetoBereturned;
        }
    }

    public static void makePayments(final String typeofpayment, final Context context){
         RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = URL_CONNECTOR+"pullappPaymentprice.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("makePayments.java getMyTenatList.php response "+response);
                        try {
                             JSONArray array = new JSONArray(response);
                             JSONObject object = array.getJSONObject(0);

                             processPayment(context,typeofpayment,object.getString("price"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(co, "Poor Internet", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("typeofpayment",typeofpayment);
                return param;
             }
        } ;
        requestQueue.add(stringRequest);
    }

    public static  void processPayment(final Context context, final String typeofpayment, final String price){
        //smstoken
        //appUserPayment
        class StoreValue {
            int value;
            public void set(int i) {
                value = i;
            }
            public int get() {
                return value;
            }
        }

        final StoreValue valueselected = new StoreValue();
        String[] paymentmethodslist = {"M-PESA"};//String[] paymentmethodslist = {"M-PESA","PAYPAL"};
        androidx.appcompat.app.AlertDialog.Builder mBuilder;
        mBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
        mBuilder.setTitle("Payment");


        mBuilder.setSingleChoiceItems(paymentmethodslist, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueselected.set(which);
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                which = valueselected.get();
                switch (which) {
                    case 0:
                        m_pesa.mPesaPayment(context,typeofpayment,price);
                        break;
                 /*   case 1:
                        Intent intent = new Intent(context, paypalPayment.class);
                        intent.putExtra("amount",price);
                        intent.putExtra("userID",sharedPreference.getIsLoggedIn());
                        intent.putExtra("typeofpayment",typeofpayment);
                        context.startActivity(intent);
                        break;*/
                    default:
                        break;
                }
            }
        }).setCancelable(true);
        mBuilder.create();
        mBuilder.show();

    }

   //Clean phone number
    public static  String cleanPhoneNumber(String phone){

        if (phone.length() > 9){
            return  "254"+phone.substring(phone.length() - 9);
        } else{
            return  "254"+phone;
        }

    }

    public static void setwatepricePerUnit(final Context context){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.setwaterprice, null);

        final EditText waterpriceperunit = (EditText) view.findViewById(R.id.waterpriceperunit);
        final Button addHouseBtn = (Button) view.findViewById(R.id.addHouseBtn);

        addHouseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String waterpriceperunit1 = waterpriceperunit.getText().toString().trim();
                if (waterpriceperunit1.length()<2 ){
                    Toast.makeText(context, "Water price can not be less than ksh.10", Toast.LENGTH_SHORT).show();
                }else{
                    addwaterPriceToDatabase(context,waterpriceperunit1,dialogBuilder);
                    waterpriceperunit.setText("");
                }
            }
        });

        dialogBuilder.setView(view);
        dialogBuilder.show();
    }

    public  static void addwaterPriceToDatabase(final Context context, final String waterpriceperunit, final AlertDialog dialogBuilder){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = URL_CONNECTOR+"addpriceperunit.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("addwaterPriceToDatabase()"+response);
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        dialogBuilder.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                dialogBuilder.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landlordID",sharedPreference.getIsLoggedIn());
                getParams.put("waterpriceperunit",waterpriceperunit);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }


    public static  void updateUserDataAfterUpadates(Context context){
         RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = URL_CONNECTOR+"getlandlorddetails.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = sharedMethod.ResponseFromServer(response,0);
                            System.out.println("dashboard.java getlandlorddetails.php response "+response);
                            sharedPreference.setAUTHENTICATEAPPUSES(result);


                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
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

    public static void addOtherPayments(final Context context){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.addnewotherpayment, null);

        final EditText typeeofpayment = (EditText) view.findViewById(R.id.typeeofpayment);
        final EditText pricetobepaid = (EditText) view.findViewById(R.id.pricetobepaid);
        final Button btnAdd = (Button) view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeeofpayment1 = typeeofpayment.getText().toString().trim();
                String pricetobepaid1 = pricetobepaid.getText().toString().trim();
                if (pricetobepaid.length()<2 ||pricetobepaid1.equals("")||typeeofpayment1.equals("")||typeeofpayment1.length()<4){
                    Toast.makeText(context, "Something is not right,Check your data and try again", Toast.LENGTH_SHORT).show();
                }else{
                    addNewOtherpayment(context,pricetobepaid1,typeeofpayment1,dialogBuilder);
                }
            }
        });

        dialogBuilder.setView(view);
        dialogBuilder.show();
    }

    public  static void addNewOtherpayment(final Context context, final String pricetobepaid,final String typeeofpayment, final AlertDialog dialogBuilder){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = URL_CONNECTOR+"addnewtypesofPayment.php";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("addwaterPriceToDatabase()"+response);
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        dialogBuilder.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                dialogBuilder.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> getParams = new HashMap<>();
                getParams.put("landlordID",sharedPreference.getIsLoggedIn());
                getParams.put("pricetobepaid",pricetobepaid);
                getParams.put("typeeofpayment",typeeofpayment);
                return getParams;
            }
        };
        requestQueue.add(stringRequest);
    }

}
