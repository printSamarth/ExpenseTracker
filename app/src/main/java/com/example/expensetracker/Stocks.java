package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private static AutoCompleteTextView code;
    private static final String[] STOCKS = new String[]{
            "Dabur", "Tcs", "20microns", "3IINFOTECH","3MINDIA","8KMILES","A2ZINFRA","AARTIDRUGS","AARTIIND"
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String url = "http://10.0.2.2:5000/api/";
    private RecyclerView recyclerView;
    private DatabaseReference stocksDb;

    private TextView stockResult;
    private static double totalStocks;
    private TextView editUnits, editCode;
    private Button btnUpdate, btnDelete;
    private double amountDouble;

    private String code_stock;
    private int unit;
    private static final String TAG = Stocks.class.getSimpleName();
    private String post_key;

    private FirebaseAuth mAuth;
    //private EditText code;
    private EditText units, amount;
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
        Log.i(TAG, "Inside on create method");
        View myView = inflater.inflate(R.layout.fragment_stocks, container, false);
        //code = myView.findViewById(R.id.code_stocks);
        code = myView.findViewById(R.id.code_stocks_auto);
        units = myView.findViewById(R.id.unit_stocks);
        btnSave = myView.findViewById(R.id.btn_save_stocks);
        amount = myView.findViewById(R.id.amount_txt_stocks);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = "";

        ArrayAdapter<String> adapterCode = new ArrayAdapter<String>(myView.getContext(),
                android.R.layout.simple_list_item_1, STOCKS);
        code.setAdapter(adapterCode);

        try {
            uid = mUser.getUid();
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }

        try {
            stocksDb = FirebaseDatabase.getInstance().getReference().child("StocksData").child(uid);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
        recyclerView = myView.findViewById(R.id.recycler_id_stocks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

//        stocksDb.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                double totalStock = 0;
//                for(DataSnapshot ds: snapshot.getChildren()){
//
//                    StockData data = ds.getValue(StockData.class);
//                    totalStock+= (data.getAmount()*data.getUnits());
//                    System.out.println("Total "+totalStock);
//                    stockResult.setText(String.valueOf(totalStock));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = units.getText().toString().trim();
                Log.i(TAG, "Stocks unit - "+temp);
                if(TextUtils.isEmpty(temp)){
                    units.setError("Number of units required");
                    Log.e(TAG, "Please enter stocks quantity");
                    return;
                }
                String cd = code.getText().toString().trim();
                Log.i(TAG, "Company Share Code - "+cd);
                if(TextUtils.isEmpty(cd)){
                    code.setError("Company code required");
                    Log.e(TAG, "Please enter Company code");
                    return;
                }
                int unit = Integer.parseInt(temp);
                String date = DateFormat.getDateInstance().format(new Date());
                Log.i(TAG, "Date - "+date);


                RequestQueue queue = Volley.newRequestQueue(getContext());
                Log.i(TAG, "Sending API request");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url+cd,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Hello from onResponse");

                        try {
                            System.out.println(response.toString() + " Units "+ unit);
                            amountDouble = response.getDouble(cd);
                            amountDouble = amountDouble * unit;
                            Log.i(TAG, "Stock Amount - "+amountDouble);
                            System.out.println(amountDouble);
                            Log.i(TAG, "Adding stocks data to database");
                            StockData data = new StockData(unit, cd, date,amountDouble);
                            System.out.println("From object "+data.getAmount());

                                String id = stocksDb.push().getKey();
                                stocksDb.child(id).setValue(data);

                            Log.i(TAG, "Stocks data Added");
                            Toast.makeText(getActivity(), "Data added.", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            System.out.println(e);
                            Log.e(TAG, e.toString());
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
            //Loading data in recycler view.
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, StockData stockData, int i) {
                myViewHolder.setCode(stockData.getCode());
                myViewHolder.setUnits(stockData.getUnits());
                myViewHolder.setDate(stockData.getDate());
                myViewHolder.setAmount(stockData.getCode(), stockData.getUnits());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();
                        code_stock = stockData.getCode();
                        RequestQueue queue = Volley.newRequestQueue(getContext());

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url+code_stock,
                                null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("Hello from onResponse");
                                System.out.println(response.toString());
                                try {
                                    amountDouble = response.getDouble(code_stock);
                                }
                                catch (Exception e){
                                    Log.e(TAG, e.toString());
                                    System.out.println(e);
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


                        System.out.println("Hello from onClick");
                        unit = stockData.getUnits();
                        amountDouble = amountDouble * unit;
                        Log.i(TAG,"Inside Update function");
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


        private void setAmount(String code, int units){
            TextView mAmount = mView.findViewById(R.id.amount_txt_stocks);
            //TextView total = mView.findViewById(R.id.abcd);
            //String a = String.valueOf(amount);
            RequestQueue queue = Volley.newRequestQueue(mView.getContext());
            String url = "http://10.0.2.2:5000/api/";

            Log.i(TAG,"Making rest api call");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url+code,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("Hello from onResponse");

                    try {
                        double amountDouble = response.getDouble(code);
                        amountDouble = amountDouble * units;
                        System.out.println(amountDouble);
                        totalStocks+= amountDouble;
                        System.out.println("Total" + totalStocks);
                        //total.setText("Abcd");
                        mAmount.setText(String.valueOf(amountDouble));
                    }
                    catch (Exception e){
                        Log.e(TAG, e.toString());
                        System.out.println(e);
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
        Log.i(TAG,"Inside Update Function");
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
                Log.i(TAG, "Inside Delete Function");
                try {
                    stocksDb.child(post_key).removeValue();
                }
                catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                Log.i(TAG, "Stock data Deleted");
                dialog.dismiss();
                Toast.makeText(myview.getContext(), "Data removed", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Inside Update Function");
                code_stock = editCode.getText().toString().trim();
                unit = Integer.parseInt(editUnits.getText().toString().trim());
                String date = DateFormat.getDateInstance().format(new Date());
                Log.i(TAG, "Quantity -"+unit);
                Log.i(TAG, "Date -"+date);
                Log.i(TAG, "Amount -"+amountDouble);

                StockData data = new StockData(unit, code_stock, date, amountDouble);
                try {
                    stocksDb.child(post_key).setValue(data);
                }
                catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                Log.i(TAG, "Stock data Updated");
                dialog.dismiss();
                Toast.makeText(myview.getContext(), "Data updated", Toast.LENGTH_SHORT).show();
            }

        });
        dialog.show();
        System.out.println("Hello end");
    }
}