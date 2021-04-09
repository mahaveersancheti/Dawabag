package com.dawabag.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.beans.Order;
import com.dawabag.helpers.Config;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsCalculationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsCalculationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tvAddress, tvDeliveryDate;
    public static TextView tvTotal, tvDiscount, tvShipping, tvPayable, tvCouponDiscount;
    Order order;
    LinearLayout llDeliveryDate, llOrderTracking;

    ImageView ivPlaced, ivConfirm, ivProcessing, ivOut, ivDeliver;
    TextView tvPlaced, tvConfirm, tvProcessing, tvOut, tvDeliver;


    public OrderDetailsCalculationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailsCalculationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailsCalculationFragment newInstance(String param1, String param2) {
        OrderDetailsCalculationFragment fragment = new OrderDetailsCalculationFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_details_calculation, container, false);

        order = (Order) getArguments().getSerializable("order");

        ivPlaced = (ImageView) view.findViewById(R.id.ivPlaced);
        ivConfirm = (ImageView) view.findViewById(R.id.ivConfirm);
        ivProcessing = (ImageView) view.findViewById(R.id.ivProcessing);
        ivOut = (ImageView) view.findViewById(R.id.ivOut);
        ivDeliver = (ImageView) view.findViewById(R.id.ivDeliver);

        tvPlaced = (TextView) view.findViewById(R.id.tvPlaced);
        tvConfirm = (TextView) view.findViewById(R.id.tvConfirm);
        tvProcessing = (TextView) view.findViewById(R.id.tvProcessing);
        tvOut = (TextView) view.findViewById(R.id.tvOut);
        tvDeliver = (TextView) view.findViewById(R.id.tvDeliver);
        tvCouponDiscount = (TextView) view.findViewById(R.id.tvCouponDiscount);

        int status = order.getStatus();
        String orderTrackingStatus = "";
        switch (status) {
            case 0 :
                tvPlaced.setTextColor(getResources().getColor(R.color.textColor));
                break;
            case 1 :
                tvPlaced.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2 : tvConfirm.setTextColor(getResources().getColor(R.color.colorPrimary)); break;
            case 3 : tvProcessing.setTextColor(getResources().getColor(R.color.colorPrimary)); break;
            case 4 : tvOut.setTextColor(getResources().getColor(R.color.colorPrimary)); break;
            case 5 : tvDeliver.setTextColor(getResources().getColor(R.color.colorPrimary)); break;
            case 6 : orderTrackingStatus = "Order Cancelled"; break;
            case 7 : orderTrackingStatus = "Order Returned"; break;
            case 8 : orderTrackingStatus = "Order Rejected"; break;
        }

        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
        tvShipping = (TextView) view.findViewById(R.id.tvShipping);
        tvPayable = (TextView) view.findViewById(R.id.tvPayable);
        tvDeliveryDate = (TextView) view.findViewById(R.id.tvDeliveryDate);

        double discount = (order.getGrossTotal()*20)/100;
        double payable = order.getGrossTotal() - discount + order.getDeliveryCharges();
        order.setDiscount(discount);
        order.setPayable((payable + order.getDeliveryCharges()));

        tvTotal.setText("₹" + order.getGrossTotal());
        tvDiscount.setText("₹" + order.getDiscount());
        tvShipping.setText("₹" + order.getDeliveryCharges());
        tvPayable.setText("₹" + (payable + order.getDeliveryCharges()));
        tvCouponDiscount.setText("₹" + order.getCouponDiscount());
        tvAddress.setText(order.getAddressLine1() + ", " + order.getAddressLine2() + ", " + order.getAddressLine3() + "\n" + order.getCity() + ", " + order.getState() + "\n" + order.getPincode());
        tvDeliveryDate.setText(order.getDeliveryDate() + "");

        llDeliveryDate = (LinearLayout) view.findViewById(R.id.llDeliveryDate);
        llOrderTracking = (LinearLayout) view.findViewById(R.id.llOrderTracking);

        if(status>5) {
            llOrderTracking.setVisibility(View.GONE);
        } else {
            llDeliveryDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = llOrderTracking.getVisibility();
                    if(i==0)
                        llOrderTracking.setVisibility(View.GONE);
                    else if(i==8)
                        llOrderTracking.setVisibility(View.VISIBLE);
                    //Toast.makeText(getActivity(), i + "", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}