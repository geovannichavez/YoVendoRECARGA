package com.globalpaysolutions.yovendorecarga.customs;

/**
 * Created by Josué Chávez on 13/08/2016.
 */
public class LocationTracker
        //implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    /*public static final String TAG = "locationYVS";

    Context _context;
    public Location mLastLocation;
    public GoogleApiClient mGoogleApiClient;

    public LocationTracker(Context context)
    {
        _context = context;
    }


    // Creating google api client object

    public synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(_context).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
    }


     // Method to verify google play services on the device
    public boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(_context);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                Log.i(TAG, "This device is not compatible with Google Play Services");
            }
            else
            {
                Log.i(TAG, "This device is not compatible with Google Play Services");
            }
            return false;
        }
        return true;
    }



    // Google api callback methods

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        // Once connected with google api, get the location
        getLocation();
    }


    @Override
    public void onConnectionSuspended(int arg0)
    {
        mGoogleApiClient.connect();
    }


    public LocationData getLocation()
    {
        LocationData locationData = new LocationData();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( _context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( _context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return locationData ;
        }
        
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)
        {
            locationData.setLatitude(mLastLocation.getLatitude());
            locationData.setLongitude(mLastLocation.getLongitude());
        }
        else
        {
           Log.i(TAG, "No se pudo obtener la ubicaci�n.");
        }

        return  locationData;

    }*/

    /*public void getLocation()
    {
        LocationData locationData = new LocationData();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( _context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( _context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)
        {
            locationData.setLatitude(mLastLocation.getLatitude());
            locationData.setLongitude(mLastLocation.getLongitude());
        }
        else
        {
            Log.i(TAG, "No se pudo obtener la ubicaci�n.");
        }

    }*/
}
