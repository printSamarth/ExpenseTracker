package com.example.expensetracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {
    //Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;


    //Recycler view.

    private RecyclerView recyclerView;
    private TextView expenseTotal;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        View myview =  inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        expenseTotal= myview.findViewById(R.id.expense_text_result);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalIncome = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    Data data = ds.getValue(Data.class);
                    totalIncome+= data.getAmount();
                    String stToalVal = String.valueOf(totalIncome);
                    expenseTotal.setText(stToalVal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myview;
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.expense_recycler_data,
                MyViewHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {
                myViewHolder.setAmount(data.getAmount());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
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
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setAmount(int amt){
            TextView mAmt = mView.findViewById(R.id.amount_txt_expense);
            String a = String.valueOf(amt);
            mAmt.setText(a);
        }
    }
}