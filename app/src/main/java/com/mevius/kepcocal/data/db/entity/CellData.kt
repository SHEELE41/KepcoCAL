package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["project_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CellData (
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "project_id") var projectId: Long?,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "cell") var cell: String,
    @ColumnInfo(name = "machine_id") var machineId: Int
)