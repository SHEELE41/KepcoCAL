package com.mevius.kepcocal.ui.project_detail.data

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import java.util.*

class MachineSuggestion : SearchSuggestion {
    private var mMachineName: String? = null
    private var mIsHistory = false

    constructor(suggestion: String) {
        mMachineName = suggestion.toLowerCase(Locale.ROOT)
    }

    constructor(source: Parcel) {
        mMachineName = source.readString()
        mIsHistory = source.readInt() != 0
    }

    fun setIsHistory(isHistory: Boolean) {
        mIsHistory = isHistory
    }

    fun getIsHistory(): Boolean {
        return mIsHistory
    }

    override fun getBody(): String? {
        return mMachineName
    }

    val CREATOR: Parcelable.Creator<MachineSuggestion?> =
        object : Parcelable.Creator<MachineSuggestion?> {
            override fun createFromParcel(parcel: Parcel): MachineSuggestion? {
                return MachineSuggestion(parcel)
            }

            override fun newArray(size: Int): Array<MachineSuggestion?> {
                return arrayOfNulls<MachineSuggestion>(size)
            }
        }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mMachineName)
        dest.writeInt(if (mIsHistory) 1 else 0)
    }
}