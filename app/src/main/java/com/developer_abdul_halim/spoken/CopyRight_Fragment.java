package com.developer_abdul_halim.spoken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class CopyRight_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myView = inflater.inflate(R.layout.fragment_copy_right_, container, false);

        ImageButton facebookButton = myView.findViewById(R.id.facebook);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebook(view);
            }
        });

        ImageButton whatsappButton = myView.findViewById(R.id.whatsApp);
        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsApp(view);
            }
        });

        ImageButton telegramButton = myView.findViewById(R.id.telegram);
        telegramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTelegram(view);
            }
        });

        ImageButton emailButton = myView.findViewById(R.id.email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmail(view);
            }
        });




        return myView;



    }
    public void openFacebook(View view) {
        String facebookUrl = "https://www.facebook.com/profile.php?id=100089692752622";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
        startActivity(intent);
    }

    public void openEmail(View view) {
        String email = "abdulhalimbaccu447@gmail.com";
        String subject = "Hello from my app!";
        String message = "I'm interested in contacting you from your app Course BD. Can we discuss further?";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Choose an email app"));
    }

    public void openWhatsApp(View view) {
        String whatsappNumber = "+8801814672789";
        String message = "Hello! I'm interested in contacting you from your app Course BD.";
        String url = "https://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + message;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void openTelegram(View view) {
        String telegramUsername = "baccu447";
        String url = "https://t.me/" + telegramUsername;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}