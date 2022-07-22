package com.example.pigeonlineandroidapp.API;

import com.example.pigeonlineandroidapp.entities.Chat;
import com.example.pigeonlineandroidapp.entities.Message;
import com.example.pigeonlineandroidapp.entities.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceAPI {
    @GET("/api/contacts")
    Call<List<Chat>> getChats(@Header("Authorization") String auth);

    @POST("/api/contacts")
    Call<String> postChat(@Body PostContactParams params, @Header("Authorization") String auth);

    @POST("/api/Users/Login")
    Call<User> Login(@Body UserValidation userValidation);

    @POST("/api/Users")
    Call<String> postUser(@Body User user);

    @POST("/api/Users/offline")
    Call<Void> offline(@Query("username") String username);

    @POST("/api/Users/declare")
    Call<Void> declareOnline(@Query("token") String appToken, @Header("Authorization") String auth);

    @GET("/api/contacts/{id}/messages")
    Call<List<Message>> getMessages(@Path("id") String id, @Header("Authorization") String auth);

    @POST("/api/contacts/{id}/messages")
    Call<Void> postMessage(@Path("id") String id, @Body MessageContent messageContent,
                           @Header("Authorization") String auth);

    @POST("/api/transfer")
    Call<Void> transfer(@Body TransferParams transferParams);

    @POST("/api/invitations")
    Call<Void> getInvitation(@Body InvitationParams invitationParams);

}
