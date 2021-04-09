package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.fragments.CartCalculationFragment;
import com.dawabag.fragments.CartMedicinesFragment;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.tabs.TabLayout;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import android.app.Activity;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {

    TabLayout tabLayout;
    ViewPager viewPager;
    public static TextView tvTotal, tvCheckout;
    PrefManager prefManager;
    String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        prefManager = new PrefManager(getApplicationContext());

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ImageView ivSearch = (ImageView) findViewById(R.id.ivSearch);
        ImageView ivHome = (ImageView) findViewById(R.id.ivHome);

        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);

        //tvTotal.setText("₹" + Config.cartTotal);

        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Config.cartItemCount>0) {
                    if(Config.shippingAddressId!=0) {
                        try {
                            JSONArray jsonArray = new JSONArray(prefManager.getSelectedPrescriptions());
                            if(jsonArray.length()>0) {
                                placeOrder();
                            } else {
                                Toast.makeText(getApplicationContext(), "Ooppps..! Prescription is not selected.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), PrescriptionsActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Ooppps..! Prescription is not selected.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Ooppps..! Delivery address is not selected.", Toast.LENGTH_LONG).show();
                        viewPager.setCurrentItem(1);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Ooppps..! Cart can not be empty", Toast.LENGTH_LONG).show();
            }
        });
    }

    public JSONArray getItemsJson() {
        JSONArray jsonArray  = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("medicineId", 1);
            jsonObject.put("quantity", 1);
            jsonObject.put("medicineStrengthId", 1);
            jsonArray.put(jsonObject);
        } catch (Exception e) {
            Log.d("veer", e.getMessage() + "");
        }
        return jsonArray;
    }

    public JSONArray getSelectedPrescriptions() {
        JSONArray jsonArray  = null;
        try {
            jsonArray = new JSONArray(prefManager.getSelectedPrescriptions());
        } catch (Exception e) {
            Log.d("veer", e.getMessage() + "");
        }
        return jsonArray;
    }

    public void placeOrder() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("total", Config.cartTotal);
            object.put("userId", prefManager.getUserId() + "");
            object.put("gst", 0);
            object.put("discount", Config.discount);
            object.put("payable", Config.payable);
            object.put("shipping", Config.shipping);
            object.put("grossTotal", Config.cartTotal);
            object.put("couponDiscount", Config.couponDiscount);
            object.put("copounId", "Flat50");
            object.put("prescriptions", getSelectedPrescriptions());
            object.put("userAddressId", Config.shippingAddressId);
            object.put("items", getItemsJson());   //[{"medicineId":1, "quantity":5, "medicineStrengthId":1},  {"medicineId":11, "quantity":8, "medicineStrengthId":1}]
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("veer", object.toString());
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/place_order";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Config.cartTotal = 0;
                                Config.cartItemCount = 0;
                                Config.shippingAddress = "";
                                Config.shippingAddressId = 0;
//                                Config.cartTotalMRP = 0;

                                Config.discount = 0;
                                Config.couponDiscount = 0;
                                prefManager.setSelectedPrescriptions("");
                                orderId = result.getString("orderId");
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(getApplicationContext(), OrderResultActivity.class);
//                                startActivity(intent);
//                                finish();
                                startPayment(orderId, Config.payable);
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

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private static final int FIRST_TAB = 0;
        private static final int SECOND_TAB = 1;

        private int[] TABS;

        private Context mContext;

        public ViewPagerAdapter(final Context context, final FragmentManager fm) {
            super(fm);
            mContext = context.getApplicationContext();
            TABS = new int[]{FIRST_TAB, SECOND_TAB};
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch (TABS[position]) {
                case FIRST_TAB:
                    bundle.putString("param1","FIRST TAB");
                    CartMedicinesFragment medicinesFragment = new CartMedicinesFragment();
                    //medicinesFragment.setArguments(bundle);
                    return medicinesFragment;
                case SECOND_TAB:
                    bundle.putString("param1","SECOND TAB");
                    CartCalculationFragment calculationFragment = new CartCalculationFragment();
                    //medicinesFragment1.setArguments(bundle);
                    return calculationFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return TABS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (TABS[position]) {
                case FIRST_TAB:
                    return "Medicines";
                case SECOND_TAB:
                    return "Details";
            }
            return null;
        }
    }

    public void startPayment(String orderId, double payable) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID("rzp_test_uKiKQRMLhf7rbe");
//        co.setKeyID("rzp_test_uKiKQRMLhf7rbe");
        co.setImage(R.drawable.logo);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Dawabag Pvt. Ltd.");
            options.put("description", "Your order id is " + orderId);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount",  (payable * 100));

            JSONObject preFill = new JSONObject();
            preFill.put("email", "payment@dawabag.com");
            preFill.put("contact", "" + prefManager.getPhone());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
            Log.e("veer", e.getMessage() + "");
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Config.payable = 0;
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            Log.e("veer", "Payment Successful: " + razorpayPaymentID);
            updateTransactionDetails(razorpayPaymentID);
        } catch (Exception e) {
            Log.e("veer", "Exception in onPaymentSuccess", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Config.payable = 0;
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            Log.e("veer", "Payment failed: " + code + " " + response);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("veer", "Exception in onPaymentError", e);
        }
    }

    public void updateTransactionDetails(String transactionId) {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId() + "");
            object.put("orderId", orderId + "");
            object.put("transactionId", transactionId + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("veer", object.toString());
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/update_transaction_details";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Intent intent = new Intent(getApplicationContext(), OrderResultActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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