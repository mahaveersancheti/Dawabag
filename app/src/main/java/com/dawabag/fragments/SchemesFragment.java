package com.dawabag.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.activities.CartActivity;
import com.dawabag.beans.Article;
import com.dawabag.beans.Medicine;
import com.dawabag.helpers.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SchemesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchemesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView listView;
    ArrayList<Article> alItem;

    public SchemesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchemesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchemesFragment newInstance(String param1, String param2) {
        SchemesFragment fragment = new SchemesFragment();
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
        View view = inflater.inflate(R.layout.fragment_schemes, container, false);

        ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ImageView ivSearch = (ImageView) view.findViewById(R.id.ivSearch);
        ImageView ivCart = (ImageView) view.findViewById(R.id.ivCart);

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });
        TextView tvCartCnt = (TextView) view.findViewById(R.id.tvCartCnt);
        tvCartCnt.setText(Config.cartItemCount + "");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) view.findViewById(R.id.lvSchemes);

        alItem = new ArrayList<>();
        Article medicine = new Article();
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        alItem.add(medicine);alItem.add(medicine);alItem.add(medicine);
        listView.setAdapter(new ItemAdapter(getActivity(), alItem));

//        getData();

        return view;
    }

    public void getData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please have patience..", true);
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
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray blogs = data.getJSONArray("offers");

                                alItem = new ArrayList<>();
                                for(int i=0;i<blogs.length();i++) {
                                    JSONObject blog = blogs.getJSONObject(i);
                                    Article article = new Article();
                                    article.setId(blog.getInt("id"));
                                    article.setTitle(blog.getString("title"));
                                    article.setImage(blog.getString("image"));
                                    article.setDescription(blog.getString("description"));
                                    alItem.add(article);
                                }
                                listView.setAdapter(new ItemAdapter(getActivity(), alItem));

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

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Article> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Article> arrayList) {
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

            final Article item = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_notification, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            if(position%2 == 1) {
//                convertView.setBackgroundColor(Color.parseColor("#F7F6F6"));
//            } else {
//                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            }

//            holder.tvTitle.setText(item.getTitle());
//            holder.tvNote.setText(item.getDescription());
//            holder.tvDate.setText(item.getDate());
//            Glide.with(getActivity())
//                    .load(item.getImage())
//                    //.centerCrop()
//                    .placeholder(R.drawable.noimg)
//                    .into(holder.iv);

            return convertView;
        }
    }
    class ViewHolder {
        TextView tvTitle;
    }
}