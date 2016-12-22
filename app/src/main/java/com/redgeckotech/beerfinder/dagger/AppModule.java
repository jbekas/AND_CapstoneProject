package com.redgeckotech.beerfinder.dagger;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.BuildConfig;
import com.redgeckotech.beerfinder.api.BreweryApi;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class AppModule {
    private final BreweryInfoApplication application;

    public AppModule(BreweryInfoApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides @Singleton
    OkHttpClient getHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                // Add any standard headers
                //builder.header(key, value);

                return chain.proceed(builder.build());
            }
        });
        builder.addInterceptor(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                return chain.proceed(request);
            }
        });

//        if (BuildConfig.DEBUG) {
//            // FIXME Does not work with grabbing raw data from Github
//            builder.addNetworkInterceptor(new LoggingInterceptor());
//        }

        return builder.build();
    }

    @Provides @Singleton
    Retrofit getRetrofit(OkHttpClient httpClient) {
        String baseUrl = BuildConfig.BREWERY_LIST_BASE_URL;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit;
    }

    @Provides @Singleton
    BreweryApi getBreweryApi(Retrofit retrofit) {

        try {
            return retrofit.create(BreweryApi.class);
        } catch (Exception e) {
            Timber.w(e, null);
        }

        return null;
    }

    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                Timber.i("inside intercept callback");
                Request request = chain.request();
                long t1 = System.nanoTime();
                String requestLog = String.format(Locale.US, "Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers());
                if (request.method().compareToIgnoreCase("post") == 0) {
                    requestLog = "\n" + requestLog + "\n" + bodyToString(request);
                }
                Timber.d("request" + "\n" + requestLog);
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();

                String responseCode = String.format(Locale.US, "Response code: %d", response.code());
                String responseLog = String.format(Locale.US, "Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers());

                String bodyString = response.body().string();

                //Timber.d("response" + "\n" + responseCode + "\n" + responseLog + "\n" + bodyString);
                Timber.d("response" + "\n" + responseCode + "\n" + responseLog);

                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .build();

            } catch (Exception e) {
                Timber.e(e, null);
            }

            return null;
        }

        public static String bodyToString(final Request request) {
            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                return "did not work";
            }
        }
    }

    @Provides
    Picasso getPicasso(Context context) {
        Picasso.Builder picassoBuilder = new Picasso.Builder(context);

        // Picasso.Builder creates the Picasso object to do the actual requests
        return picassoBuilder.build();
    }

    @Provides
    @Singleton
    FirebaseAnalytics getFirebaseAnalytics() {
        return FirebaseAnalytics.getInstance(application);
    }
}
