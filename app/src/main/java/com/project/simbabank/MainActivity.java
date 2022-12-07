package com.project.simbabank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.simbabank.Activity.DepositActivity;
import com.project.simbabank.Activity.MoneyActivity;
import com.project.simbabank.Activity.PaymentsActivity;
import com.project.simbabank.Activity.TransferActivity;
import com.project.simbabank.Adapters.TransactionAdapter;
import com.project.simbabank.Model.TransactionModel;
import com.project.simbabank.Model.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    RecyclerView trans;
    TransactionAdapter transactionAdapter;
    ArrayList<TransactionModel> list;
    CardView card1, card, card3;
    TextView date,amount;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    Query databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amount = findViewById(R.id.amount_text);
        progressBar =findViewById(R.id.progressBar3);
        card1 = findViewById(R.id.c1);
        card = findViewById(R.id.c2);
        card3 = findViewById(R.id.c3);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PaymentsActivity.class));
            }
        });
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransferActivity.class));
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DepositActivity.class));
            }
        });
        date = findViewById(R.id.text_date);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        date.setText(currentDate);
        //Transactions
        trans = findViewById(R.id.transaction_rv);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("TransactionDetails");
        trans.setHasFixedSize(true);
        trans.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));
        list = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this,list);
        trans.setAdapter(transactionAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    TransactionModel transactionModel = dataSnapshot.getValue(TransactionModel.class);
                    list.add(transactionModel);
                }
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ///user details
        progressBar.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView fullNameTxt = findViewById(R.id.name_txt);
        final TextView accountTxt = findViewById(R.id.textView5);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile !=null){
                    String fullName = userProfile.fullName;
                    String account = userProfile.account;

                    fullNameTxt.setText(fullName);
                    accountTxt.setText(account);

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,"Something wrong happened!", Toast.LENGTH_SHORT).show();

            }
        });

    }
}