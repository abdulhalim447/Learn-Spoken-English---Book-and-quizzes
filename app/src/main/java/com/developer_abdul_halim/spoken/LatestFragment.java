package com.developer_abdul_halim.spoken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LatestFragment extends Fragment {

    private HashMap<String, String> hashMap;
    private ArrayList<HashMap<String, String>> arrayList;

    private RecyclerView recyclerView;
    ProgressBar progressBar;
    //json request declare publicly============
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;

    //======================================
    public LatestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_latest, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        // load data here
        loadData();


        return view;
    }

    //----------------------------------------------------------------------------------------------

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc;
            CardView itemClick;
            ImageView bookImg;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDesc = itemView.findViewById(R.id.tvDesc);
                bookImg = itemView.findViewById(R.id.bookImg);
                itemClick = itemView.findViewById(R.id.itemClick);

            }
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View myView = inflater.inflate(R.layout.recycler_item, parent, false);

            return new ViewHolder(myView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            hashMap = arrayList.get(position);
            String title = hashMap.get("title");
            String desc = hashMap.get("desc");
            String url = hashMap.get("url");
            String img = hashMap.get("img");

            holder.tvTitle.setText(title);
            holder.tvDesc.setText(desc);
            //picasso is here--------------------------
            Picasso picassoInstance = Picasso.get();

            if(img != null && !img.isEmpty()) {
                picassoInstance.load(img).into(holder.bookImg);
            } else {
                picassoInstance.load(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.bookImg);
            } //picasso is here--------------------------



            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            holder.tvDesc.setBackgroundColor(color);

            holder.itemClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), BookConfirmation.class);
                    intent.putExtra("pdflink", url);
                    intent.putExtra("Title", title);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    //----------------------------------------------------------------------------------------------

    private void loadData() {

        arrayList = new ArrayList<>();

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://developerabdulhalim.xyz/Course%20Bazar%20BD/grammar.json";

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        progressBar.setVisibility(View.GONE);
                        // Handle the response here.
                        try {
                            // Iterate over the JSON array and parse each object.
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String desc = jsonObject.getString("desc");
                                String url = jsonObject.getString("url");
                                String img = jsonObject.getString("img");

                                hashMap = new HashMap<>();
                                hashMap.put("title", title);
                                hashMap.put("desc", desc);
                                hashMap.put("url", url);
                                hashMap.put("img", img);
                                arrayList.add(hashMap);
                            }

                            if (arrayList.size() > 0) {
                                RecyclerView.Adapter myAdapter = new MyAdapter();
                                recyclerView.setAdapter(myAdapter);
                                int numberOfColumns = 2;
                                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));  // GridLayoutManager
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            NoInternet(getActivity());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        NoInternet(getActivity());
                        // Handle errors here.
                    }
                });


        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }// load data end here ===========================================================


    private void NoInternet(Activity activity) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        View mView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        Button cancleBTN = mView.findViewById(R.id.cancleBTN);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(false);

        cancleBTN.setText("Exit App?");
        mView.findViewById(R.id.cancleBTN).setOnClickListener(v -> {
            activity.finishAffinity();
            alertDialog.dismiss();
        });

        mView.findViewById(R.id.okBTN).setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(jsonArrayRequest);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }//=================================================================================


}