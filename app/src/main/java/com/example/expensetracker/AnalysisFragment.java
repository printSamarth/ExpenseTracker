package com.example.expensetracker;
//MPAndroid chart for piechart.
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

//import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.expensetracker.Model.Data;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalysisFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int total;
    private PieChart pieChart;
    private DatabaseReference mExpenseDatabase;
    private FirebaseAuth mAuth;
    private Button btnDate;
    private Hashtable<String, Integer> categorySum;
    public AnalysisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalysisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalysisFragment newInstance(String param1, String param2) {
        AnalysisFragment fragment = new AnalysisFragment();
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
       View myView = inflater.inflate(R.layout.fragment_analysis, container, false);
       categorySum = new Hashtable<String, Integer>();
       String[] categories = {"Clothes", "Food & Dining", "Transport", "Entertainment", "Grocery", "Medical", "Electricity"};
       for(String category : categories){
           categorySum.put(category, 0);
       }
       pieChart = myView.findViewById(R.id.pie_chart);
       mAuth = FirebaseAuth.getInstance();
       FirebaseUser mUser = mAuth.getCurrentUser();
       String uid = mUser.getUid();
       setupPieChart();
       mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

       btnDate = myView.findViewById(R.id.btnDate);
       MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
       builder.setTitleText("Select range");
       final MaterialDatePicker materialDatePicker = builder.build();

       btnDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });


       mExpenseDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {

               for(DataSnapshot ds: snapshot.getChildren()){
                   Data data = ds.getValue(Data.class);
                   String type = data.getType();
                   total+= data.getAmount();
                   System.out.println("In"+data.getAmount());
                   System.out.println("Total:"+total);
                   //int count = categorySum.containsKey(type) ? categorySum.get(type) : 0;
                   int tempSum = categorySum.get(type);
                   categorySum.put(type, data.getAmount() + tempSum);
                   System.out.println(categorySum);
               }
               loadData();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
       //System.out.println(categorySum);


       return myView;
    }

    public void setupPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);

    }
    public void loadData(){
        //System.out.println("From Load Data: "+categorySum);
        //System.out.println("From Load Data: "+total);
        ArrayList<PieEntry> entries = new ArrayList<>();
        String maxSpending = "";
        int max = 0;
        for(String key: categorySum.keySet()){
            double percentage;
            int temp = categorySum.get(key);
            if(temp == 0){
                continue;
            }
            if(max < temp){
                max = temp;
                maxSpending = key;
            }
            percentage = ((double)temp) / (double)total;
            entries.add(new PieEntry((float)percentage, key));
        }
        pieChart.setCenterText("Highest spending on " + maxSpending);

        ArrayList<Integer> colors = new ArrayList<>();
        for(int color: ColorTemplate.MATERIAL_COLORS){
            colors.add(color);
        }
        for(int color: ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }
}