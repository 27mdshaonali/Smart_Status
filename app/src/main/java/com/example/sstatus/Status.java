package com.example.sstatus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Status extends AppCompatActivity {

    // Static Variables
    public static JSONArray RESPONSE = null;
    public static String DASHBOARD_TITLE = "";
    // Data List
    private final ArrayList<HashMap<String, String>> statusList = new ArrayList<>();
    TextView statusTitle;
    // UI Elements
    private ListView listView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_status);

        // Adjust Layout for System Bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeView();
    }

    @SuppressLint("SetTextI18n")
    private void initializeView() {
        listView = findViewById(R.id.listView);
        statusTitle = findViewById(R.id.statusTitle);

        statusTitle.setText(DASHBOARD_TITLE + " Quotes");

        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        parseData();
    }

    private void parseData() {
        if (RESPONSE == null) {
            Toast.makeText(this, "Response is null", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            statusList.clear(); // Clear previous data

            for (int i = 0; i < RESPONSE.length(); i++) {
                JSONObject jsonObject = RESPONSE.getJSONObject(i);

                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                String type = jsonObject.getString("type");
                String quotes_bg = jsonObject.getString("bg");

//                // Filtering data based on dashboard title
//                if ((DASHBOARD_TITLE.equalsIgnoreCase("Sad") && type.equalsIgnoreCase("love")) ||
//                        (DASHBOARD_TITLE.equalsIgnoreCase("Motivational Quotes") && type.equalsIgnoreCase("sad"))) {
//                    HashMap<String, String> itemMap = new HashMap<>();
//                    itemMap.put("type", type);
//                    itemMap.put("title", title);
//                    itemMap.put("content", content);
//                    statusList.add(itemMap);
//                }


                // Filtering data based on dashboard title
                if (DASHBOARD_TITLE.equalsIgnoreCase(type)) {
                    HashMap<String, String> itemMap = new HashMap<>();
                    itemMap.put("type", type);
                    itemMap.put("title", title);
                    itemMap.put("content", content);
                    itemMap.put("bg", quotes_bg);
                    statusList.add(itemMap);
                }


            }

            Toast.makeText(this, "Dashboard Title: " + DASHBOARD_TITLE, Toast.LENGTH_SHORT).show();
            myAdapter.notifyDataSetChanged(); // Notify adapter about data changes

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return statusList.size();
        }

        @Override
        public Object getItem(int i) {
            return statusList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint("ViewHolder") View myView = layoutInflater.inflate(R.layout.content_status, viewGroup, false);

            // UI Elements
            ImageView imageView = myView.findViewById(R.id.contentImageView);
            TextView textView = myView.findViewById(R.id.contentTextView);

            // Setting Data
            HashMap<String, String> hashMap = statusList.get(i);
            textView.setText(hashMap.get("content"));
            Picasso.get().load(hashMap.get("bg")).placeholder(R.drawable.shaon).into(imageView);

            return myView;
        }
    }
}