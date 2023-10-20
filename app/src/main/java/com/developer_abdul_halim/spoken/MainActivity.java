package com.developer_abdul_halim.spoken;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;


public class MainActivity extends AppCompatActivity  {

    // In App update Variable
    private static final int MY_UPDATE_CODE = 101;
    private AppUpdateManager appUpdateManager;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------declare---------------
        bottomNavigationView();
        checkForAppUpdates();


        //----------------------------------


    } //=================================== onCreate ened here


    // Bottom NavigationView start here---------------------------------


    private void bottomNavigationView() {


        bottomNavigationView = findViewById(R.id.bottomNavView);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, new FragmentQuiz());
        fragmentTransaction.commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.quiz) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new FragmentQuiz())
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.home) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new HomeFragment())
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.notificatio) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new LatestFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new Book_Fragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                }

                return false;
            }
        });


    }    // Bottom NavigationView end here---------------------------------



    private void checkForAppUpdates(){
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                // Start Update
                try {
                    appUpdateManager.startUpdateFlowForResult((AppUpdateInfo) appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, MY_UPDATE_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Before starting an update, register a listener for updates.  (FOR FLEXIBLE)
        appUpdateManager.registerListener(listener);



    } // checkForAppUpdates End Here =============


    @Override
    protected void onStop() {
        super.onStop();
        // When status updates are no longer needed, unregister the listener. (FOR FLEXIBLE)
        appUpdateManager.unregisterListener(listener);
    }

    // Checks that the update is not stalled during 'onResume()'.
    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_UPDATE_CODE){
            if (resultCode != RESULT_OK ){
                Log.d("ActivityResult", "onActivityResult: Update Failed: "+resultCode);
            }
        }
    }

    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make( findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(android.R.color.darker_gray));
        snackbar.show();
    }






    // alert dialoge here if user wnat to go out -----------------------------------
    private static final long DOUBLE_BACK_PRESS_TIME = 2000; // time in milliseconds
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < DOUBLE_BACK_PRESS_TIME) {
            // User has double-tapped the back button
            showExitDialog();
        } else {
            lastBackPressTime = currentTime;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Do you want to really exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close the app
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}