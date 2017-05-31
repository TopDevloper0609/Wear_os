package com.fastaccess.ui.modules.trending

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.fastaccess.R
import com.fastaccess.helper.Logger
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.trending.fragment.TrendingFragment
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by Kosh on 30 May 2017, 10:57 PM
 */

class TrendingActivity : BaseActivity<TrendingMvp.View, TrendingPresenter>(), TrendingMvp.View {

    private var trendingFragment: TrendingFragment? = null
    val languageList by lazy { findViewById(R.id.languageList) as DynamicRecyclerView }
    val daily by lazy { findViewById(R.id.daily) as FontTextView }
    val weekly by lazy { findViewById(R.id.weekly) as FontTextView }
    val monthly by lazy { findViewById(R.id.monthly) as FontTextView }
    val drawerLayout by lazy { findViewById(R.id.drawer) as DrawerLayout }


    fun onDailyClicked() {
        Logger.e()
        daily.isSelected = true
        weekly.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    fun onWeeklyClicked() {
        weekly.isSelected = true
        daily.isSelected = false
        monthly.isSelected = false
        setValues()
    }

    fun onMonthlyClicked() {
        monthly.isSelected = true
        weekly.isSelected = false
        daily.isSelected = false
        setValues()
    }

    private fun setValues() {
        trendingFragment?.onSetQuery("java", getSince())
    }

    override fun layout(): Int {
        return R.layout.trending_activity_layout
    }

    override fun isTransparent(): Boolean {
        return true
    }

    override fun canBack(): Boolean {
        return true
    }

    override fun isSecured(): Boolean {
        return false
    }

    override fun providePresenter(): TrendingPresenter {
        return TrendingPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        trendingFragment = supportFragmentManager.findFragmentById(R.id.trendingFragment) as TrendingFragment?
        daily.setOnClickListener { onDailyClicked() }
        weekly.setOnClickListener { onWeeklyClicked() }
        monthly.setOnClickListener { onMonthlyClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trending_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu) {
            drawerLayout.openDrawer(Gravity.END)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getSince(): String {
        when {
            daily.isSelected -> return "daily"
            weekly.isSelected -> return "weekly"
            monthly.isSelected -> return "monthly"
            else -> return "daily"
        }
    }
}
