package com.example.expensetracker;
//MPAndroid chart for piechart.
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.example.expensetracker.Model.Data;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalysisFragment extends Fragment {
    String type;// = {"Apr 10", "Apr 12"};
    Date startDate, endDate,tempDate;
    SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM dd, yyyy");
    Data data;
    DataSnapshot snapshottemp;
    String[] categories = {"Clothes", "Food & Dining", "Transport", "Entertainment", "Grocery",
            "Medical", "Electricity", "Electronics equipments"};
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
    private ImageButton btnDate;
    private Hashtable<String, Double> categorySum;
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
       categorySum = new Hashtable<String, Double>();

       for(String category : categories){
           double temp = 0;
           categorySum.put(category, temp);
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
       builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
       final MaterialDatePicker materialDatePicker = builder.build();

        btnDate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               materialDatePicker.show(getFragmentManager(), "MATERIAL_DATE_PICKER");

           }
       });



        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {

                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Pair selectedDates = (Pair) materialDatePicker.getSelection();
//              then obtain the startDate & endDate from the range
                        final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
//              assigned variables
                        startDate = rangeDate.first;
                        endDate = rangeDate.second;
//              Format the dates in ur desired display mode

//              Display it by setText
                       System.out.println("SELECTED DATE : " +  simpleFormat.format(startDate) + " Second : " + simpleFormat.format(endDate));
                       //String str_startDate = simpleFormat.format(startDate);


                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        //mShowSelectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                        //System.out.println("Selected Date is : " + materialDatePicker.getHeaderText());
                        //date = materialDatePicker.getHeaderText().split("-");
                        //System.out.println(materialDatePicker.toString());
                        // in the above statement, getHeaderText
                        // will return selected date preview from the
                        // dialog
                        total =0;
                        //categorySum.clear();
                        for(String category : categories){
                            double temp = 0;
                            categorySum.put(category, temp);
                        }
                        for(DataSnapshot ds: snapshottemp.getChildren()) {
                            data = ds.getValue(Data.class);
                            type = data.getType();
                            try {
                                tempDate = simpleFormat.parse(data.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if((tempDate.equals(startDate) || tempDate.after(startDate))
                                    && (tempDate.equals(endDate) || tempDate.before(endDate)))
                            {
                                total+= data.getAmount();
                                System.out.println("Total = " + total);
                                double tempSum = categorySum.get(type);
                                System.out.println("Tempsum = " + tempSum);
                                categorySum.put(type, data.getAmount() + tempSum);
                                System.out.println("In"+data.getAmount());
                                System.out.println("In "+ simpleFormat.format(tempDate));
                                System.out.println("Total:"+total);
                                //int count = categorySum.containsKey(type) ? categorySum.get(type) : 0;

                                System.out.println(categorySum);
                            }
                            else {}
                        }
                        loadData();
                    }

                });



       mExpenseDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshottemp = snapshot;
               for(DataSnapshot ds: snapshot.getChildren()){
                   data = ds.getValue(Data.class);
                   type = data.getType();
                   try {
                       tempDate = simpleFormat.parse(data.getDate());
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   total+= data.getAmount();
                   double tempSum = categorySum.get(type);
                   categorySum.put(type, data.getAmount() + tempSum);
                   System.out.println("In"+data.getAmount());
                   System.out.println("In "+ simpleFormat.format(tempDate));
                   System.out.println("Total:"+total);
                   //int count = categorySum.containsKey(type) ? categorySum.get(type) : 0;

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
        double max = 0;
        for(String key: categorySum.keySet()){
            double percentage;
            double temp = categorySum.get(key);
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