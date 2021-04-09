package com.dawabag.activities.returnmedicine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.OrderResultActivity;
import com.dawabag.activities.OrdersActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Order;
import com.dawabag.fragments.OrderDetailsMedicineFragment;
import com.dawabag.fragments.ProfileFragment;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReturnActivity extends AppCompatActivity {

    RecyclerView rvMedicines;
    ArrayList<Medicine> alItem, alSelected;
    PrefManager prefManager;
    TextView tvReturn;
    Order order;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retturn);

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        prefManager = new PrefManager(getApplicationContext());
        order = (Order) getIntent().getSerializableExtra("order");

        alSelected = new ArrayList<>();
        alItem = new ArrayList<>();
        for(int i=0;i<order.getAlMedicines().size();i++) {
            if(order.getAlMedicines().get(i).isCanReturn())     // is allow to return
                alItem.add(order.getAlMedicines().get(i));
        }
        rvMedicines = (RecyclerView) findViewById(R.id.rvMedicines);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvMedicines.setAdapter(new RecyclerAdapter(alItem));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMedicines.getContext(), new LinearLayoutManager(getApplicationContext()).getOrientation());
        rvMedicines.addItemDecoration(dividerItemDecoration);

        Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alSelected.size()>0)
                    returnOrder();
                else
                    Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public JSONArray getItemsJson() {
        Toast.makeText(getApplicationContext(), alItem.size() + " - " + alSelected.size(), Toast.LENGTH_SHORT).show();
        JSONArray jsonArray  = new JSONArray();
        try {
            for(int i=0;i<alSelected.size();i++) {
                Medicine item = alSelected.get(i);
//                for(int j=0;j<alItem.size();j++) {
//                    if(alSelected.get(i).getId()==alItem.get(j).getId()) {
//                        item = alItem.get(j);
//                        break;
//                    }
//                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("orderDetailsId", item.getOrderDetailsId());
                jsonObject.put("quantity", item.getReturnQty());
                jsonObject.put("reason", "test");
                jsonObject.put("total", item.getReturnQty()*item.getOfferPrice());
                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            Log.d("veer", e.getMessage() + "");
        }
        return jsonArray;
    }

    public void returnOrder() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("orderId",order.getId());
            object.put("userId", prefManager.getUserId() + "");
            object.put("items", getItemsJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("mahi", object.toString());
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/return_order";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
//                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                                showAlert();
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

    public void showAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);
        builder1.setView(R.layout.dialog_return_order);

        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
        private ArrayList<Medicine> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tvTitle, tvCompany, tvMRP, tvOfferPrice, tvTotal, tvQty;
            ImageView iv;
            CheckBox cb;
            Spinner spinner;
            ChipGroup chipGroup;
            public MyViewHolder(View v) {
                super(v);
                tvTitle = (TextView) v.findViewById(R.id.tvName);
                tvCompany = (TextView) v.findViewById(R.id.tvCompany);
                iv = (ImageView) v.findViewById(R.id.ivMedicine);
                cb = (CheckBox) v.findViewById(R.id.cbSelect);
                spinner = (Spinner) v.findViewById(R.id.spinner);
                tvMRP = (TextView) v.findViewById(R.id.tvMRP);
                chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);
                tvQty = (TextView) v.findViewById(R.id.tvQty);
                tvOfferPrice = (TextView) v.findViewById(R.id.tvOfferPrice);
                tvTotal = (TextView) v.findViewById(R.id.tvTotal);
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
                    .inflate(R.layout.item_medicine_return, parent, false);

            RecyclerAdapter.MyViewHolder vh = new RecyclerAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final Medicine item = mDataset.get(position);
            holder.tvTitle.setText(item.getGenericName() + "");
            holder.tvCompany.setText(item.getBrand());
            holder.tvMRP.setText("Rs."+item.getOfferPrice());
            holder.tvQty.setText("Qty : " + item.getSelectedQty());
//            holder.tvOfferPrice.setText("₹"+item.getMrp());
            holder.tvTotal.setText("₹"+item.getOfferPrice());

            double total = item.getOfferPrice() * item.getSelectedQty();
            holder.tvTotal.setText("₹" + total);
            Config.cartTotal = Config.cartTotal + total;
//            CartActivity.tvTotal.setText("₹" + Config.cartTotal);
//            CartCalculationFragment.tvTotal.setText("₹" + Config.cartTotal);

            holder.chipGroup.setSingleSelection(true);
            Chip chip = new Chip(holder.chipGroup.getContext());
            chip.setText(item.getStrength());
            chip.setChecked(true);
            holder.chipGroup.addView(chip);

            holder.tvMRP.setText("₹" + item.getOfferPrice());
            holder.iv.setImageResource(R.drawable.product);

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        if(item.getReturnQty()==0)
                            item.setReturnQty(item.getSelectedQty());
                        alSelected.add(item);
                        Toast.makeText(getApplicationContext(), item.getReturnQty() + " - " + item.getOrderDetailsId(), Toast.LENGTH_SHORT).show();
                    } else {
                        for(int i=0;i<alSelected.size();i++) {
                            if(item.getId()==alSelected.get(i).getId()) {
                                alSelected.remove(i);
                            }
                        }
                    }

                }
            });

            holder.tvQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double mrp = item.getOfferPrice();
                    listDialog(item, holder.tvQty, mrp, holder.tvTotal);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void listDialog(final Medicine item, final TextView tvQty, final double mrp, final TextView tvTotal) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ReturnActivity.this);
        //builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Select Quantity");

        int limit = item.getSelectedQty();
        //android.R.layout.select_dialog_singlechoice
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.tv);
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
                item.setReturnQty(Integer.parseInt(qty));
                tvQty.setText("Qty : " + qty);
                tvTotal.setText("₹"+(mrp* Integer.parseInt(qty)));
                Toast.makeText(getApplicationContext(), (mrp* Integer.parseInt(qty))+"", Toast.LENGTH_SHORT).show();
                //updateCart(medicineId, strengthId, qty, tvQty, mrp, tvTotal, medicineCartId, medicineBatchId);
            }
        });
        builderSingle.show();
    }
}