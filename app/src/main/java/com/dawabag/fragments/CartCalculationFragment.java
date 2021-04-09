package com.dawabag.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dawabag.R;
import com.dawabag.activities.AddressesActivity;
import com.dawabag.activities.CartActivity;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartCalculationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartCalculationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static TextView tvTotal, tvDiscount, tvShipping, tvPayable, tvCouponDiscount, tvAddress;
    ImageView ivAddAddress;
    EditText etCouponCode;
    Button btnApply;
    PrefManager prefManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartCalculationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartCalculationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartCalculationFragment newInstance(String param1, String param2) {
        CartCalculationFragment fragment = new CartCalculationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_calculation, container, false);

        prefManager = new PrefManager(getActivity());

        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
        tvShipping = (TextView) view.findViewById(R.id.tvShipping);
        tvPayable = (TextView) view.findViewById(R.id.tvPayable);
        tvCouponDiscount = (TextView) view.findViewById(R.id.tvCouponDiscount);
        etCouponCode = (EditText) view.findViewById(R.id.etCouponCode);
        btnApply = (Button) view.findViewById(R.id.btnApply);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);

        tvAddress.setText(Config.shippingAddress);

        ivAddAddress = (ImageView) view.findViewById(R.id.ivAddAddress);
        ivAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddressesActivity.class);
                startActivity(intent);
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String couponCode = etCouponCode.getText().toString().trim();
                if(couponCode.length()==0) {
                    etCouponCode.setError("Invalid value");
                    return;
                }
                if(Config.cartItemCount>0)
                    applyCoupon();
                else
                    Toast.makeText(getActivity(), "Cart can not be empty", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void applyCoupon() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("couponCode", etCouponCode.getText().toString());
            object.put("userId", prefManager.getUserId() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/apply_coupon";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject coupon = result.getJSONObject("coupon");
                                int isFlatOrPer = coupon.getInt("isFlatOrPer");
                                double discountAmtLimit = coupon.getDouble("discountAmtLimit");
                                double discountAmt = coupon.getDouble("discountAmt");
                                double minimumOrderAmount = coupon.getDouble("minimumOrderAmount");

                                if(Config.cartTotal>=minimumOrderAmount) {
                                    if(isFlatOrPer==0) {    //flat
                                        Config.couponDiscount = discountAmt;
                                    } else if(isFlatOrPer==1) { //percentage
                                        double discount = (discountAmt * Config.cartTotal) / 100;
                                        if(discount<=discountAmtLimit) {
                                            Config.couponDiscount = discount;
                                        } else {
                                            Config.couponDiscount = discountAmtLimit;
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Minimum order amount must be " + minimumOrderAmount + " INR", Toast.LENGTH_LONG).show();
                                }

//                                CartActivity.tvTotal.setText("₹" + Config.cartTotal);
//                                CartCalculationFragment.tvTotal.setText("₹" + Config.cartTotal);
                                Config.discount = (Config.cartTotal*20)/100;
                                Config.payable = Config.cartTotal - Config.discount - Config.couponDiscount + Config.shipping;
                                CartCalculationFragment.tvDiscount.setText("-₹" + Config.discount);
                                tvCouponDiscount.setText("-₹" + discountAmt + "");
                                CartCalculationFragment.tvPayable.setText("₹" + Config.payable);
                                CartActivity.tvTotal.setText("₹" + Config.payable);

                                Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_LONG).show();
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
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}