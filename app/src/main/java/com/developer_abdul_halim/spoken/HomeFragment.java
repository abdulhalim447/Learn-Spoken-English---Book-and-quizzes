package com.developer_abdul_halim.spoken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.developer_abdul_halim.spoken.R;

public class HomeFragment extends Fragment {

    private HashMap<String, String> hashMap;
    private ArrayList<HashMap<String, String>> arrayList;
    private ArrayList<HashMap<String, String>> filteredList;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView searchView;
    private myAdapter adapter;
    //---------nav drawer----------------------------

    Toolbar toolBar;
    NavigationView nav_View;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;


    //---------nav drawer----------------------------

    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);


        //------------------------nav drawer------------------------------

        toolBar = view.findViewById(R.id.toolBar);
        nav_View = view.findViewById(R.id.nav_View);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        nav_View = view.findViewById(R.id.nav_View);


        //-----------------------------------------------------------------

        progressBar.setVisibility(View.VISIBLE);

        //--------------------------------------------------------------------

        //load data is here--------------------------------------------------------------
        loadData();
        NavDrawer();


        //set up search view---------------------------------------------------------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });


        //=====================================================================


        return view;
    }


    //------------------Adapter is start now-----------------------------------------------------------

    private class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

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
            hashMap = filteredList.get(position);
            String title = hashMap.get("title");
            String desc = hashMap.get("desc");
            String url = hashMap.get("url");
            String img = hashMap.get("img");

            holder.tvTitle.setText(title);
            holder.tvDesc.setText(desc);


            //picasso is here--------------------------
            Picasso picassoInstance = Picasso.get();

            if (img != null && !img.isEmpty()) {
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
            return filteredList.size();
        }
    }//================================================= Adapter end =====================================


    //----------------------------------------------------------------------------------------------

    //load data from url and show it in recycler view
    private void loadData() {

        String url = "https://developerabdulhalim.xyz/Course%20Bazar%20BD/spoken.json";

        requestQueue = Volley.newRequestQueue(getActivity());

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);

                arrayList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
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

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        NoInternet(getActivity());
                    }
                }

                filteredList = new ArrayList<>(arrayList);
                adapter = new myAdapter();
                recyclerView.setAdapter(adapter);
                int numberOfColumns = 2;
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));  // GridLayoutManager
                //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));              //LinearLayoutManager
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                NoInternet(getActivity());

            }
        });

        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    //----------------------------------------------------------------------------------------------

    //filter the data based on the search query
    private void filter(String text) {
        filteredList.clear();

        for (HashMap<String, String> item : arrayList) {
            if (item.get("title").toLowerCase().contains(text.toLowerCase()) || item.get("desc").toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }
//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------


    private void NavDrawer() {
        //===========================================================================================================================

        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawar click event
        // Drawer item Click event ------
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.copyright_content) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, new CopyRight_Fragment());
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();


                } else if (id== R.id.about_developer) {

                        FragmentManager dev_fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction dev_fragmentTransaction = dev_fragmentManager.beginTransaction();
                        dev_fragmentTransaction.replace(R.id.frameLayout, new Developer_Fragment());
                        dev_fragmentTransaction.commit();

                        drawerLayout.closeDrawers();
                } else if (id==R.id.Rate ) {

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }

                    drawerLayout.closeDrawers();



                } else if (id==R.id.shareapp) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Please download the beautiful app COURSE BD on the Play Store: https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "Share using"));

                    drawerLayout.closeDrawers();

                } else if (id==R.id.Policy) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://sites.google.com/view/paidcoursefree1/home"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    drawerLayout.closeDrawers();

                }




                return false;
            }
        });
        //------------------------------


        //===========================================
    }


    //======================================================

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