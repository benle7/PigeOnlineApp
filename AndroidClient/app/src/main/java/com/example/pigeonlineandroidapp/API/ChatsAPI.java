package com.example.pigeonlineandroidapp.API;

import android.content.Context;
import com.example.pigeonlineandroidapp.Activities.AddContactActivity;
import com.example.pigeonlineandroidapp.DataBase.ChatsDao;
import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.repos.ContactsRepository;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// API for chats (contacts) requests.
public class ChatsAPI {
    private ServiceAPI serviceAPI;
    private ChatsDao chatsDao;
    private Retrofit retrofit;
    private String token;
    private String defaultServer;

    public ChatsAPI(Context context, ChatsDao chatsDao, String token, String defaultServer) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        this.retrofit = new Retrofit.Builder().baseUrl(defaultServer).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        this.serviceAPI = retrofit.create(ServiceAPI.class);
        this.chatsDao = chatsDao;
        this.token = token;
        this.defaultServer = defaultServer;
    }

    // Request for get all chats of the current user.
    public void get(ContactsRepository repository) {
        Call<List<Chat>> call = this.serviceAPI.getChats(this.token);
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                repository.handleGetChats(response.code(), response.body());
            }
            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {

            }
        });
    }

    // Request for add new contact (chat).
    public void post(PostContactParams postContactParams, ContactsRepository contactsRepository, String username, AddContactActivity addContactActivity) {
        Call<String> postContactCall = serviceAPI.postChat(postContactParams, this.token);
        postContactCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                contactsRepository.postChatHandler(response.code(), postContactParams.getId(),
                        postContactParams.getName(), postContactParams.getServer(), username, addContactActivity, response.body());
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                contactsRepository.postChatHandler(0, postContactParams.getId(),
                        postContactParams.getName(), postContactParams.getServer(), username, addContactActivity, null);
            }
        });

    }

    // Invitation request (to the contact).
    public void sendInvitation(InvitationParams invitationParams, String server, String name ,ContactsRepository contactsRepository, AddContactActivity addContactActivity) {
        // Build the retrofit with the contact server.
        Retrofit tempRetro = new Retrofit.Builder().baseUrl(server).
                addConverterFactory(GsonConverterFactory.create()).build();
        ServiceAPI tempServiceAPI = tempRetro.create(ServiceAPI.class);
        Call<Void> invitationCall = tempServiceAPI.getInvitation(invitationParams);
        invitationCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                contactsRepository.afterInvitationHandler(response.code(), invitationParams.getTo(), name, server, addContactActivity);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                contactsRepository.afterInvitationHandler(0, invitationParams.getTo(), name, server, addContactActivity);
            }
        });
    }

    // Request for declare the current user is online (after login / register - in contacts activity).
    public void declareOnline(String appToken) {
        Call<Void> declareOnlineCall = this.serviceAPI.declareOnline(appToken, this.token);
        declareOnlineCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

}

