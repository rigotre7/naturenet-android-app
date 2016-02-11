package naturenet.org.naturenet.data.legacy;

import naturenet.org.naturenet.data.INatureNetServer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class NatureNetLegacyAPI implements INatureNetServer {

    private NatureNetAPI impl = get();

    @Override
    public boolean login(String username, String password) {
        impl.login(username, password);
        return false;
    }

    private static NatureNetAPI get() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //TODO: add interceptor to inject credentials?
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://naturenet.herokuapp.com/api")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(NatureNetAPI.class);
    }
}
