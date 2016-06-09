package sadev.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        MapContract.View {

    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 2;

    private GoogleMap mMap;
    private MapView mMapView;
    private TextView mTextView;
    private View mBackground;

    private MapContract.UserActionListener mMapPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapPresenter = new MapPresenter(this);

        mBackground = findViewById(android.R.id.background);
        mTextView = (TextView) findViewById(android.R.id.text1);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        mMapPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                showMessage(R.string.error_permission_denied);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null) {
            showMessage(R.string.error_address_not_found);
            return;
        }

        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(city));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMapPresenter.getWeather(country + ", " + city);
    }

    @Override
    public void updateWeather(@Nullable Weather weather) {
        if (weather == null) {
            showMessage(R.string.error_address_not_found);
            return;
        }
        Weather.Forecast[] forecasts = weather.getForecasts();
        if (forecasts == null || forecasts.length == 0) {
            showMessage(R.string.error_address_not_found);
            return;
        }
        Weather.Forecast forecast = forecasts[0];
        try {
            int low = Integer.parseInt(forecast.low);
            int color = Math.abs(low);
            if (color > 255) {
                color = 0;
            }
            mBackground.setBackgroundColor(Color.rgb(color, color, color));
        } catch (Exception e) {

        }
        showMessage(getString(R.string.forecast, forecast.date, forecast.low, forecast.high));
    }

    private void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    private void showMessage(@NonNull final String msg) {
        mTextView.setText(msg);
    }
}
