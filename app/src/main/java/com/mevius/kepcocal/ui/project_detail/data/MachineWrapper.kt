package com.mevius.kepcocal.ui.project_detail.data

import android.os.Parcel
import android.os.Parcelable

class MachineWrapper: Parcelable {
    var indexInExcel: String? = null
    var cnum: String? = null
    var name: String? = null
    var coordinateLat: String? = null
    var coordinateLng: String? = null

    constructor() {
    }

    constructor(parcel: Parcel) {
        indexInExcel = parcel.readString()
        cnum = parcel.readString()
        name = parcel.readString()
        coordinateLat = parcel.readString()
        coordinateLng = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(indexInExcel)
        dest.writeString(cnum)
        dest.writeString(name)
        dest.writeString(coordinateLat)
        dest.writeString(coordinateLng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MachineWrapper> {
        override fun createFromParcel(parcel: Parcel): MachineWrapper {
            return MachineWrapper(parcel)
        }

        override fun newArray(size: Int): Array<MachineWrapper?> {
            return arrayOfNulls(size)
        }
    }

}