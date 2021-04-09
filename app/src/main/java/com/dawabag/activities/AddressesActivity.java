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
import com.dawabag.R;
import com.dawabag.beans.Address;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddressesActivity extends AppCompatActivity {

    RecyclerView rvMedicines;
    PrefManager prefManager;
    ArrayList<Address> alItem;
    TextView tvAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        prefManager = new PrefManager(this);

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
//                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
//                startActivity(intent);
            }
        });

        tvAddNew = (TextView) findViewById(R.id.tvAddNew);
        tvAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intent);
            }
        });

        rvMedicines = (RecyclerView) findViewById(R.id.rvAddress);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getData();
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
            object.put("token", "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/user_locations";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray addresses = data.getJSONArray("user_address");
                                for(int i=0;i<addresses.length();i++) {
                                    JSONObject addressObj = addresses.getJSONObject(i);
                                    Address address = new Address();
                                    address.setId(addressObj.getInt("id"));
                                    address.setName(addressObj.getString("name"));
                                    address.setPhone(addressObj.getString("phone"));
                                    address.setAddressType(addressObj.getInt("type"));
                                    address.setAddrLine1(addressObj.getString("addressLine1"));
                                    address.setAddrLine2(addressObj.getString("addressLine2"));
                                    address.setAddrLine3(addressObj.getString("addressLine3"));
                                    address.setCity(addressObj.getString("city"));
                                    address.setState(addressObj.getString("state"));
                                    address.setPincode(addressObj.getString("pincode"));
                                    alItem.add(address);
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
        private ArrayList<Address> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tvName, tvAddress, tvPhone, tvAddressType;
            ImageView ivSelect;
            LinearLayout llRemove, llEdit;
            public MyViewHolder(View v) {
                super(v);
                tvAddressType = (TextView) v.findViewById(R.id.tvAddressType);
                tvName = (TextView) v.findViewById(R.id.tvName);
                tvAddress = (TextView) v.findViewById(R.id.tvAddress);
                tvPhone = (TextView) v.findViewById(R.id.tvPhone);
                ivSelect = (ImageView) v.findViewById(R.id.ivSelect);
                llRemove = (LinearLayout) v.findViewById(R.id.llRemove);
                llEdit = (LinearLayout) v.findViewById(R.id.llEdit);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RecyclerAdapter(ArrayList<Address> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                 int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address, parent, false);

            RecyclerAdapter.MyViewHolder vh = new RecyclerAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Address item = mDataset.get(position);

            if(item.getAddressType()==0) {
                holder.tvAddressType.setText("Home");
            }
            if(item.getAddressType()==1) {
                holder.tvAddressType.setText("Office");
            }
            if(item.getAddressType()==2) {
                holder.tvAddressType.setText("Other");
            }

            holder.tvName.setText(item.getName() + "");
            holder.tvAddress.setText(item.getAddrLine1() + ", "+item.getAddrLine2()+", "+item.getAddrLine3()+"\n"+item.getCity()+", "+item.getState()+"\n"+item.getPincode());
            holder.tvPhone.setText("" + item.getPhone());
            holder.ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Config.shippingAddressId = item.getId();
                    Config.shippingAddress = item.getAddrLine1() + ", "+item.getAddrLine2()+", "+item.getAddrLine3()+"\n"+item.getCity()+", "+item.getState()+"\n"+item.getPincode();
                    holder.ivSelect.setImageResource(R.drawable.check);
                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            holder.llEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AddAddressActivity.class);
                    intent.putExtra("address", item);
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