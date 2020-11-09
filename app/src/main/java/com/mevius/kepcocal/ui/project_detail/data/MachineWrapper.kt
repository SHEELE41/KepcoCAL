package com.mevius.kepcocal.ui.project_detail.data

import android.os.Parcel
import android.os.Parcelable


class MachineWrapper: Parcelable {
    var cnum: String? = null
    var name: String? = null

    constructor() {
    }

    constructor(parcel: Parcel) {
        cnum = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cnum)
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    val CREATOR: Parcelable.Creator<MachineWrapper?> = object : Parcelable.Creator<MachineWrapper?> {
        override fun createFromParcel(parcel: Parcel): MachineWrapper? {
            return MachineWrapper(parcel)
        }

        override fun newArray(size: Int): Array<MachineWrapper?> {
            return arrayOfNulls(size)
        }
    }
}