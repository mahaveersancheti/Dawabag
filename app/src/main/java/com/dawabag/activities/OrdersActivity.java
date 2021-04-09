package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Order;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

//    ListView listView;
    ArrayList<Order> alItem;
    RecyclerView rvMedicines;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

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
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }
        });

        rvMedicines = (RecyclerView) findViewById(R.id.rvOrders);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        alItem = new ArrayList<>();
        alItem.clear();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken() + "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/orders";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray orders = data.getJSONArray("orders");
                                for(int i=0;i<orders.length();i++) {
                                    JSONObject orderJson = orders.getJSONObject(i);
                                    Order order = new Order();
                                    order.setId(orderJson.getInt("id"));
                                    order.setPayable(orderJson.getDouble("payable"));
                                    order.setDeliveryCharges(orderJson.getDouble("deliveryCharges"));
                                    order.setTotal(orderJson.getDouble("total"));
                                    order.setGrossTotal(orderJson.getDouble("grossTotal"));
                                    order.setCouponDiscount(orderJson.getDouble("couponDiscount"));
                                    order.setGst(orderJson.getDouble("gst"));
                                    order.setUserId(orderJson.getInt("userId"));
                                    order.setStatus(orderJson.getInt("status"));
                                    order.setPaymentMode(orderJson.getString("paymentMode"));
                                    order.setTransactionId(orderJson.getString("transactionId"));
                                    order.setCouponId(orderJson.getString("couponId"));
                                    order.setDate(orderJson.getString("createdAt"));
                                    order.setfName(orderJson.getString("fname"));
                                    order.setlName(orderJson.getString("lname"));
                                    order.setAddressLine1(orderJson.getString("addressLine1"));
                                    order.setAddressLine2(orderJson.getString("addressLine2"));
                                    order.setAddressLine3(orderJson.getString("addressLine3"));
                                    order.setPincode(orderJson.getString("pincode"));
                                    order.setCity(orderJson.getString("city"));
                                    order.setState(orderJson.getString("state"));
                                    order.setCountry(orderJson.getString("country"));
                                    order.setCanReturn(orderJson.getBoolean("canReturn"));
                                    order.setDeliveryDate(orderJson.getString("deliveryDate"));

                                    JSONArray medicines = orderJson.getJSONArray("details");
                                    ArrayList<Medicine> alMedicines = new ArrayList<>();
                                    for(int j=0;j<medicines.length();j++) {
                                        JSONObject medicineObject = medicines.getJSONObject(j);
                                        Medicine medicine = new Medicine();
                                        medicine.setId(medicineObject.getInt("id"));
                                        medicine.setSelectedStrengthId(medicineObject.getInt("strengthId"));
                                        medicine.setMedicineBatchId(medicineObject.getInt("batchId"));
                                        medicine.setGenericName(medicineObject.getString("name"));
                                        medicine.setStrength(medicineObject.getString("strength"));
                                        medicine.setBrand(medicineObject.getString("brandName"));
                                        medicine.setMrp(medicineObject.getDouble("MRP"));
                                        medicine.setOfferPrice(medicineObject.getDouble("offerPrice"));
                                        medicine.setSelectedQty(medicineObject.getInt("quantity"));
                                        medicine.setCanReturn(medicineObject.getBoolean("canReturn"));
                                        medicine.setStorageCondition(medicineObject.getString("storage_condition"));
                                        medicine.setOrderDetailsId(medicineObject.getInt("orderDetailsId"));
                                        alMedicines.add(medicine);
                                    }
                                    order.setAlMedicines(alMedicines);

                                    JSONArray prescriptions = orderJson.getJSONArray("prescriptions");
                                    ArrayList<String> alPrescriptions = new ArrayList<>();
                                    for(int j=0;j<prescriptions.length();j++) {
                                        JSONObject json = prescriptions.getJSONObject(j);
                                        alPrescriptions.add(json.getString("path"));
                                    }
                                    order.setAlPrescriptions(alPrescriptions);

                                    alItem.add(order);
                                }

                            } else {
                                //Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            //tvResults.setText("Showing " + alItem.size() + " results");
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
        private ArrayList<Order> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tvName, tvStatus, tvDate;
            Button btnReorder;
            View viewIndicator;
            public MyViewHolder(View v) {
                super(v);
                tvName = (TextView) v.findViewById(R.id.tvName);
                tvStatus = (TextView) v.findViewById(R.id.tvStatus);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                btnReorder = (Button) v.findViewById(R.id.btnReorder);
                viewIndicator = (View) v.findViewById(R.id.viewIndicator);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RecyclerAdapter(ArrayList<Order> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                 int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);

            RecyclerAdapter.MyViewHolder vh = new RecyclerAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Order item = mDataset.get(position);
            holder.tvName.setText(item.getfName() + " " + item.getlName());
            holder.tvDate.setText(item.getDate());
            holder.tvStatus.setText(""+item.getStatusLabel());
            Log.d("veer status", item.getStatusLabel() + "");
            switch (item.getStatus()) {
                case 6: holder.viewIndicator.setBackgroundResource(R.color.red);
                    break;
            }

            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    intent.putExtra("order", item);
                    startActivity(intent);
                }
            });

            holder.btnReorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    intent.putExtra("order", item);
                    startActivity(intent);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}