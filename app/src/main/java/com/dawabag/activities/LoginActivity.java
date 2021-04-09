package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dawabag.MainActivity;
import com.dawabag.R;
import com.dawabag.helpers.Config;
import com.dawabag.helpers.PrefManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    PrefManager prefManager;
    EditText etPhone;
    TextView tvTerms, tvPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PrefManager(this);
        if(prefManager.getIsLogin()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // change notification bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        etPhone = (EditText) findViewById(R.id.etPhone);
        tvPrivacy = (TextView) findViewById(R.id.tvPrivacy);
        tvTerms = (TextView) findViewById(R.id.tvTerms);

        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        prefManager.setImei(deviceId + "");
    }

    public void login(View view) {
        String contactPattern = "[6789][0-9]{9}";
        String username = etPhone.getText().toString().trim();

        boolean flag = true;
        if(username.length()==0) {
            flag = false;
            etPhone.setError("Phone is mandatory");
        }
        if(!username.matches(contactPattern)) {
            flag = false;
            etPhone.setError("Invalid value");
        }
        if(flag) {
            authenticate();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid details.", Toast.LENGTH_LONG).show();
        }
    }

    public void authenticate() {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Authenticating", "Please have patience..", true);
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("apiVersion", Config.API_VERSION);
            object.put("mobile",etPhone.getText().toString().trim());
            object.put("userType","patient");
            object.put("imei", prefManager.getImei());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Config.JSON_URL + "/send_otp";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("veer response ", response.toString());
                        progressDialog.dismiss();
                        try {
                            JSONObject result = response.getJSONObject("result");
                            if(result.getBoolean("ack")) {
                                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                                intent.putExtra("mobile", etPhone.getText().toString().trim());
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