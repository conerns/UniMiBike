package com.unimib.unimibike.ProjectFiles.Repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.Util.ServerResponseParserCallback;
import com.unimib.unimibike.Util.UnimibBikeFetcher;

public class UsersRepository {
    private static UsersRepository instance;

    public UsersRepository(){}

    public static synchronized UsersRepository getInstance() {
        if (instance == null) {
            instance = new UsersRepository();
        }
        return instance;
    }

    public void userLogin(final Context context, final String user_email, final String user_password, final MutableLiveData<Resource<User>> user){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    logTheUser(context, user_email, user_password, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void getUserId(final Context context, final String user_email, final MutableLiveData<User> user) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    getUserIdFromDatabase(context, user_email, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void addUser(final Context context, final String user_email, final MutableLiveData<User> user){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    addUserToDatabase(context, user_email, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void logTheUser(final Context context, final String user_email,
                            final String user_password, final MutableLiveData<Resource<User>> user){
        UnimibBikeFetcher.postUserLogin(context, user_email, user_password, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                Resource<User> resource= new Resource<>();
                resource.setStatusCode(200);
                resource.setData(response);
                user.postValue(resource);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
                Resource<User> resource= new Resource<>();
                resource.setStatusCode(404);
                resource.setData(null);
                user.postValue(resource);
            }
        });
    }

    private void getUserIdFromDatabase(final Context context, final String user_email, final MutableLiveData<User> user){
        UnimibBikeFetcher.getUserId(context, user_email, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                user.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
            }
        });
    }

    private void addUserToDatabase(final Context context, final String user_email, final MutableLiveData<User> user){
        UnimibBikeFetcher.postAddUser(context, user_email, new ServerResponseParserCallback<User>() {
            @Override
            public void onSuccess(User response) {
                user.postValue(response);
            }

            @Override
            public void onError(String errorTitle, String errorMessage) {
            }
        });
    }
}
