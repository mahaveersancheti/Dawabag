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
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Offer;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Offer> alItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

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

        listView = (ListView) findViewById(R.id.lvOffers);

//        alItem = new ArrayList<>();
//        Offer medicine = new Offer();
//        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
//        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
//        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
//        listView.setAdapter(new ItemAdapter(getApplicationContext(), alItem));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                startActivity(intent);
            }
        });

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/offers";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray medicines = data.getJSONArray("offers");
                                for(int i=0;i<medicines.length();i++) {
                                    JSONObject medicineObject = medicines.getJSONObject(i);
                                    Offer medicine = new Offer();
                                    medicine.setId(medicineObject.getInt("id"));
                                    medicine.setTitle(medicineObject.getString("title"));
                                    medicine.setDescription(medicineObject.getString("description"));
                                    medicine.setEndDate(medicineObject.getString("endDate"));
                                    medicine.setStartDate(medicineObject.getString("startDate"));
                                    medicine.setCode(medicineObject.getString("code"));
                                    medicine.setMinimumOrderAmount(medicineObject.getDouble("minimumOrderAmount"));
                                    medicine.setDiscountAmount(medicineObject.getDouble("discountAmt"));
                                    medicine.setDiscountAmountLimit(medicineObject.getDouble("discountAmtLimit"));
                                    medicine.setIsFlatOrPer(medicineObject.getInt("isFlatOrPer"));
                                    medicine.setIsRedeemMultipleTimes(medicineObject.getInt("isRedeemMultipleTimes"));
                                    alItem.add(medicine);
                                }
                            } else {
                                //Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            listView.setAdapter(new ItemAdapter(getApplicationContext(), alItem));
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

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Offer> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Offer> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;

            final Offer item = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_offer, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
                holder.tvEndDate = (TextView) convertView.findViewById(R.id.tvEndDate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            if(position%2 == 1) {
//                convertView.setBackgroundColor(Color.parseColor("#F7F6F6"));
//            } else {
//                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            }

            holder.tvTitle.setText(item.getTitle());
            holder.tvDescription.setText(item.getDescription());
            holder.tvEndDate.setText(item.getEndDate());
//            Glide.with(getActivity())
//                    .load(item.getImage())
//                    //.centerCrop()
//                    .placeholder(R.drawable.noimg)
//                    .into(holder.iv);

            return convertView;
        }
    }
    class ViewHolder {
        TextView tvTitle, tvDescription, tvEndDate;
        ImageView iv;
    }
}