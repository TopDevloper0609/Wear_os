package com.fastaccess.data.dao.wiki

import android.os.Parcel
import com.fastaccess.helper.KParcelable
import com.fastaccess.helper.parcelableCreator

/**
 * Created by Kosh on 13 Jun 2017, 8:03 PM
 */
data class WikiSideBarModel(val title: String? = null, val link: String? = null) : KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(::WikiSideBarModel)
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(link)
    }
}
