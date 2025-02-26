package com.example.sstatus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import soup.neumorphism.NeumorphTextView;

public class MainActivity extends AppCompatActivity {

    String dashboard_url = "https://codecanvas.top/Smart%20Status/JSon/Dashboard%20Titles%20and%20Images.json";
    String status_url = "https://codecanvas.top/Smart%20Status/JSon/All%20Status.json";
    JSONArray STATUS_RESPONSE;
    ArrayList<HashMap<String, String>> dashboardList = new ArrayList<>();
    ArrayList<HashMap<String, String>> statusList = new ArrayList<>();
    SearchView searchView;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeView();
    }

    public void initializeView() {
        gridView = findViewById(R.id.gridView);
        searchView = findViewById(R.id.searchView);
        searchViewImplementation();
        parseData();
    }

    public void parseData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, dashboard_url, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", jsonObject.getString("type"));
                    hashMap.put("dashboardItemImg", jsonObject.getString("icon"));
                    hashMap.put("dashboardItemTle", jsonObject.getString("title"));
                    dashboardList.add(hashMap);
                }

                MyAdapter myAdapter = new MyAdapter(dashboardList);
                gridView.setAdapter(myAdapter);

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Dashboard JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> Toast.makeText(MainActivity.this, "Dashboard Error", Toast.LENGTH_SHORT).show());

        JsonArrayRequest arrayRequest2 = new JsonArrayRequest(Request.Method.GET, status_url, null, response -> {
            try {
                for (int x = 0; x < response.length(); x++) {
                    JSONObject jsonObject = response.getJSONObject(x);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title2", jsonObject.getString("title"));
                    hashMap.put("content", jsonObject.getString("content"));
                    statusList.add(hashMap);

                    STATUS_RESPONSE = response;
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Status JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> Toast.makeText(MainActivity.this, "Status Error", Toast.LENGTH_SHORT).show());

        requestQueue.add(arrayRequest);
        requestQueue.add(arrayRequest2);

    }

    public class MyAdapter extends BaseAdapter implements Filterable {
        ArrayList<HashMap<String, String>> dataList;
        ArrayList<HashMap<String, String>> fullList;
        Filter filter;

        public MyAdapter(ArrayList<HashMap<String, String>> list) {
            this.dataList = new ArrayList<>(list);
            this.fullList = new ArrayList<>(list);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint("ViewHolder") View myView = layoutInflater.inflate(R.layout.dashboard_item_layout, viewGroup, false);

            RoundedImageView imageView = myView.findViewById(R.id.imageView1);
            NeumorphTextView textView = myView.findViewById(R.id.dashboardItemTittle);

            LinearLayout linearLayout = myView.findViewById(R.id.cardLinear);

            HashMap<String, String> hashMap = dataList.get(i);

            String type = hashMap.get("type");
            if (type.equals("dashboard")) {
                textView.setText(hashMap.get("dashboardItemTle"));
                Picasso.get().load(hashMap.get("dashboardItemImg")).into(imageView);
            }


            linearLayout.setOnClickListener(v -> {

                if (STATUS_RESPONSE != null) {

                    String dashTitle = hashMap.get("dashboardItemTle");

                    Status.RESPONSE = STATUS_RESPONSE;
                    Status.DASHBOARD_TITLE = dashTitle;
                    startActivity(new Intent(MainActivity.this, Status.class));

                }

            });

            return myView;
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

                        if (constraint == null || constraint.length() == 0) {
                            filteredList.addAll(fullList);
                        } else {
                            String filterPattern = constraint.toString().toLowerCase().trim();
                            for (HashMap<String, String> item : fullList) {
                                if (item.get("dashboardItemTle").toLowerCase().contains(filterPattern)) {
                                    filteredList.add(item);
                                }
                            }
                        }

                        results.values = filteredList;
                        results.count = filteredList.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        dataList.clear();
                        dataList.addAll((ArrayList<HashMap<String, String>>) results.values);
                        notifyDataSetChanged();
                    }
                };
            }
            return filter;
        }
    }

    public void searchViewImplementation() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((MyAdapter) gridView.getAdapter()).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((MyAdapter) gridView.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }


}
