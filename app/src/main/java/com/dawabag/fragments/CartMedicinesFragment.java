package com.dawabag.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.dawabag.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.MedicineDetailsActivity;
import com.dawabag.activities.MedicinesActivity;
import com.dawabag.activities.PrescriptionsActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.DatabaseHelper;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartMedicinesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartMedicinesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView listView;
    ArrayList<Medicine> alItem;
    RecyclerView rvMedicines;
    PrefManager prefManager;
    TextView tvAddNew, tvContinue;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartMedicinesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartMedicinesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartMedicinesFragment newInstance(String param1, String param2) {
        CartMedicinesFragment fragment = new CartMedicinesFragment();
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
        View view = inflater.inflate(R.layout.fragment_cart_medicines, container, false);

        prefManager = new PrefManager(getActivity());

        tvAddNew = (TextView) view.findViewById(R.id.tvAddNew);
        tvContinue = (TextView) view.findViewById(R.id.tvContinue);

        rvMedicines = (RecyclerView) view.findViewById(R.id.rvMedicines);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMedicines.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        rvMedicines.addItemDecoration(dividerItemDecoration);

        getData();

        try {
            JSONArray jsonArray = new JSONArray(prefManager.getSelectedPrescriptions());
            tvAddNew.setText(jsonArray.length() + " Prescriptions selected");
        } catch (Exception e) {}
        tvAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PrescriptionsActivity.class);
                startActivity(intent);
            }
        });
        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MedicinesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getData() {
        alItem = new ArrayList<>();
        alItem.clear();
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/get_cart_items";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray medicines = data.getJSONArray("medicines");
                                for(int i=0;i<medicines.length();i++) {
                                    JSONObject medicineObject = medicines.getJSONObject(i);
                                    Medicine medicine = new Medicine();
                                    medicine.setMedicineBatchId(medicineObject.getInt("medicineBatchId"));
                                    medicine.setMedicineCartId(medicineObject.getInt("medicineCartId"));
                                    medicine.setId(medicineObject.getInt("id"));
                                    medicine.setGenericName(medicineObject.getString("genericName"));
                                    medicine.setCompany(medicineObject.getString("companyName"));
                                    medicine.setOrderQtyLimit(medicineObject.getInt("orderQuantityLimit"));
                                    medicine.setSelectedQty(medicineObject.getInt("quantity"));
                                    medicine.setSelectedStrengthId(medicineObject.getInt("medicineStrengthId"));
                                    medicine.setMrp(medicineObject.getDouble("MRP"));
                                    medicine.setPackingType(medicineObject.getString("packingType"));
                                    medicine.setStrength(medicineObject.getString("strength"));
                                    medicine.setOfferPrice(medicineObject.getDouble("offerPrice"));

//                                    JSONArray strengths = medicineObject.getJSONArray("strenghts");
//                                    for(int j=0;j<strengths.length();j++) {
//                                        JSONObject strenght = strengths.getJSONObject(j);
//                                        Strengths strengthObj = new Strengths();
////                                        strengthObj.setId(strenght.getInt("id"));
//                                        strengthObj.setStrength(strenght.getString("strength"));
//                                        strengthObj.setBatchNumber(strenght.getString("batchNumber"));
//                                        strengthObj.setExpiryDate(strenght.getString("expiryDate"));
//                                        strengthObj.setShelfLifeDate(strenght.getString("shelfLifeDate"));
//                                        strengthObj.setPriceToRetailer(strenght.getDouble("priceToRetailer"));
//                                        strengthObj.setTotalPriceToRetailer(strenght.getDouble("totalPriceToRetailer"));
//                                        //strengthObj.setMRP(strenght.getDouble("MRP"));
//                                        strengthObj.setMRP(strenght.getDouble("MRP"));
//                                        strengthObj.setOfferPrice(strenght.getDouble("offerPrice"));
//                                        strengthObj.setNPR(strenght.getDouble("NPR"));
//                                        strengthObj.setTNPR(strenght.getDouble("TNPR"));
//                                        strengthObj.setPTS(strenght.getDouble("PTS"));
//                                        medicine.getAlStrength().add(strengthObj);
//                                    }
                                    alItem.add(medicine);
                                }
                                JSONObject jCalculations = data.getJSONObject("calculations");
                                Config.cartItemCount = alItem.size();
//                                Config.cartTotal = jCalculations.getDouble("total");
//                                Config.couponDiscount = jCalculations.getDouble("couponDiscount");
//                                Config.shipping = jCalculations.getDouble("deliveryCharges");
//                                Config.discount = jCalculations.getDouble("dawabagDiscount");
//                                Config.payable = jCalculations.getDouble("payable");
                                Config.cartTotal = 0;
                                Config.couponDiscount = 0;
                                Config.shipping = 0;
                                Config.discount = 0;
                                Config.payable = 0;
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
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
            TextView tvTitle, tvCompany, tvMRP, tvOfferPrice, tvTotal, tvQty, tvMRPShow;
            ImageView iv, ivDelete;
            Spinner spinner;
            ChipGroup chipGroup;
            public MyViewHolder(View v) {
                super(v);
                tvTitle = (TextView) v.findViewById(R.id.tvName);
                tvCompany = (TextView) v.findViewById(R.id.tvCompany);
                iv = (ImageView) v.findViewById(R.id.ivMedicine);
                ivDelete = (ImageView) v.findViewById(R.id.ivDelete);
                spinner = (Spinner) v.findViewById(R.id.spinner);
                tvMRP = (TextView) v.findViewById(R.id.tvMRP);
                chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);
                tvQty = (TextView) v.findViewById(R.id.tvQty);
                tvMRPShow = (TextView) v.findViewById(R.id.tvMRPShow);
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
                    .inflate(R.layout.item_medicine_cart, parent, false);

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
            holder.tvCompany.setText(item.getCompany());
            holder.tvMRP.setText("₹"+item.getOfferPrice());
            holder.tvQty.setText("Qty : " + item.getSelectedQty());
//            holder.tvOfferPrice.setText("₹"+item.getAlStrength().get(0).getOfferPrice());
            holder.tvTotal.setText("₹"+item.getOfferPrice());
            holder.tvMRPShow.setText("₹"+item.getMrp());
            holder.tvMRPShow.setPaintFlags(holder.tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            double total = item.getOfferPrice() * item.getSelectedQty();
//            holder.tvTotal.setText("₹" + total);
            Config.payable = Config.payable + total;

            double totalMRP = item.getMrp() * item.getSelectedQty();
            Config.cartTotal = Config.cartTotal + totalMRP;

            CartCalculationFragment.tvTotal.setText("₹" + Config.cartTotal);
            CartCalculationFragment.tvShipping.setText("₹" + Config.shipping);
            CartCalculationFragment.tvCouponDiscount.setText("₹0");
            Config.discount = (Config.cartTotal*20)/100;
            Config.payable = Config.cartTotal - Config.discount + Config.shipping;
            CartCalculationFragment.tvDiscount.setText("-₹" + Config.discount);
            CartCalculationFragment.tvPayable.setText("₹" + Config.payable);
            CartActivity.tvTotal.setText("₹" + Config.payable);

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Config.cartItemCount--;
                    int medicineCartId = item.getMedicineCartId();
                    deleteCartItem(item.getMedicineCartId(), medicineCartId);
                }
            });

            holder.chipGroup.setSingleSelection(true);
            Chip chip = new Chip(holder.chipGroup.getContext());
            chip.setText(item.getStrength());
            chip.setChecked(true);
            holder.chipGroup.addView(chip);

//            holder.tvMRP.setText("₹" + item.getMrp());

            holder.tvQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int strengthId = item.getSelectedStrengthId();
                    int medicineId = item.getId();
                    double mrp = item.getOfferPrice();
                    double offerPrice = item.getOfferPrice();
                    int medicineBatchId = item.getMedicineBatchId();
                    int medicineCartId = item.getMedicineCartId();
                    listDialog(item.getOrderQtyLimit(), holder.tvQty, medicineId, strengthId, offerPrice, holder.tvTotal, medicineCartId, medicineBatchId);
                }
            });

            holder.iv.setImageResource(R.drawable.product);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getApplicationContext(), MedicineDetailsActivity.class);
//                    startActivity(intent);
//                    Toast.makeText(getActivity(), holder.chipGroup.isSelected() + "", Toast.LENGTH_LONG).show();
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void listDialog(int limit, final TextView tvQty, final int medicineId, final int strengthId, final double mrp, final TextView tvTotal, final int medicineCartId, final int medicineBatchId) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        //builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Select Quantity");

        //android.R.layout.select_dialog_singlechoice
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tv);
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
                updateCart(medicineId, strengthId, qty, tvQty, mrp, tvTotal, medicineCartId, medicineBatchId);
            }
        });
        builderSingle.show();
    }

    public void updateCart(final int medicineId, final int strengthId, final String qty, final TextView tvQty, final double mrp, final TextView tvTotal, final int medicineCartId, final int medicineBatchId) {
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
                                double total = mrp * Integer.parseInt(qty);
                                tvTotal.setText("₹" + total);
                                for(int i=0;i<alItem.size();i++) {
                                    Medicine medicine = alItem.get(i);
                                    if(medicine.getMedicineCartId() == medicineCartId)
                                        medicine.setSelectedQty(Integer.parseInt(qty));
                                }
                                setTotalPayable();
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

    public void setTotalPayable() {
        Config.cartTotal = 0;
//        Config.cartTotalMRP = 0;
        for(int i=0;i<alItem.size();i++) {
            Medicine item = alItem.get(i);
            double total = item.getOfferPrice() * item.getSelectedQty();
            Config.payable = Config.payable + total;
            double totalMRP = item.getMrp() * item.getSelectedQty();
            Config.cartTotal = Config.cartTotal + totalMRP;
        }
//        CartActivity.tvTotal.setText("₹" + Config.payable);

        CartCalculationFragment.tvTotal.setText("₹" + Config.cartTotal);
        Config.discount = (Config.cartTotal*20)/100;
        Config.payable = Config.cartTotal - Config.discount - Config.couponDiscount + Config.shipping;
        CartCalculationFragment.tvDiscount.setText("-₹" + Config.discount);
        CartCalculationFragment.tvPayable.setText("₹" + Config.payable);
        CartActivity.tvTotal.setText("₹" + Config.payable);

    }

    public void deleteCartItem(final int cartItemId, final int medicineCartId) {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("id", cartItemId);   //cart item id
            object.put("userId", prefManager.getUserId() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/remove_item_from_cart";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Config.cartItemCount--;
                                getData();
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