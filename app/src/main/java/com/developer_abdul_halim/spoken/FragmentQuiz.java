package com.developer_abdul_halim.spoken;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FragmentQuiz extends Fragment {

    GridView gridView;
    HashMap<String, String> hashMap;
    private SharedPreferences sharedPreferences;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    public FragmentQuiz() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        gridView = view.findViewById(R.id.gridView);

        TextView tvScore = view.findViewById(R.id.tvScore);


        sharedPreferences = getContext().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        String lastScore = sharedPreferences.getString("savedScore", "No Data");

        tvScore.setText(lastScore);

        initializeCategoryStatus(sharedPreferences);


        createTable();

        MyAdapter myAdapter = new MyAdapter();
        gridView.setAdapter(myAdapter);

        return view;
    }


    private void initializeCategoryStatus(SharedPreferences sharedPreferences) {
        if (!sharedPreferences.contains("Category1Completed")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Category0Completed", true); // "Live Exam" is unlocked by default
            editor.putBoolean("Category1Completed", false); // "Test Exam 1" is locked by default
            editor.putBoolean("Category2Completed", false); // "Test Exam 3" is locked by default
            editor.putBoolean("Category3Completed", false); // "Test Exam 4" is locked by default
            editor.apply();
        }
    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item, parent, false);

            ImageView imgIcon = view.findViewById(R.id.itemCover);
            TextView lineOne = view.findViewById(R.id.lineOne);
            LinearLayout layItem = view.findViewById(R.id.layItem);
            ImageView lockIcon = view.findViewById(R.id.lockIcon);


            HashMap<String, String> hashMap = arrayList.get(position);
            String Title = hashMap.get("Title");
            String icon = hashMap.get("icon");

            if (isCategoryUnlocked(position)) {
                lockIcon.setImageResource(R.drawable.unlock_icon);
            } else {
                lockIcon.setImageResource(R.drawable.lock_icon);
            }

            lineOne.setText(Title);

            if (imgIcon != null && icon != null) {
                int drawable = Integer.parseInt(icon);
                imgIcon.setImageResource(drawable);
            }


//=============================================================================================
//=============================================================================================
            // Intent is here=======================


            List<String> quizUrls = Arrays.asList(
                    "https://developerabdulhalim.xyz/quiz_app/quiz.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiztwo.json",
                    "https://developerabdulhalim.xyz/quiz_app/quizthree.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_four.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_five.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_six.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_seven.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_eight.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_nine.json",
                    "https://developerabdulhalim.xyz/quiz_app/quiz_ten.json"
                    // ... add other URLs here ...
            );


            // Intent is here
            layItem.setOnClickListener(v -> {
                if (isCategoryUnlocked(position)) {
                    QuestionCollection.JSONurl = quizUrls.get(position);
                    Intent intent = new Intent(getContext(), QuestionCollection.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Please! Complete the previous category first", Toast.LENGTH_LONG).show();
                }
            });


            return view;
        }

    }


    private boolean isCategoryUnlocked(int position) {
        if (position == 0) {
            return true;  // The 0th position is always unlocked.
        }

        //final long TWELVE_HOURS_IN_MILLIS = 5 * 60 * 1000; // lock after five munites
        final long TWELVE_HOURS_IN_MILLIS = 12 * 60 * 60 * 1000; // lock after 12 hoours
        boolean isCompleted = sharedPreferences.getBoolean("Category" + position + "Completed", false);
        long completionTime = sharedPreferences.getLong("Category" + position + "CompletionTime", 0);

        return isCompleted && (System.currentTimeMillis() - completionTime <= TWELVE_HOURS_IN_MILLIS);
    }


    private void createTable() {
        hashMap = new HashMap<>();
        hashMap.put("Title", "Live Quiz");
        hashMap.put("icon", String.valueOf(R.drawable.live_quiz)); // img1 is a placeholder, replace with your image resource name
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 2");
        hashMap.put("icon", String.valueOf(R.drawable.exam1));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 3");
        hashMap.put("icon", String.valueOf(R.drawable.exam2));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 4");
        hashMap.put("icon", String.valueOf(R.drawable.exam3));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 5");
        hashMap.put("icon", String.valueOf(R.drawable.exam4));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 6");
        hashMap.put("icon", String.valueOf(R.drawable.exam5));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 7");
        hashMap.put("icon", String.valueOf(R.drawable.exam6));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 8");
        hashMap.put("icon", String.valueOf(R.drawable.exam7));
        arrayList.add(hashMap);


        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 9");
        hashMap.put("icon", String.valueOf(R.drawable.exam9));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("Title", "Today's Quiz 10");
        hashMap.put("icon", String.valueOf(R.drawable.exam10));
        arrayList.add(hashMap);


    }

}
