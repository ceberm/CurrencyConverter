package com.example.tarea_3_cesar_bermudez.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.tarea_3_cesar_bermudez.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyFragment extends Fragment {

    private JSONObject jsonResponse;
    private double colon;
    private double egyptianPound;
    private double euro;
    private double libraTurka;
    private double actual;


    // Array of strings for ListView Title
    private String[] listviewTitle = new String[]{
             "CRC", "EGP", "EUR", "TRY",
    };


    private int[] listviewImage = new int[]{
            R.drawable.usdcrc,
            R.drawable.usdegp,
            R.drawable.usdeur,
            R.drawable.usdtry,
    };

    private String[] listviewShortDescription = new String[]{
            "Costa Rican Colón", "Egyptian Pound", "Euro", "Turkish Lira",
    };

    private final Double[] temporalValues = new Double[4];

    public CurrencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Get Json
        Bundle bundle = getArguments();
        String strResponse= (String) bundle.getSerializable("jsonResponse");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currency, container, false);

        try {
            jsonResponse = new JSONObject(strResponse);
            String success = jsonResponse.getString("success");
            JSONObject currencies = jsonResponse.getJSONObject("quotes");
            colon = currencies.getDouble("USDCRC");
            egyptianPound = currencies.getDouble("USDEGP");
            euro = currencies.getDouble("USDEUR");
            libraTurka = currencies.getDouble("USDTRY");

            temporalValues[0] = colon;
            temporalValues[1] = egyptianPound ;
            temporalValues[2] = euro ;
            temporalValues[3] = libraTurka;
            setupUI(view);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;

    }

    private void refreshList(View view){
        String[] label_ids = {"list_image", "list_country", "list_long_name", "list_currency",};
        int[] fields = {R.id.list_item_image, R.id.list_item_code, R.id.list_item_name, R.id.list_item_currency_value};
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        int places = 2;

        for (int i = 0; i < 4; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            BigDecimal bd = new BigDecimal(Double.toString(temporalValues[i]));
            bd = bd.setScale(places, RoundingMode.HALF_UP);

            hm.put("list_country", listviewTitle[i]);
            hm.put("list_long_name", listviewShortDescription[i]);
            hm.put("list_image", Integer.toString(listviewImage[i]));
            hm.put("list_currency", Double.toString(bd.doubleValue()));
            aList.add(hm);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(view.getContext(), aList, R.layout.list_element, label_ids, fields);
        ListView ListContent = (ListView) view.findViewById(R.id.list_view);
        ListContent.setAdapter(simpleAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void hideKeyBoard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setupUI(final View view){

        final EditText txtEdit = (EditText) view.findViewById(R.id.txt_dollars);

        txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    actual = Double.parseDouble(txtEdit.getText().toString());
                    if(actual != 1){
                        temporalValues[0] = colon * actual;
                        temporalValues[1] = egyptianPound * actual;
                        temporalValues[2] = euro * actual;
                        temporalValues[3] = libraTurka * actual;
                    }
                    refreshList(view);
                    hideKeyBoard(getContext(),txtEdit);
                }

            }
        });

        refreshList(view);

    }

}
