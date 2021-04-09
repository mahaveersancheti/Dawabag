package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.dawabag.beans.Patient;
import com.dawabag.fragments.ProfileFragment;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class AddPatientsActivity extends AppCompatActivity {
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale;
    Spinner spBloodGroup;
    EditText etfName, etlName, etWeight;
    EditText etPhone, etReferredDoctor;
    TextView tvDob;
    PrefManager prefManager;
    String dob = "", gender = "";
    boolean isUpdate = false;
    int patientId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patients);

        prefManager = new PrefManager(getApplicationContext());

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

        etfName = (EditText) findViewById(R.id.etfName);
        etlName = (EditText) findViewById(R.id.etlName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        spBloodGroup = (Spinner) findViewById(R.id.spBloodGroup);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        etWeight = (EditText) findViewById(R.id.etWeight);
        tvDob = (TextView) findViewById(R.id.tvDob);
        etReferredDoctor = (EditText) findViewById(R.id.etReferredDoctor);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        ArrayList<String> alBloodGroup = new ArrayList<>();
        alBloodGroup.add("A+");alBloodGroup.add("A-");
        alBloodGroup.add("B+");alBloodGroup.add("B-");
        alBloodGroup.add("O+");alBloodGroup.add("O-");
        alBloodGroup.add("AB+");alBloodGroup.add("AB-");
        spBloodGroup.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.tv, alBloodGroup));

        tvDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPatientsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String dd = dayOfMonth + "";
                                String mm = (monthOfYear + 1) + "";
                                String yy = year + "";
                                String d = dd.length()==1 ? ("0" + dd) : dd;
                                String m = mm.length()==1 ? ("0" + mm) : mm;
                                dob = yy + "-" + m + "-" + d;
                                tvDob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear-18, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        if(getIntent().hasExtra("patient")) {
            Patient patient = (Patient) getIntent().getSerializableExtra("patient");
            patientId = patient.getId();
            etfName.setText(patient.getFirstName());
            etlName.setText(patient.getLastName());
            etWeight.setText(patient.getWeight() + "");
            etPhone.setText(patient.getPhone());
            tvDob.setText(patient.getDob());
            spBloodGroup.setSelection(alBloodGroup.indexOf(patient.getBloodGroup()));
            if(patient.getGender().equalsIgnoreCase("male")) {
                rbMale.setSelected(true);
            } else {
                rbFemale.setSelected(true);
            }
            isUpdate = true;
        }

    }

    public void submit(View view) {
        if(isUpdate) {
            updatePatient();
        } else {
            addPatient();
        }
    }

    public void updatePatient() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Updating", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
            object.put("fname", etfName.getText().toString().trim());
            object.put("lname", etlName.getText().toString().trim());
            object.put("phone", etPhone.getText().toString().trim());
            object.put("dob", dob);
            object.put("weight", etWeight.getText().toString().trim() + "");
            object.put("bloodGroup", spBloodGroup.getSelectedItem().toString().trim());
            object.put("gender", gender);
            object.put("referredDoctor", etReferredDoctor.getText().toString().trim());
            object.put("patientId", patientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/update_patient";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                                onBackPressed();
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
                progressDialog.dismiss();
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

    public void addPatient() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Updating", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("token", "");
            object.put("imei",Config.API_VERSION);
            object.put("macId",Config.API_VERSION);
            object.put("userId", prefManager.getUserId());
            object.put("fname", etfName.getText().toString().trim());
            object.put("lname", etlName.getText().toString().trim());
            object.put("phone", etPhone.getText().toString().trim());
            object.put("dob", dob);
            object.put("weight", etWeight.getText().toString().trim() + "");
            object.put("bloodGroup", spBloodGroup.getSelectedItem().toString().trim());
            object.put("gender", gender);
            object.put("referredDoctor", etReferredDoctor.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/add_patient";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                                onBackPressed();
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
                progressDialog.dismiss();
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
}