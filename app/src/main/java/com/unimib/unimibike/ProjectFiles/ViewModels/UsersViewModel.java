package com.unimib.unimibike.ProjectFiles.ViewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.unimibike.Model.Resource;
import com.unimib.unimibike.Model.User;
import com.unimib.unimibike.ProjectFiles.Repositories.UsersRepository;

import java.util.List;

public class UsersViewModel extends ViewModel {
    private MutableLiveData<List<User>> users;
    private MutableLiveData<Resource<User>> resourceUser;
    private MutableLiveData<User> user;

    public MutableLiveData<Resource<User>> login(final Context context, final String user_email, final String user_password) {
        if (resourceUser == null)
            resourceUser = new MutableLiveData<>();

        UsersRepository.getInstance().userLogin(context, user_email, user_password, resourceUser);
        return resourceUser;
    }

    public MutableLiveData<User> getUserId(final Context context, final String user_email) {
        if (user == null)
            user = new MutableLiveData<>();

        UsersRepository.getInstance().getUserId(context, user_email, user);
        return user;
    }

    public MutableLiveData<User> addUser(final Context context, final String user_email) {
        if (user == null)
            user = new MutableLiveData<>();

        UsersRepository.getInstance().addUser(context, user_email, user);
        return user;
    }
}
