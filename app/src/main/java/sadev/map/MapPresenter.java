package sadev.map;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Andrey Shloma on 08.06.2016.
 */
public class MapPresenter implements MapContract.UserActionListener {

    private final MapContract.View mView;
    private GetWeather mGetWeather;

    public MapPresenter(@NonNull MapContract.View view) {
        mView = view;
    }

    public void getWeather(@NonNull String address) {
        if (mGetWeather != null) {
            mGetWeather.cancel(true);
        }
        mGetWeather = new GetWeather();
        mGetWeather.execute(address);
    }

    @Override
    public void onStop() {
        if (mGetWeather != null) {
            mGetWeather.cancel(true);
        }
    }

    private class GetWeather extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            String address = params[0];
            try {
                return Requester.getWater(address);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Weather weather) {
            mView.updateWeather(weather);
        }
    }
}
