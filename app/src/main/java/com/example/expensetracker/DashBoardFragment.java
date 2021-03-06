package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.Model.Data;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView netBankText, netCashText;

    private int position;
    // DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mBalance;

    private double netBank=0, netCash=0;
    private static double globalBank, globalCash;
    private double incomeCash, incomeBank, expenseBank, expenseCash;
    //Dashboard income and expense.
    private TextView totalincomeresult, totalexpenseresult;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    private TextView fab_stock_txt;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isOpen = false;
    private static final String TAG = DashBoardFragment.class.getSimpleName();
    private String spinnerType, mediumType;
    private Animation fadOpen, fadClose;
    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = "";
        try{
            uid = mUser.getUid();
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
        try {
            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
            mBalance = FirebaseDatabase.getInstance().getReference().child("Balance").child(uid);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }

        //Connect floating button to layout
        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_button);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_button);
        

        //Connect text

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        //Connect total textview
        totalincomeresult = myview.findViewById(R.id.income_set_result);
        totalexpenseresult = myview.findViewById(R.id.expense_set_result);
        netBankText = myview.findViewById(R.id.bank_set_result);
        netCashText = myview.findViewById(R.id.cash_set_result);

        //Global balance
//        String tempCash = netCashText.getText().toString();
//        double cash  = Double.parseDouble(tempCash);
//        globalCash = cash;
//
//        String tempBank = netBankText.getText().toString();
//        double bank = Double.parseDouble(tempBank);
//        globalBank = bank;


        //Animation Connect.
        fadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();

                if(isOpen){
                    //Close animation
                    fab_income_btn.startAnimation(fadClose);
                    fab_expense_btn.startAnimation(fadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);
                    fab_income_txt.startAnimation(fadClose);
                    fab_expense_txt.startAnimation(fadClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }
                else{
                    fab_income_btn.startAnimation(fadOpen);
                    fab_expense_btn.startAnimation(fadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);
                    fab_income_txt.startAnimation(fadOpen);
                    fab_expense_txt.startAnimation(fadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });
        //calculate total amount.
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                incomeCash = 0;
                incomeBank = 0;
                for(DataSnapshot m: snapshot.getChildren()){

                    Data data = m.getValue(Data.class);
                    total+= data.getAmount();
                    try{
                        if(data.getMedium().equals("Bank")) {
                            //globalBank+= data.getAmount();
                            incomeBank+= data.getAmount();
                        }
                        else{
                            //globalCash+=data.getAmount();
                            incomeCash+= data.getAmount();
                        }
                        String temp = String.valueOf(total);
                        totalincomeresult.setText(temp);
                    }
                    catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                }
                globalBank = incomeBank - expenseBank;
                globalCash = incomeCash - expenseCash;
                Log.i(TAG, "mIncome "+position++);
                Log.i(TAG, "Bank Balance  "+globalBank);
                Log.i(TAG, "Cash Left "+globalCash);

                netBankText.setText(String.valueOf(globalBank));
                netCashText.setText(String.valueOf(globalCash));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                expenseBank = 0;
                expenseCash = 0;
                for(DataSnapshot m: snapshot.getChildren()){

                    Data data = m.getValue(Data.class);
                    total+= data.getAmount();
                    try {
                        if (data.getMedium().equals("Bank")) {
                            //globalBank-= data.getAmount();
                            expenseBank += data.getAmount();
                        } else {
                            //globalCash-=data.getAmount();
                            expenseCash += data.getAmount();
                        }
                        String temp = String.valueOf(total);
                        totalexpenseresult.setText(temp);
                    }
                    catch (Exception e){
                        Log.e("DashBoardFragment", e.toString());
                    }
                }

                globalBank = incomeBank - expenseBank;
                globalCash = incomeCash - expenseCash;
                Log.i(TAG, "mExpense "+position++);
                Log.i(TAG, "Bank Balance "+globalBank);
                Log.i(TAG, "Cash Left "+globalCash);
                netBankText.setText(String.valueOf(globalBank));
                netCashText.setText(String.valueOf(globalCash));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myview;
    }
    //Floating button animation
    public void animationFtbutton(){
        if(isOpen){
            //Close animation
            fab_income_btn.startAnimation(fadClose);
            fab_expense_btn.startAnimation(fadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_txt.startAnimation(fadClose);
            fab_expense_txt.startAnimation(fadClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }
        else{
            fab_income_btn.startAnimation(fadOpen);
            fab_expense_btn.startAnimation(fadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);
            fab_income_txt.startAnimation(fadOpen);
            fab_expense_txt.startAnimation(fadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }
    private void addData(){
        //Fab button income

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }
    public void incomeDataInsert(){
        Log.i(TAG,"Inside Income data insert function");
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();
        EditText edtAmount = myview.findViewById(R.id.amount_edt);
        EditText edtType = myview.findViewById(R.id.type_edt);
        Spinner edtTypeSpinner = (Spinner) myview.findViewById(R.id.type_edt_spinner);
        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(edtTypeSpinner.getContext(), R.array.TypesIncome,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtTypeSpinner.setAdapter(adapter);
        edtTypeSpinner.setOnItemSelectedListener(this);

        Spinner mediumSpinner = myview.findViewById(R.id.medium_edt_spinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mediumSpinner.getContext(),
                R.array.medium, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mediumSpinner.setAdapter(adapter1);
        mediumSpinner.setOnItemSelectedListener(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Inserting Expense Transaction data");
                String description = edtType.getText().toString().trim();
                String amnt = edtAmount.getText().toString().trim();

                if(TextUtils.isEmpty(description)){
                    edtType.setError("Description required");
                    return;
                }
                if(TextUtils.isEmpty(amnt)){
                    edtAmount.setError("Amount required");
                    return;
                }
                int intamount = Integer.parseInt(amnt);
                //Generate random key
                String id = "";
                try {
                    id = mIncomeDatabase.push().getKey();
                }
                catch (Exception e){
                    Log.e(TAG, e.toString());
                }

                String mDate = DateFormat.getDateInstance().format(new Date());
                Log.i(TAG,"Amount - "+amnt);
                Log.i(TAG,"Description - "+description);
                Log.i(TAG,"Date - "+mDate);
                Log.i(TAG,"Income Type- "+spinnerType);
                Log.i(TAG,"Payment Medium - "+mediumType);
                Data data = new Data(intamount, spinnerType, id, mDate, description, mediumType);
                try {
                    mIncomeDatabase.child(id).setValue(data);
                }
                catch (Exception e){
                    Log.e(TAG, e.toString());
                }
                Log.i(TAG,"Income data Inserted");
                Toast.makeText(getActivity(), "Data added.", Toast.LENGTH_SHORT).show();

                animationFtbutton();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationFtbutton();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){
        Log.i(TAG,"Inside Expense data insert function");
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        EditText edtAmount = myview.findViewById(R.id.amount_edt);
        EditText edtType = myview.findViewById(R.id.type_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        Spinner edtTypeSpinner = (Spinner) myview.findViewById(R.id.type_edt_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(edtTypeSpinner.getContext(), R.array.TypesExpense,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtTypeSpinner.setAdapter(adapter);
        edtTypeSpinner.setOnItemSelectedListener(this);

        Spinner mediumSpinner = myview.findViewById(R.id.medium_edt_spinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mediumSpinner.getContext(),
                R.array.medium, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mediumSpinner.setAdapter(adapter1);
        mediumSpinner.setOnItemSelectedListener(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Inserting Expense Transaction data");
                String tmAmount = edtAmount.getText().toString().trim();
                String tmDescription = edtType.getText().toString().trim();
                if(TextUtils.isEmpty(tmDescription)){
                    edtType.setError("Description required");
                    return;
                }
                if(TextUtils.isEmpty(tmAmount)){
                    edtAmount.setError("Amount required");
                    return;
                }
                int intamount = Integer.parseInt(tmAmount);
                String id ="";
                try {
                    id = mExpenseDatabase.push().getKey();
                }
                catch (Exception e){
                    Log.e(TAG, e.toString());
                }

                String mDate = DateFormat.getDateInstance().format(new Date());
                Log.i(TAG,"Amount - "+tmAmount);
                Log.i(TAG,"Description - "+tmDescription);
                Log.i(TAG,"Date - "+mDate);
                Log.i(TAG,"Expense Type- "+spinnerType);
                Log.i(TAG,"Payment Medium - "+mediumType);


                System.out.println("Frooooommmmmm "+mediumType);
                Data data = new Data(intamount, spinnerType, id, mDate, tmDescription, mediumType);
                try {
                    mExpenseDatabase.child(id).setValue(data);
                }
                catch (Exception e){
                    Log.e(TAG, e.toString());
                }
                Log.i(TAG,"Expense data Inserted");
                Toast.makeText(getActivity(), "Data added.", Toast.LENGTH_SHORT).show();

                animationFtbutton();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationFtbutton();
                dialog.dismiss();
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
            Log.i(TAG,"type - "+spinnerType);
        }
        else{
            mediumType = spinner2.getItemAtPosition(position).toString();
            Log.i(TAG,"Payment Medium - "+mediumType);
        }
        System.out.println("From on ItemSelected spinner Type" + spinnerType);
        System.out.println("From on ItemSelected medium Type" + mediumType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}