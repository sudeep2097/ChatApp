package com.example.whtas;

import com.example.whtas.Notificaation.Sender;
import com.example.whtas.Notificaation.myResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {



    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA81cAfiM:APA91bFVHLw3rwLBZmsmYrapJgj4pzwKvV_Ogkyw0CHbhCxvVqjcQLV59s-6HJNRnmup4HH57dh7_MciWW58_FA-cUeo4jMpUI1ftPnv4ZctY6ubym9ZfpzKUOQcWoAQVjdSlohYIyM6"
            }
    )

    @POST("fcm/send")
    Call<myResponse> sendNotification(@Body Sender body);
}