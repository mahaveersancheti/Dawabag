package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.dawabag.beans.Article;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.DatabaseHelper;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedicinesActivity extends AppCompatActivity {

//    ListView listView;
    ArrayList<Medicine> alItem;
    EditText etSearch;
    TextView tvResults, tvCartCnt, tvCheckout, tvAddMore;
    RecyclerView rvMedicines;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);

        prefManager = new PrefManager(getApplicationContext());
        //Toast.makeText(getApplicationContext(), prefManager.getUserId(), Toast.LENGTH_LONG).show();

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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
            }
        });

        tvAddMore = (TextView) findViewById(R.id.tvAddMore);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        tvCartCnt = (TextView) findViewById(R.id.tvCartCnt);
        tvResults = (TextView) findViewById(R.id.tvResults);
        etSearch = (EditText) findViewById(R.id.etSearch);
        //etSearch.requestFocus();

        tvCartCnt.setText(Config.cartItemCount + "");

        TextWatcher tw = new TextWatcher() {
            public void afterTextChanged(Editable s){
                String searchString = etSearch.getText().toString().trim();
                try {
                    if(searchString.length()==0)
                        return;
                    getData();
                    //tvCart.setText(String.format("%.2f", total) + " INR");
                } catch (Exception e) {
                    Log.d("veer", e.getMessage() + "");
                }
            }
            public void  beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void  onTextChanged (CharSequence s, int start, int before,int count) {
            }
        };
        etSearch.addTextChangedListener(tw);

        rvMedicines = (RecyclerView) findViewById(R.id.rvMedicines);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMedicines.getContext(), new LinearLayoutManager(getApplicationContext()).getOrientation());
        rvMedicines.addItemDecoration(dividerItemDecoration);

        tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                etSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                alItem.clear();
                tvResults.setText("Showing " + alItem.size() + " results");
                rvMedicines.setAdapter(new RecyclerAdapter(alItem));
            }
        });

        rvMedicines.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(view);
                return false;
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getData() {
        alItem = new ArrayList<>();
        alItem.clear();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("medicineName", etSearch.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/search_medicines";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray medicines = data.getJSONArray("medicines");
                                for(int i=0;i<medicines.length();i++) {
                                    JSONObject medicineObject = medicines.getJSONObject(i);
                                    Medicine medicine = new Medicine();
                                    medicine.setId(medicineObject.getInt("id"));
                                    medicine.setGenericName(medicineObject.getString("genericName"));
                                    medicine.setCompany(medicineObject.getString("companyName"));
                                    medicine.setOrderQtyLimit(medicineObject.getInt("orderQuantityLimit"));
                                    medicine.setTabletPack(medicineObject.getString("tabletPack"));
                                    medicine.setPackingType(medicineObject.getString("packingType"));
                                    JSONArray strengths = medicineObject.getJSONArray("strenghts");
                                    for(int j=0;j<strengths.length();j++) {
                                        JSONObject strenght = strengths.getJSONObject(j);
                                        Strengths strengthObj = new Strengths();
                                        strengthObj.setId(strenght.getInt("id"));
                                        strengthObj.setMedicineBatchId(strenght.getInt("medicineBatchId"));
                                        strengthObj.setStrength(strenght.getString("strength"));
                                        strengthObj.setBatchNumber(strenght.getString("batchNumber"));
                                        strengthObj.setExpiryDate(strenght.getString("expiryDate"));
                                        strengthObj.setShelfLifeDate(strenght.getString("shelfLifeDate"));
                                        strengthObj.setPriceToRetailer(strenght.getDouble("priceToRetailer"));
                                        strengthObj.setTotalPriceToRetailer(strenght.getDouble("totalPriceToRetailer"));
                                        strengthObj.setMRP(strenght.getDouble("MRP"));
                                        strengthObj.setOfferPrice(strenght.getDouble("offerPrice"));
                                        strengthObj.setNPR(strenght.getDouble("NPR"));
                                        strengthObj.setTNPR(strenght.getDouble("TNPR"));
                                        strengthObj.setPTS(strenght.getDouble("PTS"));
                                        medicine.getAlStrength().add(strengthObj);
                                    }
                                    alItem.add(medicine);
                                }

                            } else {
                                //Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            tvResults.setText("Showing " + alItem.size() + " results");
                            rvMedicines.setAdapter(new RecyclerAdapter(alItem));
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

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
        private ArrayList<Medicine> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tvTitle, tvCompany, tvMRP, tvOfferPrice, tvTotal, tvQty, tvDiscount;
            ImageView iv;
            Spinner spinner;
            ChipGroup chipGroup;
            public MyViewHolder(View v) {
                super(v);
                tvTitle = (TextView) v.findViewById(R.id.tvName);
                tvCompany = (TextView) v.findViewById(R.id.tvCompany);
                iv = (ImageView) v.findViewById(R.id.ivMedicine);
                spinner = (Spinner) v.findViewById(R.id.spinner);
                tvMRP = (TextView) v.findViewById(R.id.tvMRP);
                chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);
                tvQty = (TextView) v.findViewById(R.id.tvQty);
                tvOfferPrice = (TextView) v.findViewById(R.id.tvOfferPrice);
                tvTotal = (TextView) v.findViewById(R.id.tvTotal);
                tvDiscount = (TextView) v.findViewById(R.id.tvDiscount);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RecyclerAdapter(ArrayList<Medicine> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_medicines, parent, false);

            RecyclerAdapter.MyViewHolder vh = new RecyclerAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Medicine item = mDataset.get(position);
            //holder.tvTitle.setText(item.getGenericName() + " - " + item.getTabletPack() + " " + item.getPackingType());
            holder.tvTitle.setText(item.getGenericName());
            holder.tvCompany.setText(item.getCompany());
            if(item.getAlStrength().size()>0) {
                holder.tvMRP.setText("₹"+item.getAlStrength().get(0).getMRP());
                holder.tvMRP.setPaintFlags(holder.tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvOfferPrice.setText("₹"+item.getAlStrength().get(0).getOfferPrice());
                holder.tvTotal.setText("₹"+item.getAlStrength().get(0).getOfferPrice());
                holder.tvDiscount.setText("20% off");
            }

            Chip chipObj = null;
            Strengths strengthObj = null;
            holder.chipGroup.removeAllViews();
            holder.chipGroup.setSingleSelection(true);
            for(int i=0;i<item.getAlStrength().size();i++) {
                Strengths strength = item.getAlStrength().get(i);
                Chip chip = new Chip(holder.chipGroup.getContext());
                chip.setText(strength.getStrength());
                chip.setCheckable(true);
                chip.setClickable(true);
                holder.chipGroup.addView(chip);
                if(item.getSelectedStrengthId() == strength.getId()) {
                    chipObj = chip;
                    strengthObj = strength;
                }
            }
            //set selected chip while scrolling list
//            try {
//                if(strengthObj!=null && chipObj!=null)
//                    if(item.getSelectedStrengthId() == strengthObj.getId())
//                        chipObj.setChecked(true);
//            } catch (Exception e) {}

            holder.chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, int checkedId) {
                    Chip chip = holder.chipGroup.findViewById(checkedId);
                    if(chip!=null) {
                        for(int i=0;i<item.getAlStrength().size();i++) {
                            Strengths strength = item.getAlStrength().get(i);
                            String name = strength.getStrength();
                            if(name.equals(chip.getText().toString())) {
                                holder.tvMRP.setText("₹" + strength.getMRP());
                                holder.tvOfferPrice.setText("₹" + strength.getOfferPrice());
                                holder.tvTotal.setText("₹" + strength.getOfferPrice());
                                item.setSelectedStrengthId(strength.getId());
                                //Toast.makeText(getApplicationContext(), item.getGenericName().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        //deselect chip
                        item.setSelectedStrengthId(0);
                    }
                }
            });
            //chipGroup.invalidate();

            holder.tvQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double mrp = 0;
                    int medicineBatchId = 0;
                    int strengthId = 0;
                    if(item.getAlStrength().size()==1) {
                        ArrayList<Strengths> alStrengths = item.getAlStrength();
                        for(int i=0;i<alStrengths.size();i++) {
                            Strengths strength = alStrengths.get(i);
                            strengthId = strength.getId();
                            mrp = strength.getOfferPrice();
                            medicineBatchId = strength.getMedicineBatchId();
                        }
                    } else {
                        strengthId = item.getSelectedStrengthId();
                        if(strengthId==0) {
                            Toast.makeText(getApplicationContext(), "Please select strength first", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ArrayList<Strengths> alStrengths = item.getAlStrength();
                        for(int i=0;i<alStrengths.size();i++) {
                            Strengths strength = alStrengths.get(i);
                            if(strengthId==strength.getId()) {
                                mrp = strength.getOfferPrice();
                                medicineBatchId = strength.getMedicineBatchId();
                            }
                        }
                    }

                    int medicineId = item.getId();
                    listDialog(item.getOrderQtyLimit(), holder.tvQty, medicineId, strengthId, mrp, holder.tvTotal, medicineBatchId);
                }
            });

//            holder.iv.setImageResource(R.drawable.product);
            Glide.with(getApplicationContext()).load(item.getImage1())
                    .error(R.drawable.product)
                    .into(holder.iv);

            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MedicineDetailsActivity.class);
                    intent.putExtra("medicine", item);
                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), holder.chipGroup.isSelected() + "", Toast.LENGTH_LONG).show();
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void listDialog(int limit, final TextView tvQty, final int medicineId, final int strengthId, final double mrp, final TextView tvTotal, final int medicineBatchId) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MedicinesActivity.this);
        //builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Select Quantity");

        //android.R.layout.select_dialog_singlechoice
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MedicinesActivity.this, R.layout.tv);
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
                                tvCartCnt.setText(Config.cartItemCount + "");
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

    public void move(View v) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.openDb();
        Cursor c = dbHelper.readRecords();
        //Toast.makeText(getApplicationContext(), c.getCount() + "", Toast.LENGTH_LONG).show();
        for(int i=0;i<alItem.size();i++) {
            Medicine product = alItem.get(i);
            if(product.getOrderQtyLimit()!=0) {
                ContentValues values = new ContentValues();
                values.put("productId", product.getId());
//                values.put("productName", product.getName());
//                values.put("prize", product.getPrice() + "");
//                values.put("image", product.getImage());
//                values.put("qty", product.getQty());
//                values.put("total", product.getSubTotal());
//                values.put("unit", product.getUnit());
                dbHelper.addRecord(values);
            }
        }
        dbHelper.closeDb();

//        //int index = spCustomer.getSelectedItemPosition();
//        //int customerId = alUsers.get(index).getId();
//        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
//        intent.putExtra("customerId", customerId);
//        intent.putExtra("address", address);
//        startActivity(intent);
    }
}