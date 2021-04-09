package com.dawabag.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.activities.IntroductionActivity;
import com.dawabag.activities.MedicinesActivity;
import com.dawabag.activities.OffersActivity;
import com.dawabag.activities.OrderDetailsActivity;
import com.dawabag.activities.OrdersActivity;
import com.dawabag.activities.OtpActivity;
import com.dawabag.activities.WebviewActivity;
import com.dawabag.beans.Article;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Order;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;
import com.dawabag.listeners.LocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.razorpay.Checkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView rvArticle, rvOpenOrders, rvSlider;
    ArrayList<Article> alArticles;
    ArrayList<Order> alOpenOrders;
    ArrayList<String> alBanners;
    LinearLayout llOrderMedicine, llMyOrders, llOffers, llPolicy;
    ImageView ivCart;
    TextView tvCartCnt, tvLocation;
    PrefManager prefManager;
    int scrollingIndex = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText etSearch;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        prefManager = new PrefManager(getActivity());
//        Toast.makeText(getActivity(), prefManager.getPhone(), Toast.LENGTH_LONG).show();

        ImageView ivNotification = (ImageView) view.findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                if(notificationsFragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, notificationsFragment);
                    transaction.commit();
                }
            }
        });
        ivCart = (ImageView) view.findViewById(R.id.ivCart);
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        tvCartCnt = (TextView) view.findViewById(R.id.tvCartCnt);
        tvCartCnt.setText(Config.cartItemCount + "");

        llOrderMedicine = (LinearLayout) view.findViewById(R.id.llOrderMedicine);
        llOrderMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MedicinesActivity.class);
                startActivity(intent);
            }
        });

        llMyOrders = (LinearLayout) view.findViewById(R.id.llMyOrders);
        llMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrdersActivity.class);
                startActivity(intent);
            }
        });

        llOffers = (LinearLayout) view.findViewById(R.id.llOffers);
        llOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OffersActivity.class);
                startActivity(intent);
            }
        });

        llPolicy = (LinearLayout) view.findViewById(R.id.llPolicy);
        llPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("forWhat", "policy");
                intent.putExtra("url", Config.BASE_URL + "test.php");
                startActivity(intent);
            }
        });

        tvLocation = (TextView) view.findViewById(R.id.tvLocation);

        if(prefManager.getPincode().length()==0) {
            MainActivity.locationListener = new LocationListener() {
                @Override
                public void onLocationReceived() {
                    super.onLocationReceived();
                    tvLocation.setText(Config.city + " " + Config.pinCode);
                }
            };
            tvLocation.setText(Config.city + " " + Config.pinCode);
        } else {
            tvLocation.setText(prefManager.getPincode() + "");
        }

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                fragment = new ChangePincodeFragment();
                if(fragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.commit();
                }
            }
        });

        rvSlider = (RecyclerView) view.findViewById(R.id.rvSlider);
        rvSlider.setHasFixedSize(true);
        rvSlider.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        rvArticle = (RecyclerView) view.findViewById(R.id.rvArticle);
        rvArticle.setHasFixedSize(true);
        rvArticle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        alArticles = new ArrayList<>();
        alArticles.add(new Article());alArticles.add(new Article());alArticles.add(new Article());alArticles.add(new Article());
        rvArticle.setAdapter(new MyAdapterFree(alArticles));

        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MedicinesActivity.class);
                startActivity(intent);
            }
        });

        rvOpenOrders = (RecyclerView) view.findViewById(R.id.rvOpenOrders);
        rvOpenOrders.setHasFixedSize(true);
        rvOpenOrders.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getData();

        return view;
    }

    public void scrollItems() {
        int duration = alBanners.size()*3000;
        new CountDownTimer(duration, 3000) {
            public void onTick(long millisUntilFinished) {
                if(scrollingIndex<alBanners.size()) {
                    rvSlider.smoothScrollToPosition(scrollingIndex);
                    scrollingIndex++;
                } else {
                    scrollingIndex = 0;
                }
            }
            public void onFinish() {
                scrollItems();
            }
        }.start();
    }

    class MyAdapterFree extends RecyclerView.Adapter<MyAdapterFree.MyViewHolder> {
        private ArrayList<Article> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView textView;
            ImageView imageView;
            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.tvArticle);
                imageView = v.findViewById(R.id.ivArticle);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapterFree(ArrayList<Article> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapterFree.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_article, parent, false);

            MyAdapterFree.MyViewHolder vh = new MyAdapterFree.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyAdapterFree.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Article item = mDataset.get(position);
//            holder.textView.setText(item.getTitle());
//            Glide.with(getApplicationContext()).load(item.getImage())
//                    .error(R.drawable.noimage)
//                    .into(holder.imageView);
//
//            holder.imageView.setId(position);
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Item itemFree = alVideos.get(holder.imageView.getId());
//                    Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
//                    intent.putExtra("object", itemFree);
//                    startActivity(intent);
//                }
//            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    class OpenOrderAdapter extends RecyclerView.Adapter<OpenOrderAdapter.MyViewHolder> {
        private ArrayList<Order> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tvOrderStatus, tvDate;
            Button btnOpenOrder;
            public MyViewHolder(View v) {
                super(v);
                tvOrderStatus = (TextView) v.findViewById(R.id.tvOrderStatus);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                btnOpenOrder = (Button) v.findViewById(R.id.btnOpenOrder);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public OpenOrderAdapter(ArrayList<Order> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public OpenOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_open_orders, parent, false);

            OpenOrderAdapter.MyViewHolder vh = new OpenOrderAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final OpenOrderAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Order item = mDataset.get(position);
            holder.tvOrderStatus.setText(item.getStatusLabelOpenOrder() + "");
            holder.tvDate.setText(item.getDate() + "");

            if(item.getStatus()!=0) {
                holder.btnOpenOrder.setText("Track");
            }

            holder.btnOpenOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.orderId = item.getId() + "";
                    String oId = item.getId() + "";
                    double payable = item.getPayable();
                    if(item.getStatus()==0) {
                        Log.d("veer payable", payable + "");
                        startPayment(oId, payable);
                    } else {
                        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                        intent.putExtra("order", item);
                        startActivity(intent);
//                        Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void getData() {
        alOpenOrders = new ArrayList<>();
        alBanners = new ArrayList<>();

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", "");
            object.put("imei", prefManager.getImei());
            object.put("userId", prefManager.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/home_data_mobile";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = result.getJSONObject("data");
                                Config.cartItemCount = data.getInt("cartCount");
                                tvCartCnt.setText(Config.cartItemCount + "");

                                JSONArray banners = data.getJSONArray("banners");
                                for(int i=0;i<banners.length();i++) {
                                    JSONObject jBanner = banners.getJSONObject(i);
                                    alBanners.add(jBanner.getString("image1"));
                                    alBanners.add(jBanner.getString("image2"));
                                    alBanners.add(jBanner.getString("image3"));
                                    alBanners.add(jBanner.getString("image4"));
                                }
                                rvSlider.setAdapter(new BannerAdapter(alBanners));

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

                                    alOpenOrders.add(order);
                                }
                                rvOpenOrders.setAdapter(new OpenOrderAdapter(alOpenOrders));
                            } else {
                                //Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            scrollItems();

                        } catch (Exception e) {
                            Log.d("veer error", e.getMessage() + "");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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

    public void startPayment(String orderId, double payable) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = getActivity();

        final Checkout co = new Checkout();
        co.setKeyID("rzp_test_uKiKQRMLhf7rbe");
//        co.setKeyID("rzp_test_uKiKQRMLhf7rbe");
        co.setImage(R.drawable.logo);

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Dawabag Pvt. Ltd.");
            options.put("description", "Your order id is " + orderId);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", (payable * 100));

            JSONObject preFill = new JSONObject();
            preFill.put("email", "payment@dawabag.com");
            preFill.put("contact", "" + prefManager.getPhone());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
            Log.e("veer", e.getMessage() + "");
        }
    }

    class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
        private ArrayList<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView ivBanner;
            public MyViewHolder(View v) {
                super(v);
                ivBanner = (ImageView) v.findViewById(R.id.ivBanner);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public BannerAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public BannerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner, parent, false);

            BannerAdapter.MyViewHolder vh = new BannerAdapter.MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final BannerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final String item = mDataset.get(position);

            Glide.with(getActivity()).load(item)
                    .error(R.drawable.logo)
                    //.apply(RequestOptions.circleCropTransform())
                    .into(holder.ivBanner);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}