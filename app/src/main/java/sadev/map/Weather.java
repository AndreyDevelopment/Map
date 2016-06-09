package sadev.map;

import android.support.annotation.Nullable;

/**
 * Created by Andrey Shloma on 08.06.2016.
 */
public class Weather {

    public Query query;

    public class Query {
        public Results results;
    }

    public class Results {
        public Channel channel;
    }

    public class Channel {
        public Item item;
    }

    public class Item {
        Forecast[] forecast;
    }

    public class Forecast {
        String code;
        String date;
        String day;
        String high;
        String low;
        String text;
    }

    @Nullable
    public Forecast[] getForecasts() {
        if (query == null) {
            return null;
        }
        if (query.results == null) {
            return null;
        }
        if (query.results.channel == null) {
            return null;
        }
        if (query.results.channel.item == null) {
            return null;
        }
        return query.results.channel.item.forecast;
    }
}
