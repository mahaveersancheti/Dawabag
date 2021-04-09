package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dawabag.R;
import com.dawabag.beans.Address;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddAddressActivity extends AppCompatActivity {

    boolean isUpdate = false;
    int addressId = 0;
    String addressType = "Home";
    PrefManager prefManager;
    ChipGroup chipGroup;
    TextInputEditText tvDeliverTo, tvPhone, tvAddressLine1, tvAddressLine2, tvAddressLine3, tvCity, tvState, tvPincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        prefManager = new PrefManager(getApplicationContext());

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ImageView ivSearch = (ImageView) findViewById(R.id.ivSearch);
        ImageView ivCart = (ImageView) findViewById(R.id.ivCart);

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        TextView tvCartCnt = (TextView) findViewById(R.id.tvCartCnt);
        tvCartCnt.setText(Config.cartItemCount + "");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), AddressesActivity.class);
//                startActivity(intent);
            }
        });

        tvDeliverTo = (TextInputEditText) findViewById(R.id.tvDeliverTo);
        tvPhone = (TextInputEditText) findViewById(R.id.tvPhone);
        tvAddressLine1 = (TextInputEditText) findViewById(R.id.tvAddressLine1);
        tvAddressLine2 = (TextInputEditText) findViewById(R.id.tvAddressLine2);
        tvAddressLine3 = (TextInputEditText) findViewById(R.id.tvAddressLine3);
        tvCity = (TextInputEditText) findViewById(R.id.tvCity);
        tvState = (TextInputEditText) findViewById(R.id.tvState);
        tvPincode = (TextInputEditText) findViewById(R.id.tvPincode);

        chipGroup = (ChipGroup) findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = chipGroup.findViewById(checkedId);
                addressType = chip.getText().toString();
            }
        });

        if(getIntent().hasExtra("address")) {
            Address address = (Address) getIntent().getSerializableExtra("address");
            addressId = address.getId();
            tvDeliverTo.setText(address.getName() + "");
            tvPhone.setText(address.getPhone() + "");
            tvAddressLine1.setText(address.getAddrLine1() + "");
            tvAddressLine2.setText(address.getAddrLine2() + "");
            tvAddressLine3.setText(address.getAddrLine3() + "");
            tvCity.setText(address.getCity() + "");
            tvState.setText(address.getState() + "");
            tvPincode.setText(address.getPincode() + "");
            isUpdate = true;
        }
    }

    public void submit(View view) {
        if(isUpdate) {
            updateAddress();
        } else {
            addAddress();
        }

    }

    public void updateAddress() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId() + "");
            object.put("addressLine1", tvAddressLine1.getText().toString().trim());
            object.put("addressLine2", tvAddressLine2.getText().toString().trim());
            object.put("addressLine3", tvAddressLine3.getText().toString().trim());
            object.put("city", tvCity.getText().toString().trim());
            object.put("state", tvState.getText().toString().trim());
            object.put("pincode", tvPincode.getText().toString().trim());
            object.put("country", "India");
            object.put("lat", "");
            object.put("lng", "");
            object.put("name", tvDeliverTo.getText().toString().trim() + "");
            object.put("phone", tvPhone.getText().toString().trim() + "");
            object.put("addressType", addressType + "");
            object.put("addressId", addressId + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/user_address_update";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(getApplicationContext(), AddressesActivity.class);
//                                startActivity(intent);
//                                finish();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.d("veer error", e.getMessage() + "");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("veer error", error.getMessage() + "");
            }
        });
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void addAddress() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId() + "");
            object.put("addressLine1", tvAddressLine1.getText().toString().trim());
            object.put("addressLine2", tvAddressLine2.getText().toString().trim());
            object.put("addressLine3", tvAddressLine3.getText().toString().trim());
            object.put("city", tvCity.getText().toString().trim());
            object.put("state", tvState.getText().toString().trim());
            object.put("pincode", tvPincode.getText().toString().trim());
            object.put("country", "India");
            object.put("lat", "");
            object.put("lng", "");
            object.put("name", tvDeliverTo.getText().toString().trim() + "");
            object.put("phone", tvPhone.getText().toString().trim() + "");
            object.put("addressType", addressType + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/user_address_insert";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(getApplicationContext(), AddressesActivity.class);
//                                startActivity(intent);
//                                finish();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.d("veer error", e.getMessage() + "");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("veer error", error.getMessage() + "");
            }
        });
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}