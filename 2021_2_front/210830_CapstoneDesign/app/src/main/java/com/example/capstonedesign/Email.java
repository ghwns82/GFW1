package com.example.capstonedesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstonedesign.retrofit.RetrofitClient;
import com.example.capstonedesign.retrofit.ValidateResponse;
import com.example.capstonedesign.retrofit.initMyApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Email extends AppCompatActivity {

    private EditText email;
    private initMyApi initMyApi;
    private Button check;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        check = findViewById(R.id.check);
        email = findViewById(R.id.email);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckEmail();
            }
        });

    }

    public void CheckEmail() {
        String userEmail = email.getText().toString().trim();

        sharedPreferences = getSharedPreferences("email", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        initMyApi = RetrofitClient.getRetrofitInterface();

        Call<ValidateResponse> call = initMyApi.getValidateResponse(userEmail);

        call.enqueue(new Callback<ValidateResponse>() {
            @Override
            public void onResponse(Call<ValidateResponse> call, Response<ValidateResponse> response) {
                if(response.code() == 201) {
                    ValidateResponse result = response.body();
                    String status = result.getStatus();
                    String token = result.getToken();
                    PreferenceManager.setString(getApplicationContext(),"token",token);
                    editor.putString("token", token);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), Password.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(),"등록된 이메일이 맞습니다.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"등록된 이메일이 아닙니다.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValidateResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"다시 시도해주세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}