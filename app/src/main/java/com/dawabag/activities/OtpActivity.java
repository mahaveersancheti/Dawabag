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
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {

    EditText etOTP;
    TextView tvNumber1, tvNumber2, tvResend;
    String mobile = "";
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        prefManager = new PrefManager(this);

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mobile = getIntent().getStringExtra("mobile");

        etOTP = (EditText) findViewById(R.id.etOTP);
        tvNumber1 = (TextView) findViewById(R.id.tvNumber1);
        tvNumber2 = (TextView) findViewById(R.id.tvNumber2);
        tvResend = (TextView) findViewById(R.id.tvResend);

        tvNumber1.setText("+91 " + mobile);
        tvNumber2.setText("+91 " + mobile);
    }

    public void validate(View view) {
        String otp = etOTP.getText().toString().trim();

        boolean flag = true;
        if(otp.length()==0) {
            flag = false;
            etOTP.setError("OTP is mandatory");
        }
        if(flag) {
            authenticate();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details.", Toast.LENGTH_LONG).show();
        }
    }

    public void authenticate() {
        final ProgressDialog progressDialog = ProgressDialog.show(OtpActivity.this, "Validating", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("mobile",mobile);
            object.put("otp", etOTP.getText().toString().trim());
            object.put("userType","patient");
            object.put("imei",prefManager.getImei());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/verify_otp";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                PrefManager prefManager = new PrefManager(getApplicationContext());
                                prefManager.setUserId(result.getInt("userId") + "");
                                prefManager.setUserType(result.getString("userType") + "");
                                prefManager.setName(result.getString("username"));
                                prefManager.setToken(result.getString("token"));
                                prefManager.setPhone(mobile);
                                prefManager.setIsLogin(true);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
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