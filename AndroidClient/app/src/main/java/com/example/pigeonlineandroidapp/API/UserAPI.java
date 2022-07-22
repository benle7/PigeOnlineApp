package com.example.pigeonlineandroidapp.API;

import android.content.Context;
import com.example.pigeonlineandroidapp.Activities.MainActivity;
import com.example.pigeonlineandroidapp.Activities.RegisterActivity;
import com.example.pigeonlineandroidapp.entities.User;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// Api for user requests.
public class UserAPI {
    private Retrofit retrofit;
    private ServiceAPI serviceAPI;

    public UserAPI(Context context, String defaultServer) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        this.retrofit = new Retrofit.Builder().baseUrl(defaultServer).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        this.serviceAPI = retrofit.create(ServiceAPI.class);
    }

    // postUser - register request.
    public void postUser(User user, RegisterActivity registerActivity) {
        Call<String> postUserCall = serviceAPI.postUser(user);
        postUserCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                registerActivity.handleRegisterResponse(response.code(), response.body(), user.getUsername());
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                registerActivity.handleRegisterResponse(0, null, null);
            }
        });
    }

    // Login request.
    public void Login(String username, String password, MainActivity mainActivity) {
        UserValidation userValidation = new UserValidation();
        userValidation.setUsername(username);
        userValidation.setPassword(password);
        Call<User> loginCall = serviceAPI.Login(userValidation);
        loginCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mainActivity.handleLoginResponse(response.body(), response.code(), username);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mainActivity.handleLoginResponse(null, 0, null);
            }
        });
    }

    // Logout request.
    public void declareOffline(String username) {
        Call<Void> offlineCall = this.serviceAPI.offline(username);
        offlineCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

}
