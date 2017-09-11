package com.fastaccess.ui.modules.repos.projects.list.details

import android.content.Intent
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ProjectPagerPresenter : BasePresenter<ProjectPagerMvp.View>(), ProjectPagerMvp.Presenter {

    private val columns = arrayListOf<ProjectColumnModel>()
    @com.evernote.android.state.State var projectId: Long = -1
    @com.evernote.android.state.State var repoId: String = ""
    @com.evernote.android.state.State var login: String = ""

    override fun getColumns(): ArrayList<ProjectColumnModel> = columns


    override fun onRetrieveColumns() {
        makeRestCall(RestProvider.getProjectsService(isEnterprise).getProjectColumns(projectId)
                .flatMap {
                    if (it.items != null) {
                        return@flatMap Observable.just(it.items)
                    }
                    return@flatMap Observable.just(listOf<ProjectColumnModel>())
                }, { t ->
            columns.clear()
            columns.addAll(t)
            sendToView { it.onInitPager(columns) }
        })
    }

    override fun onActivityCreated(intent: Intent?) {
        intent?.let {
            it.extras?.let {
                projectId = it.getLong(BundleConstant.ID)
                repoId = it.getString(BundleConstant.ITEM)
                login = it.getString(BundleConstant.EXTRA)
            }
        }
        if (columns.isEmpty()) {
            if (projectId > 0)
                onRetrieveColumns()
            else
                sendToView { it.showMessage(R.string.error, R.string.unexpected_error) }
        } else {
            sendToView { it.onInitPager(columns) }
        }
    }
}