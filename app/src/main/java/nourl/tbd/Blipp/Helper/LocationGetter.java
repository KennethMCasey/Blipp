package nourl.tbd.Blipp.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;


import nourl.tbd.Blipp.BuildConfig;
import nourl.tbd.Blipp.R;

public class LocationGetter {
    //TODO: Implement the LocationGetter Class, this will make the process of getting the users current geo location (somthing that will need to be repeated a million times) way easier.

    private Context context;
   private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private LocationGetterCompletion completion;
    private double  lati, longi;

    public static LatLongBounds getBounds(double latitude, double longitude, double miles)
    {
        double latMin = (((latitude + 90) - (miles/69)) % 180) - 90;
        double latMax = (((latitude  + 90) + (miles/69)) % 180) - 90;
        double lonMax = (((longitude + 180) + (miles/69)) % 360) - 180;
        double lonMin = (((longitude + 180) - (miles/69)) % 360) - 180;

        return  new LatLongBounds(latMin, latMax, lonMin, lonMax);
    }





   static public class LatLongBounds
    {
        double latMin;
        double latMax;
        double lonMin;
        double lonMax;

        public LatLongBounds(double latMin, double latMax, double lonMin, double lonMax) {
            this.latMin = latMin;
            this.latMax = latMax;
            this.lonMin = lonMin;
            this.lonMax = lonMax;
        }
    }

    public LocationGetter(Context context, final LocationGetterCompletion completion) {
        this.context = context;
        this.completion = completion;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (!checkPermissions()) goToSettings(); // If Location permission has not been granted Bring user to settings
        // User permission already granted
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null) {//location pull successful
                            mLastLocation = task.getResult();
                            completion.locationGetterDidGetLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        }
                        else
                            {
                            completion.locationGetterDidFail(true);
                        }
                    }
                });
    }


    private void showSnackbar ( final int mainTextStringId, final int actionStringId,
                                View.OnClickListener listener){
        Snackbar.make(((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content),
                context.getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(context.getString(actionStringId), listener).show();
    }



    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions () {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void goToSettings() { // If location permission is not granted, bring user to settings
        completion.locationGetterDidFail(false);
        showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

    }


}