package com.fastaccess.ui.modules.gists;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.gists.create.CreateGistActivity;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 25 Mar 2017, 11:28 PM
 */

public class GistsListActivity extends BaseActivity {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.gistsContainer) ViewPagerView pager;

    public static void startActivity(@NonNull Context context, boolean myGists) {
        Intent intent = new Intent(context, GistsListActivity.class);
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, myGists).end());
        context.startActivity(intent);
    }

    @State boolean myGists;

    @BindView(R.id.fab) FloatingActionButton fab;

    @Override protected int layout() {
        return R.layout.gists_activity_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.gists);
        setTaskName(getString(R.string.gists));
        setupTabs();
        fab.show();
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @OnClick(R.id.fab) public void onViewClicked() {
        ActivityHelper.startReveal(this, new Intent(this, CreateGistActivity.class), fab);
    }

    private void setupTabs() {
        pager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForGists(this)));
        tabs.setupWithViewPager(pager);
    }
}
