package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project (
    @PrimaryKey(autoGenerate = true) val id: Int,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "project_name") val projectName: String?,
    @ColumnInfo(name = "modified_date") val modifiedDate: String?
)