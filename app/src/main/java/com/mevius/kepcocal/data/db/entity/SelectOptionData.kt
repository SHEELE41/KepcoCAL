package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = CellForm::class,
        parentColumns = ["id"],
        childColumns = ["cell_form_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SelectOptionData (
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "cell_form_id", index = true) var cellFormId: Long?,
    @ColumnInfo(name = "report_id") var reportId: Long?,
    @ColumnInfo(name = "is_auto") var isAuto: Boolean,
    @ColumnInfo(name = "content") var content: String
)