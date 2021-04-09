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
import com.bumptech.glide.Glide;
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedicineDetailsActivity extends AppCompatActivity {

    Medicine medicine;
    ChipGroup chipGroup;
    TextView tvTitle, tvCompany, tvMRP, tvOfferPrice, tvQty, tvDiscount, tvTotal, tvCheckout, tvAddMore;
    ImageView iv;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        prefManager = new PrefManager(this);

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
            }
        });

        medicine = (Medicine) getIntent().getSerializableExtra("medicine");

        chipGroup = (ChipGroup) findViewById(R.id.chipGroup);
        tvTitle = (TextView) findViewById(R.id.tvName);
        tvCompany = (TextView) findViewById(R.id.tvCompany);
        iv = (ImageView) findViewById(R.id.ivMedicine);
        tvMRP = (TextView) findViewById(R.id.tvMRP);
        chipGroup = (ChipGroup) findViewById(R.id.chipGroup);
        tvQty = (TextView) findViewById(R.id.tvQty);
        tvOfferPrice = (TextView) findViewById(R.id.tvOfferPrice);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);

        tvTitle.setText(medicine.getGenericName());
        tvCompany.setText(medicine.getCompany());
        if(medicine.getAlStrength().size()>0) {
            tvMRP.setText("₹"+medicine.getAlStrength().get(0).getMRP());
            tvMRP.setPaintFlags(tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvOfferPrice.setText("₹"+medicine.getAlStrength().get(0).getOfferPrice());
            tvTotal.setText("₹"+medicine.getAlStrength().get(0).getOfferPrice());
            tvDiscount.setText("20% off");
        }

        Chip chipObj = null;
        Strengths strengthObj = null;
        chipGroup.removeAllViews();
        chipGroup.setSingleSelection(true);
        for(int i=0;i<medicine.getAlStrength().size();i++) {
            Strengths strength = medicine.getAlStrength().get(i);
            Chip chip = new Chip(chipGroup.getContext());
            chip.setText(strength.getStrength());
            chip.setCheckable(true);
            chip.setClickable(true);
            chipGroup.addView(chip);
            if(medicine.getSelectedStrengthId() == strength.getId()) {
                chipObj = chip;
                strengthObj = strength;
            }
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
            Chip chip = chipGroup.findViewById(checkedId);
            if(chip!=null) {
                for(int i=0;i<medicine.getAlStrength().size();i++) {
                    Strengths strength = medicine.getAlStrength().get(i);
                    String name = strength.getStrength();
                    if(name.equals(chip.getText().toString())) {
                        tvMRP.setText("₹" + strength.getMRP());
                        tvOfferPrice.setText("₹" + strength.getOfferPrice());
                        tvTotal.setText("₹" + strength.getOfferPrice());
                        medicine.setSelectedStrengthId(strength.getId());
                        //Toast.makeText(getApplicationContext(), item.getGenericName().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                //deselect chip
                medicine.setSelectedStrengthId(0);
            }
            }
        });

        tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double mrp = 0;
                int medicineBatchId = 0;
                int strengthId = 0;
                if(medicine.getAlStrength().size()==1) {
                    ArrayList<Strengths> alStrengths = medicine.getAlStrength();
                    for(int i=0;i<alStrengths.size();i++) {
                        Strengths strength = alStrengths.get(i);
                        strengthId = strength.getId();
                        mrp = strength.getOfferPrice();
                        medicineBatchId = strength.getMedicineBatchId();
                    }
                } else {
                    strengthId = medicine.getSelectedStrengthId();
                    if(strengthId==0) {
                        Toast.makeText(getApplicationContext(), "Please select strength first", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ArrayList<Strengths> alStrengths = medicine.getAlStrength();
                    for(int i=0;i<alStrengths.size();i++) {
                        Strengths strength = alStrengths.get(i);
                        if(strengthId==strength.getId()) {
                            mrp = strength.getOfferPrice();
                            medicineBatchId = strength.getMedicineBatchId();
                        }
                    }
                }

                int medicineId = medicine.getId();
                listDialog(medicine.getOrderQtyLimit(), tvQty, medicineId, strengthId, mrp, tvTotal, medicineBatchId);
            }
        });

        Glide.with(getApplicationContext()).load(medicine.getImage1())
                .error(R.drawable.product)
                .into(iv);

        tvAddMore = (TextView) findViewById(R.id.tvAddMore);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void listDialog(int limit, final TextView tvQty, final int medicineId, final int strengthId, final double mrp, final TextView tvTotal, final int medicineBatchId) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MedicineDetailsActivity.this);
        //builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Select Quantity");

        //android.R.layout.select_dialog_singlechoice
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MedicineDetailsActivity.this, R.layout.tv);
        for(int i=1;i<=limit;i++) {
            arrayAdapter.add("" + i);
        }

//        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String qty = arrayAdapter.getItem(which);
                double total = mrp * Integer.parseInt(qty);
                tvTotal.setText("₹" + total);
                updateCart(medicineId, strengthId, qty, tvQty, medicineBatchId);
            }
        });
        builderSingle.show();
    }

    public void updateCart(final int medicineId, final int strengthId, final String qty, final TextView tvQty, final int medicineBatchId) {
        Log.d("veer", prefManager.getUserId() + "," + medicineId + "," + strengthId + ",");
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("medicineId", medicineId);
            object.put("userId", prefManager.getUserId() + "");
            object.put("medicineStrengthId", strengthId);
            object.put("quantity", qty);
            object.put("medicineBatchId", medicineBatchId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/add_item_in_cart";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                tvQty.setText("Qty : " + qty);
                                Config.cartItemCount++;
//                                tvCartCnt.setText(Config.cartItemCount + "");
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
}