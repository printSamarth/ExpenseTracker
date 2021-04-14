package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.expensetracker.Model.Data;
import com.example.expensetracker.Model.StockData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Stocks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stocks extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String url = "http://10.0.2.2:5000/api/";
    private RecyclerView recyclerView;
    private DatabaseReference stocksDb;

    private TextView editUnits, editCode;
    private Button btnUpdate, btnDelete;
    private double amountDouble;

    private String code_stock;
    private int unit;
    private String post_key;

    private FirebaseAuth mAuth;
    private EditText code, units, amount;
    private Button btnSave;
    public Stocks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Stocks.
     */
    // TODO: Rename and change types and number of parameters
    public static Stocks newInstance(String param1, String param2) {
        Stocks fragment = new Stocks();
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
        View myView = inflater.inflate(R.layout.fragment_stocks, container, false);
        code = myView.findViewById(R.id.code_stocks);
        units = myView.findViewById(R.id.unit_stocks);
        btnSave = myView.findViewById(R.id.btn_save_stocks);
        amount = myView.findViewById(R.id.amount_txt_stocks);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        stocksDb = FirebaseDatabase.getInstance().getReference().child("StocksData").child(uid);
        recyclerView = myView.findViewById(R.id.recycler_id_stocks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = units.getText().toString().trim();
                if(TextUtils.isEmpty(temp)){
                    units.setError("Number of units required");
                    return;
                }
                String cd = code.getText().toString().trim();
                if(TextUtils.isEmpty(cd)){
                    code.setError("Company code required");
                    return;
                }
                int unit = Integer.parseInt(temp);
                String date = DateFormat.getDateInstance().format(new Date());


                RequestQueue queue = Volley.newRequestQueue(getContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url+cd,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Hello from onResponse");

                        try {
                            System.out.println(response.toString());
                            amountDouble = response.getDouble(cd);
                            System.out.println(amountDouble);
                            amountDouble*= unit;
                            StockData data = new StockData(unit, cd, date,amountDouble);
                            System.out.println("From object "+data.getAmount());
                            String id = stocksDb.push().getKey();
                            stocksDb.child(id).setValue(data);
                            Toast.makeText(getActivity(), "Data added.", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            System.out.println(e);
                            Toast.makeText(getActivity(), "Error in data adding.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Hello from onError");
                        System.out.println(error.toString());
                    }
                });

                queue.add(jsonObjectRequest);




            }
        });


        return myView;
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<StockData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<StockData, MyViewHolder>(
                StockData.class,
                R.layout.stocks_recycler_data,
                MyViewHolder.class,
                stocksDb
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, StockData stockData, int i) {
                myViewHolder.setCode(stockData.getCode());
                myViewHolder.setUnits(stockData.getUnits());
                myViewHolder.setDate(stockData.getDate());
                myViewHolder.setAmount(stockData.getAmount());
                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();
                        code_stock = stockData.getCode();
                        amountDouble = stockData.getAmount();

                        System.out.println("Hello from onClick");
                        unit = stockData.getUnits();
                        amountDouble*= unit;
                        updateDateItem();
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
        private void setAmount(double amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_stocks);
            String a = String.valueOf(amount);
            mAmount.setText(a);
        }
        private void setUnits(int units){
            TextView mType = mView.findViewById(R.id.units_txt_stocks);
            String a = String.valueOf(units);
            mType.setText(a);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_stocks);
            mDate.setText(date);
        }


        private void setCode(String code){
            TextView mDesc = mView.findViewById(R.id.code_txt_stocks);
            mDesc.setText(code);
        }
    }
    public void updateDateItem(){
        System.out.println("Hello start");
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_stocks_layout, null);
        mydialog.setView(myview);
        System.out.println("Hello between");
        editCode = myview.findViewById(R.id.code_edt);
        editUnits = myview.findViewById(R.id.unit_edt);

        editCode.setText(code_stock);
        String temp = Integer.toString(unit);
        editUnits.setText(temp);
        System.out.println("After ");
        btnUpdate = myview.findViewById(R.id.btnUpdateStock1);
        btnDelete = myview.findViewById(R.id.btnDeleteStock1);

        AlertDialog dialog = mydialog.create();
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stocksDb.child(post_key).removeValue();
                dialog.dismiss();
                Toast.makeText(myview.getContext(), "Data removed", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code_stock = editCode.getText().toString().trim();
                unit = Integer.parseInt(editUnits.getText().toString().trim());
                String date = DateFormat.getDateInstance().format(new Date());
                StockData data = new StockData(unit, code_stock, date, amountDouble);
                stocksDb.child(post_key).setValue(data);

                dialog.dismiss();
                Toast.makeText(myview.getContext(), "Data updated", Toast.LENGTH_SHORT).show();
            }

        });
        dialog.show();
        System.out.println("Hello end");
    }
}