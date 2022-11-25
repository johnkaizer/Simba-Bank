package com.project.simbabank.Activity;

import static com.project.simbabank.Constants.BUSINESS_SHORT_CODE;
import static com.project.simbabank.Constants.CALLBACKURL;
import static com.project.simbabank.Constants.PARTYB;
import static com.project.simbabank.Constants.PASSKEY;
import static com.project.simbabank.Constants.TRANSACTION_TYPE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.simbabank.DarajaApiClient;
import com.project.simbabank.Model.AccessToken;
import com.project.simbabank.Model.STKPush;
import com.project.simbabank.Model.TransactionModel;
import com.project.simbabank.R;
import com.project.simbabank.Utils;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DepositActivity extends AppCompatActivity {
    TextView date, title;
    DatabaseReference dataRef;
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;

    @BindView(R.id.editText2)
    EditText mAmount;
    @BindView(R.id.editText1)
    EditText mPhone;
    @BindView(R.id.deposit)
    Button mPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        title = findViewById(R.id.textView14);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.
        getAccessToken();
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = mPhone.getText().toString();
                String amount = mAmount.getText().toString();
                performSTKPush(phone_number,amount);
                DepositDetails();
            }
        });
        date = findViewById(R.id.date_text);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        date.setText(currentDate);
        dataRef= FirebaseDatabase.getInstance().getReference().child("TransactionDetails");

    }

    private void performSTKPush(String phone_number, String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "Simba Bank", //Account reference
                "Deposit"  //Transaction description
        );


        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    private void DepositDetails() {
        String TransactionDate =  date.getText().toString();
        String TransactionTitle =  title.getText().toString();
        String TransactionAmount =  mAmount.getText().toString();

        TransactionModel deposit = new TransactionModel(TransactionTitle,TransactionAmount,TransactionDate);
        dataRef.push().setValue(deposit);
        startActivity(new Intent(DepositActivity.this, SuccessActivity.class));
        finish();

    }
}