package com.fastaccess.ui.modules.profile.following;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 03 Dec 2016, 3:48 PM
 */

class ProfileFollowingPresenter extends BasePresenter<ProfileFollowingMvp.View> implements ProfileFollowingMvp.Presenter {

    private ArrayList<User> users = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        sendToView(view -> {//wait view
            if (view.getLoadMore().getParameter() != null) {
                onWorkOffline(view.getLoadMore().getParameter());
            }
        });
        super.onError(throwable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (parameter == null) {
            throw new NullPointerException("Username is null");
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        setCurrentPage(page);
        if (page > lastPage || lastPage == 0) {
            sendToView(ProfileFollowingMvp.View::hideProgress);
            return;
        }
        makeRestCall(RestProvider.getUserService().getFollowing(parameter, page),
                response -> {
                    lastPage = response.getLast();
                    if (getCurrentPage() == 1) {
                        users.clear();
                        manageSubscription(User.saveUserFollowingList(response.getItems(), parameter).subscribe());
                    }
                    users.addAll(response.getItems());
                    sendToView(ProfileFollowingMvp.View::onNotifyAdapter);
                });
    }

    @NonNull @Override public ArrayList<User> getFollowing() {
        return users;
    }

    @Override public void onWorkOffline(@NonNull String login) {
        if (users.isEmpty()) {
            manageSubscription(RxHelper.getObserver(User.getUserFollowingList(login)).subscribe(userModels -> {
                users.addAll(userModels);
                sendToView(ProfileFollowingMvp.View::onNotifyAdapter);
            }));
        } else {
            sendToView(ProfileFollowingMvp.View::hideProgress);
        }
    }

    @Override public void onItemClick(int position, View v, User item) {}

    @Override public void onItemLongClick(int position, View v, User item) {}
}
