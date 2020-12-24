package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "title") var title: String
)