package com.fastaccess.ui.modules.repos.code.files;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.adapter.RepoFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.code.CodeViewerView;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathView;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 18 Feb 2017, 2:10 AM
 */

public class RepoFilesView extends BaseFragment<RepoFilesMvp.View, RepoFilesPresenter> implements RepoFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private RepoFilesAdapter adapter;
    private RepoFilePathView parentFragment;

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void onItemClicked(@NonNull RepoFilesModel model) {
        if (refresh.isRefreshing()) return;
        if (model.getType() == FilesType.dir) {
            if (getParent() != null) {
                getParent().onAppendPath(model);
            }
        } else {
            if (model.getSize() > FileHelper.ONE_MB && !MarkDownProvider.isImage(model.getDownloadUrl())) {
                MessageDialogView.newInstance(getString(R.string.big_file), getString(R.string.big_file_description),
                        Bundler.start().put(BundleConstant.EXTRA, model.getDownloadUrl()).end())
                        .show(getChildFragmentManager(), "MessageDialogView");
            } else {
                CodeViewerView.startActivity(getContext(), model.getDownloadUrl());
            }
        }
    }

    @Override public void onMenuClicked(@NonNull RepoFilesModel item, View v) {
        if (refresh.isRefreshing()) return;
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_share_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.download).setVisible(item.getType() == FilesType.file);
        popup.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.share:
                    ActivityHelper.shareUrl(v.getContext(), item.getHtmlUrl());
                    break;
                case R.id.download:
                    RestProvider.downloadFile(getContext(), item.getDownloadUrl());
                    break;
            }
            return true;
        });
        popup.show();
    }

    @Override public void onSetData(@NonNull String login, @NonNull String repoId, @Nullable String path) {
        getPresenter().onInitDataAndRequest(login, repoId, path);
    }

    @Override public boolean isRefreshing() {
        return refresh.isRefreshing();
    }

    @Override protected int fragmentLayout() {
        return R.layout.vertical_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(v -> onRefresh());
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new RepoFilesAdapter(getPresenter().getFiles());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
        super.showErrorMessage(msgRes);
    }

    @NonNull @Override public RepoFilesPresenter providePresenter() {
        return new RepoFilesPresenter();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            String url = bundle.getString(BundleConstant.EXTRA);
            if (!InputHelper.isEmpty(url)) {
                RestProvider.downloadFile(getContext(), url);
            }
        }
    }

    private RepoFilePathView getParent() {
        if (parentFragment == null) {
            parentFragment = (RepoFilePathView) getParentFragment();
        }
        return parentFragment;
    }
}
