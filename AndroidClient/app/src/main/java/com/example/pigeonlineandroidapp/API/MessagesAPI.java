package com.example.pigeonlineandroidapp.API;

import android.content.Context;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.repos.MessagesRepository;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// Api for messages requests.
public class MessagesAPI {
    private ServiceAPI serviceAPI;
    private Retrofit retrofit;
    private String token;

    public MessagesAPI(Context context, String token, String defaultServer) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        this.retrofit = new Retrofit.Builder().baseUrl(defaultServer).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        this.serviceAPI = retrofit.create(ServiceAPI.class);
        this.token = token;
    }

    // Request for get all messages with contact (from current user).
    public void get(String contactUsername, MessagesRepository messagesRepository) {
        Call<List<Message>> getMessagesCall = this.serviceAPI.getMessages(contactUsername, this.token);
        getMessagesCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                messagesRepository.handleGetMessages(response.code(), response.body());
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                messagesRepository.handleGetMessages(0, null);
            }
        });
    }

    // Request for add new message (to the current chat).
    public void postMessage(String contactUsername, String content, MessagesRepository messagesRepository, Message message) {
        MessageContent messageContent = new MessageContent();
        messageContent.setContent(content);
        Call<Void> postMessageCall = this.serviceAPI.postMessage(contactUsername, messageContent, this.token);
        postMessageCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                messagesRepository.handlePostMessage(response.code(), message);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                messagesRepository.handlePostMessage(0, message);
            }
        });
    }

    // Transfer request (to the contact).
    public void transfer(String from, String to, String content, String server, MessagesRepository messagesRepository, Message message) {
        if(server.equals("http://127.0.0.1:5010") || server.equals("http://localhost:5010")) {
            server = "http://10.0.2.2:5010";
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        // Build the retrofit with the contact server.
        Retrofit tempRetrofit = new Retrofit.Builder().baseUrl(server).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        ServiceAPI tempServiceAPI = tempRetrofit.create(ServiceAPI.class);
        TransferParams transferParams = new TransferParams();
        transferParams.setFrom(from);
        transferParams.setTo(to);
        transferParams.setContent(content);
        Call<Void> transferCall = tempServiceAPI.transfer(transferParams);
        transferCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                messagesRepository.afterTransfer(response.code(), to, content, message);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                messagesRepository.afterTransfer(0, to, content, message);
            }
        });
    }
}
