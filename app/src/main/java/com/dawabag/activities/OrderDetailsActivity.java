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
import com.dawabag.activities.returnmedicine.ReturnDetailsActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Order;
import com.dawabag.fragments.CartCalculationFragment;
import com.dawabag.fragments.CartMedicinesFragment;
import com.dawabag.fragments.OrderDetailsCalculationFragment;
import com.dawabag.fragments.OrderDetailsMedicineFragment;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.tabs.TabLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Order order;
    public static TextView tvTotal, tvCheckout;
    PrefManager prefManager;
    TextView tvCancel, tvReturnDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        prefManager = new PrefManager(getApplicationContext());

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        order = (Order) getIntent().getSerializableExtra("order");

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
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

        tvReturnDetails = (TextView) findViewById(R.id.tvReturnDetails);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        if(order.getStatus()==0 || order.getStatus()==1 || order.getStatus()==2 || order.getStatus()==3) {
            // you are allow to cancel
        } else {
            // order is already dispatched. you can not cancel now.
            tvCancel.setVisibility(View.GONE);
        }
        if(order.getStatus()==7 || (order.getStatus()>=9 && order.getStatus()<=14)) {
            tvReturnDetails.setVisibility(View.VISIBLE);
        }
        tvReturnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReturnDetailsActivity.class);
                startActivity(intent);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order.getStatus()==0 || order.getStatus()==1 || order.getStatus()==2 || order.getStatus()==3) {
                    showAlert();
                } else {
                    Toast.makeText(getApplicationContext(), "You can cancel order only before dispatch.", Toast.LENGTH_LONG).show();
                }
            }
        });

        double discount = (order.getGrossTotal()*20)/100;
        double payable = order.getGrossTotal() - discount + order.getDeliveryCharges();
        order.setDiscount(discount);
        order.setPayable((payable + order.getDeliveryCharges()));

        tvTotal.setText("₹" + order.getPayable());
        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
            JSONArray jsonArray  = new JSONArray();
            for(int i=0;i<order.getAlMedicines().size();i++) {
                Medicine medicine = order.getAlMedicines().get(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("medicineId", medicine.getId());
                    jsonObject.put("quantity", medicine.getSelectedQty());
                    jsonObject.put("medicineBatchId", medicine.getMedicineBatchId());
                    jsonObject.put("medicineStrengthId", medicine.getSelectedStrengthId());
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    Log.d("veer", e.getMessage() + "");
                }
            }
//            Log.d("veer", jsonArray.toString());
            updateCart(jsonArray);
            }
        });
    }

    public void showAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);
        builder1.setView(dialogView);

//        builder1.setView(R.layout.dialog_cancel_order);

        final EditText etOther = (EditText) dialogView.findViewById(R.id.etOther);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.rgCancelOrder);

        builder1.setPositiveButton(
                "Cancel Order",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        int rbId = radioGroup.getCheckedRadioButtonId();
                        String reason = "", other = "";
                        switch (rbId) {
                            case R.id.rbIncorrectAddress:
                                reason = "Incorrect address";
                            break;
                            case R.id.rbPurchased:
                                reason = "Purchased from somewhere else";
                            break;
                            case R.id.rbDelay:
                                reason = "Delay in order";
                            break;
                            case R.id.rbNoCoupon:
                                reason = "Did not apply coupon";
                            break;
                            case R.id.rbWrongMedicine:
                                reason = "Wrong medicine selected";
                            break;
                            case R.id.rbOther:
                                reason = "Other";
                                other = etOther.getText().toString().trim() + "";
                            break;
                        }
                        cancelOrder(reason, other);
//                        Toast.makeText(getApplicationContext(),  "" + etOther.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
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
                    //bundle.putString("param1","FIRST TAB");
                    bundle.putSerializable("order", order);
                    OrderDetailsCalculationFragment calculationFragment = new OrderDetailsCalculationFragment();
                    calculationFragment.setArguments(bundle);
                    return calculationFragment;

                case SECOND_TAB:
//                    bundle.putString("param1","SECOND TAB");
                    bundle.putSerializable("order", order);
                    OrderDetailsMedicineFragment medicinesFragment = new OrderDetailsMedicineFragment();
                    medicinesFragment.setArguments(bundle);
                    return medicinesFragment;

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
                    return "Details";
                case SECOND_TAB:
                    return "Medicines";
            }
            return null;
        }
    }

    public void updateCart(JSONArray jsonArray) {
//            Log.d("veer", prefManager.getUserId() + "," + medicineId + "," + strengthId + ",");
            JSONObject object = new JSONObject();
            try {
                //input your API parameters
                object.put("apiVersion", Config.API_VERSION);
                object.put("token", prefManager.getToken());
                object.put("imei",prefManager.getImei());
                object.put("macId",Config.API_VERSION);
                object.put("userId", prefManager.getUserId() + "");
                object.put("jsonArray", jsonArray);
//                object.put("medicineId", medicineId);
//                object.put("medicineStrengthId", strengthId);
//                object.put("quantity", qty);
//                object.put("medicineBatchId", medicineBatchId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Enter the correct url for your api service site
            String url = Config.JSON_URL + "/reorder";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("veer response ", response.toString());
                            try {
                                JSONObject result = response.getJSONObject("result");
                                if(result.getBoolean("ack")) {
//                                    tvQty.setText("Qty : " + qty);
//                                    Config.cartItemCount++;
//                                    tvCartCnt.setText(Config.cartItemCount + "");
                                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
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

    public void cancelOrder(String reason, String other) {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",prefManager.getImei());
            object.put("userId", prefManager.getUserId() + "");
            object.put("orderId", order.getId() + "");
            object.put("reason", reason + "");
            object.put("other", other + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/cancel_order";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
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