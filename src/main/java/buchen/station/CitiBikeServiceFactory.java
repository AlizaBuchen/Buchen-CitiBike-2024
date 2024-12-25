package buchen.station;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CitiBikeServiceFactory {

    public CitiBikeService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gbfs.citibikenyc.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return retrofit.create(CitiBikeService.class);
    }
}