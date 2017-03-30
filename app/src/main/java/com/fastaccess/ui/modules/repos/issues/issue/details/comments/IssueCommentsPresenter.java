package com.fastaccess.ui.modules.repos.issues.issue.details.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.comments.CommentsHandler;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class IssueCommentsPresenter extends BasePresenter<IssueCommentsMvp.View> implements IssueCommentsMvp.Presenter {
    private ArrayList<Comment> comments = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private String repoId;
    private String login;
    private int number;

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

        super.onError(throwable);
    }

    @Override public void onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(IssueCommentsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getIssueService().getIssueComments(login, repoId, number, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        comments.clear();
                        manageSubscription(Comment.saveForIssues(listResponse.getItems(), repoId(), login(),
                                String.valueOf(number)).subscribe());
                    }
                    comments.addAll(listResponse.getItems());
                    sendToView(IssueCommentsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null) throw new NullPointerException("Bundle is null?");
        repoId = bundle.getString(BundleConstant.ID);
        login = bundle.getString(BundleConstant.EXTRA);
        number = bundle.getInt(BundleConstant.EXTRA_TWO);
    }

    @NonNull @Override public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override public void onWorkOffline() {
        if (comments.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Comment.getIssueComments(repoId(), login(), String.valueOf(number)))
                    .subscribe(models -> {
                        if (models != null) {
                            comments.addAll(models);
                            sendToView(IssueCommentsMvp.View::onNotifyAdapter);
                        }
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            if (commId != 0) {
                makeRestCall(RestProvider.getIssueService().deleteIssueComment(login, repoId, commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                getComments().remove(comment);
                                view.onNotifyAdapter();
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            }
        }
    }

    @NonNull @Override public String repoId() {
        return repoId;
    }

    @NonNull @Override public String login() {
        return login;
    }

    @Override public int number() {
        return number;
    }

    @Override public void onItemClick(int position, View v, Comment item) {
        Login user = Login.getUser();
        if (getView() != null) {
            if (v.getId() == R.id.delete) {
                if (user != null && item.getUser().getLogin().equals(user.getLogin())) {
                    if (getView() != null) getView().onShowDeleteMsg(item.getId());
                }
            } else if (v.getId() == R.id.reply) {
                getView().onTagUser(item.getUser());
            } else if (v.getId() == R.id.edit) {
                if (user != null && item.getUser().getLogin().equals(user.getLogin())) {
                    getView().onEditComment(item);
                }
            } else {
                CommentsHandler.handleReactions(v.getContext(), login, repoId, v.getId(), item.getId(), false);
            }
        }
    }


    @Override public void onItemLongClick(int position, View v, Comment item) {
        onItemClick(position, v, item);
    }
}
