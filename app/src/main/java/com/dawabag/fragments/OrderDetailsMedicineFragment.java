package com.dawabag.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.MedicineDetailsActivity;
import com.dawabag.activities.returnmedicine.ReturnActivity;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Order;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsMedicineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsMedicineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    ListView listView;
    ArrayList<Medicine> alItem;
    RecyclerView rvMedicines, rvPrescriptions;
    PrefManager prefManager;
    TextView tvReturn;
    Order order;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderDetailsMedicineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailsMedicineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailsMedicineFragment newInstance(String param1, String param2) {
        OrderDetailsMedicineFragment fragment = new OrderDetailsMedicineFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_details_medicine, container, false);

        order = (Order) getArguments().getSerializable("order");
        tvReturn = (TextView) view.findViewById(R.id.tvReturn);

        if(order.getStatus()!=5) {
            //without receive you can not return
            tvReturn.setVisibility(View.GONE);
        } else {

        }

        if(!order.isCanReturn())
            tvReturn.setVisibility(View.GONE);

        alItem = new ArrayList<>();
        for(int i=0;i<order.getAlMedicines().size();i++) {
            alItem.add(order.getAlMedicines().get(i));
        }

        rvMedicines = (RecyclerView) view.findViewById(R.id.rvMedicines);
        rvMedicines.setHasFixedSize(true);
        rvMedicines.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMedicines.setAdapter(new RecyclerAdapter(alItem));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMedicines.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        rvMedicines.addItemDecoration(dividerItemDecoration);

        rvPrescriptions = (RecyclerView) view.findViewById(R.id.rvPrescriptions);
        rvPrescriptions.setHasFixedSize(true);
        rvPrescriptions.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvPrescriptions.setAdapter(new RecyclerPrescriptionAdapter(order.getAlPrescriptions()));

        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReturnActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });
        return view;
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
                tvOfferPrice = (TextView) v.findViewById(R.id.tvOfferPrice);
                tvTotal = (TextView) v.findViewById(R.id.tvTotal);
                tvMRPShow = (TextView) v.findViewById(R.id.tvMRPShow);
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
            
            holder.ivDelete.setVisibility(View.GONE);
            
            final Medicine item = mDataset.get(position);
            holder.tvTitle.setText(item.getGenericName() + "");
            holder.tvCompany.setText(item.getBrand());
            holder.tvMRP.setText("Rs."+item.getOfferPrice());
            holder.tvQty.setText("Qty : " + item.getSelectedQty());
//            holder.tvOfferPrice.setText("₹"+item.getMrp());
            holder.tvTotal.setText("₹"+item.getOfferPrice());
            holder.tvMRPShow.setText("₹"+item.getMrp());
            holder.tvMRPShow.setPaintFlags(holder.tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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
            
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    class RecyclerPrescriptionAdapter extends RecyclerView.Adapter<RecyclerPrescriptionAdapter.MyViewHolder> {
        private ArrayList<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView ivPrescription;
            CheckBox cb;
            public MyViewHolder(View v) {
                super(v);
                ivPrescription = (ImageView) v.findViewById(R.id.ivPrescription);
                cb = (CheckBox) v.findViewById(R.id.cb);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RecyclerPrescriptionAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerPrescriptionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_prescription, parent, false);

            RecyclerPrescriptionAdapter.MyViewHolder vh = new RecyclerPrescriptionAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final RecyclerPrescriptionAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.cb.setVisibility(View.GONE);
            String path = mDataset.get(position);
            Glide.with(getActivity())
                    .load(path)
                    //.centerCrop()
                    .placeholder(R.drawable.sample)
                    .into(holder.ivPrescription);

            holder.ivPrescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void showZoomableImage(String fileUrl) {
        Dialog d = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setCancelable(true);
        WebView wv = new WebView(getActivity());
        wv.setLayoutParams(new ViewGroup.LayoutParams(android.widget.TableRow.LayoutParams.MATCH_PARENT, android.widget.TableRow.LayoutParams.MATCH_PARENT));
        wv.loadUrl(fileUrl);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setSupportZoom(true);
//        d.setContentView(iv);
        d.show();
    }

}