package com.mevius.kepcocal.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "project_name") var projectName: String,
    @ColumnInfo(name = "modified_date") var modifiedDate: String,
    @ColumnInfo(name = "uri") var uri: String
): Parcelable {
    @ColumnInfo(name = "report_id") var reportId: Long? = null
}