package com.happyselling.niznet.Services;

import android.content.Context;

public class ApiServices {
    public static final String BASE_URL = "https://service.happyselling.id/public/api/";

    public static BaseApiService getApiService(Context context){
        return ApiClient.getClient(BASE_URL, context).create(BaseApiService.class);
    }

    public static BaseApiService getApiServiceWithToken(String token, Context context){
        return ApiClient.getClientWithToken(BASE_URL, token, context).create(BaseApiService.class);
    }
}
