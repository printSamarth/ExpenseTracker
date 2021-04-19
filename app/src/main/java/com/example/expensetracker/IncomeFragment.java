package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler view.

    private RecyclerView recyclerView;
    private TextView incomeTotal;
    // Update data.
    private EditText edtAmount, edtDescription;
    private Button btnUpdate, btnDelete;

    //private String type;
    private String description;
    private String spinnerType;
    private int amount;
    private String post_key;
    private String mediumType;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
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
        View myView =  inflater.inflate(R.layout.fragment_income, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotal = myView.findViewById(R.id.income_text_result);
        recyclerView = myView.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    Data data = ds.getValue(Data.class);
                    totalIncome+= data.getAmount();
                    String stToalVal = String.valueOf(totalIncome);
                    incomeTotal.setText(stToalVal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.income_recycler_data,
                MyViewHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {
                myViewHolder.setType(data.getType());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setAmount(data.getAmount());
                myViewHolder.setDescription(data.getDescription());
                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();

                        description = data.getDescription();
                        amount = data.getAmount();
                        updateDataItem();

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amt){
            TextView mAmt = mView.findViewById(R.id.amount_txt_income);
            String a = String.valueOf(amt);
            mAmt.setText(a);
        }

        private void setDescription(String description){
            TextView mDesc = mView.findViewById(R.id.desc_txt_income);
            mDesc.setText(description);
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.update_data_layout, null);
        mydialog.setView(myView);
        Spinner edtTypeSpinner = (Spinner) myView.findViewById(R.id.type_edt_spinner_update);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(edtTypeSpinner.getContext(), R.array.TypesIncome,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtTypeSpinner.setAdapter(adapter);

        Spinner mediumSpinner = myView.findViewById(R.id.medium_edt_spinner_update);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mediumSpinner.getContext(),
                R.array.medium, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mediumSpinner.setAdapter(adapter1);
        mediumSpinner.setOnItemSelectedListener(this);

        edtAmount = myView.findViewById(R.id.amount_edt);
        edtDescription = myView.findViewById(R.id.type_edt);
        btnUpdate = myView.findViewById(R.id.btnUpdate);
        btnDelete = myView.findViewById(R.id.btnDelete);

        //To add already present data in update dialog.
        edtDescription.setText(description);
        edtDescription.setSelection(description.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        AlertDialog dialog = mydialog.create();
        edtTypeSpinner.setOnItemSelectedListener(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = edtDescription.getText().toString().trim();
                String amount_string = edtAmount.getText().toString().trim();
                amount  = Integer.parseInt(amount_string);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, spinnerType, post_key,mDate, description, mediumType);
                mIncomeDatabase.child(post_key).setValue(data);
                dialog.dismiss();
                Toast.makeText(myView.getContext(), "Data updated", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
                Toast.makeText(myView.getContext(), "Data removed", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner1 = (Spinner) parent;
        Spinner spinner2 = (Spinner) parent;
        if(spinner1.getId() == R.id.type_edt_spinner) {
            spinnerType = spinner1.getItemAtPosition(position).toString();
        }
        else{
            mediumType = spinner2.getItemAtPosition(position).toString();
        }
        System.out.println("From on ItemSelected spinner Type" + spinnerType);
        System.out.println("From on ItemSelected medium Type" + mediumType);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}