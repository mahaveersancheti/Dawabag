package com.dawabag.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Switch sw;
    CardView cv;
    RadioGroup rgUserType;
    RadioButton rbMale, rbFemale, rbDoctor, rbPharmacist;
    EditText etfName, etlName, etEmail, etDrugLicense, etHospital, etSpeciality, etRegistration, etStore, etAddress;
    EditText etPhone, etGstNumber;
    boolean isPharmacistOrDoctor = false;
    boolean isB2BSelected = false;
    String userType = "customer", gender = "male";
    PrefManager prefManager;
    LinearLayout llDoctor, llPharmacy;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        prefManager = new PrefManager(getActivity());

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
                Fragment fragment = null;
                fragment = new ProfileFragment();
                if(fragment!=null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.commit();
                }
            }
        });

        llPharmacy = (LinearLayout) view.findViewById(R.id.llPharmacy);
        llDoctor = (LinearLayout) view.findViewById(R.id.llDoctor);

        sw = (Switch) view.findViewById(R.id.sw);
        cv = (CardView) view.findViewById(R.id.cvDetails);
        etfName = (EditText) view.findViewById(R.id.etfName);
        etlName = (EditText) view.findViewById(R.id.etlName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etGstNumber = (EditText) view.findViewById(R.id.etGstNumber);
        etDrugLicense = (EditText) view.findViewById(R.id.etDrugLicense);
        etHospital = (EditText) view.findViewById(R.id.etHospital);
        etSpeciality = (EditText) view.findViewById(R.id.etSpeciality);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etRegistration = (EditText) view.findViewById(R.id.etRegistration);
        etStore = (EditText) view.findViewById(R.id.etStore);
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        rbMale = (RadioButton) view.findViewById(R.id.rbMale);
        rbFemale = (RadioButton) view.findViewById(R.id.rbFemale);
        rbDoctor = (RadioButton) view.findViewById(R.id.rbDoctor);
        rbPharmacist = (RadioButton) view.findViewById(R.id.rbPharmacist);

        etPhone.setText(prefManager.getPhone() + "");

        rgUserType = (RadioGroup) view.findViewById(R.id.rgUserType);
        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioId = rgUserType.getCheckedRadioButtonId();
                switch (radioId) {
                    case R.id.rbDoctor:
                        llDoctor.setVisibility(View.VISIBLE);
                        llPharmacy.setVisibility(View.GONE);
                        break;
                    case R.id.rbPharmacist:
                        llDoctor.setVisibility(View.GONE);
                        llPharmacy.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    cv.setVisibility(View.VISIBLE);
                    isPharmacistOrDoctor = true;
                } else {
                    cv.setVisibility(View.GONE);
                    isPharmacistOrDoctor = false;
                }
            }
        });

        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etfName.getText().toString().trim().length()==0) {
                    etfName.setError("Mandatory Field");
                    return;
                }
                if(etlName.getText().toString().trim().length()==0) {
                    etlName.setError("Mandatory Field");
                    return;
                }
                if(etEmail.getText().toString().trim().length()==0) {
                    etEmail.setError("Mandatory Field");
                    return;
                }
                if(isPharmacistOrDoctor) {
                    int id = rgUserType.getCheckedRadioButtonId();
                    if(id==R.id.rbDoctor) {
                        userType = "doctor";
                        if(etHospital.getText().toString().trim().length()==0) {
                            etHospital.setError("Mandatory Field");
                            return;
                        }
                        if(etSpeciality.getText().toString().trim().length()==0) {
                            etSpeciality.setError("Mandatory Field");
                            return;
                        }
                        if(etRegistration.getText().toString().trim().length()==0) {
                            etRegistration.setError("Mandatory Field");
                            return;
                        }
                    } else {
                        userType = "pharmacist";
                        if(etStore.getText().toString().trim().length()==0) {
                            etStore.setError("Mandatory Field");
                            return;
                        }
                        if(etAddress.getText().toString().trim().length()==0) {
                            etAddress.setError("Mandatory Field");
                            return;
                        }
                        if(etDrugLicense.getText().toString().trim().length()==0) {
                            etDrugLicense.setError("Mandatory Field");
                            return;
                        }
                        if(etGstNumber.getText().toString().trim().length()==0) {
                            etGstNumber.setError("Mandatory Field");
                            return;
                        }
                    }
                }
                updateData();
            }
        });

        getData();

        return view;
    }

    public void updateData() {
        if(rbMale.isSelected())
            gender = "Male";
        else
            gender = "Female";
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Updating", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
            object.put("userType", userType);
            object.put("fname", etfName.getText().toString().trim() + "");
            object.put("lname", etlName.getText().toString().trim()+ "");
            object.put("email", etEmail.getText().toString().trim() + "");
            object.put("isPharmacistOrDoctor", isPharmacistOrDoctor);
            object.put("clinicName", etHospital.getText().toString().trim() + "");
            object.put("registrationNumber", etRegistration.getText().toString().trim() +"" );
            object.put("specialization", etSpeciality.getText().toString().trim() + "");
            object.put("storeName", etStore.getText().toString().trim() + "");
            object.put("address", etAddress.getText().toString().trim() + "");
            object.put("drugLicense", etDrugLicense.getText().toString().trim() + "");
            object.put("gstNumber", etGstNumber.getText().toString().trim() + "");
            object.put("gender", gender + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/update_profile";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                if(isPharmacistOrDoctor)
                                    showAlert();
                                else
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

    public void showAlert() {
        String str = "Your details have been submitted. Pending for verification.\n"
                + "Once verified, you can start using your account for placing the order.\n"
                + "Our team will shortly get in touch with you. Thank you for registering with us.\n"
                + "Team DAWABAG!\n";
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
//        builder1.setMessage(str);
        builder1.setCancelable(true);
        builder1.setView(R.layout.dialog_profile);

        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Fragment fragment = null;
                        fragment = new ProfileFragment();
                        if(fragment!=null) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_container, fragment);
                            transaction.commit();
                        }
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void getData() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Updating", "Please have patience..", true);
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
        String url = Config.JSON_URL + "/user_details";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                JSONArray jUsers = result.getJSONArray("user");
                                for(int i=0;i<jUsers.length();i++) {
                                    JSONObject jUser = jUsers.getJSONObject(i);
                                    etfName.setText(jUser.getString("firstName"));
                                    etlName.setText(jUser.getString("lastName"));
                                    etEmail.setText(jUser.getString("email"));
                                    etHospital.setText(jUser.getString("clinicName"));
                                    etSpeciality.setText(jUser.getString("specialization"));
                                    etRegistration.setText(jUser.getString("registrationNumber"));
                                    etDrugLicense.setText(jUser.getString("licenceNumber"));
                                    etAddress.setText(jUser.getString("address"));
                                    etStore.setText(jUser.getString("storeName"));

                                    if(jUser.getString("gender").equalsIgnoreCase("male")) {
                                        rbMale.setSelected(true);
                                    } else {
//                                        Toast.makeText(getActivity(), jUser.getString("gender"), Toast.LENGTH_LONG).show();
                                        rbFemale.setSelected(true);
                                    }

                                    if(jUser.getString("role").equalsIgnoreCase("customer") || jUser.getString("role").equalsIgnoreCase("patient")) {

                                    }
                                    if(jUser.getString("role").equalsIgnoreCase("doctor")) {
                                        sw.setChecked(true);
                                        rbDoctor.setSelected(true);
                                    }
                                    if(jUser.getString("role").equalsIgnoreCase("pharmacist")) {
                                        sw.setChecked(true);
                                        rbPharmacist.setSelected(true);
                                    }
                                }
//                                Toast.makeText(getActivity(), result.getString("message"), Toast.LENGTH_LONG).show();
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
}