package sadev.map;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Andrey Shloma on 08.06.2016.
 */
public interface MapContract {

    interface View {
        void updateWeather(@Nullable Weather weather);
    }

    interface UserActionListener {
        void getWeather(@NonNull String address);

        void onStop();
    }
}
