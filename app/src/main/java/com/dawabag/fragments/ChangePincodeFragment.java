package com.dawabag.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.dawabag.R;
import com.dawabag.beans.Medicine;
import com.dawabag.beans.Strengths;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePincodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePincodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    PrefManager prefManager;
    EditText etPincode;
    Button btnCheck;
    TextView tvError;

    public ChangePincodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePincodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePincodeFragment newInstance(String param1, String param2) {
        ChangePincodeFragment fragment = new ChangePincodeFragment();
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
        View view = inflater.inflate(R.layout.fragment_change_pincode, container, false);

        prefManager = new PrefManager(getActivity());

        tvError = (TextView) view.findViewById(R.id.tvError);
        etPincode = (EditText) view.findViewById(R.id.etPincode);
        btnCheck = (Button) view.findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pincode = etPincode.getText().toString().trim();
                if(pincode.length()==0) {
                    etPincode.setError("Mandatory field");
                    return;
                } else {
                    if(pincode.length()<6) {
                        etPincode.setError("Pincode must be 6 digit long");
                    } else {
                        checkPincode();
                    }
                }
            }
        });


        return view;
    }
    public void checkPincode() {
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", prefManager.getToken());
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
            object.put("pincode", etPincode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/checkPincode";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                tvError.setVisibility(View.GONE);
                                prefManager.setPincode(etPincode.getText().toString().trim());
                                Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_LONG).show();
                                Fragment fragment = null;
                                fragment = new HomeFragment();
                                if(fragment!=null) {
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_container, fragment);
                                    transaction.commit();
                                }
                            } else {
                                tvError.setVisibility(View.VISIBLE);
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