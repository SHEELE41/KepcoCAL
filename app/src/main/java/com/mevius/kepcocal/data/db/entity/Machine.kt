package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["project_id"],
        onDelete = CASCADE
    )]
)
data class Machine(
    @PrimaryKey(autoGenerate = true) val id: Long?,   // 데이터베이스에서 사용할 자동 생성 기기 인덱스(not Null)
    @ColumnInfo(name = "project_id") val projectId: Long?,    // 외래키(not Null)

    @ColumnInfo(name = "machine_id_in_excel") val machineIdInExcel: String?,    // 실제 파일에 적혀있는 기기 연번(MachineData의 index)
    @ColumnInfo(name = "branch") val branch: String?,
    @ColumnInfo(name = "computerized_number") val computerizedNumber: String?,
    @ColumnInfo(name = "line_name") val lineName: String?,
    @ColumnInfo(name = "line_number") val lineNumber: String?,
    @ColumnInfo(name = "company") val company: String?,
    @ColumnInfo(name = "manufacturing_year") val manufacturingYear: String?,
    @ColumnInfo(name = "manufacturing_date") val manufacturingDate: String?,
    @ColumnInfo(name = "manufacturing_number") val manufacturingNumber: String?,
    @ColumnInfo(name = "first_address") val address1: String?,
    @ColumnInfo(name = "second_address") val address2: String?,
    @ColumnInfo(name = "coordinate_lng") val coordinateLng: String?,
    @ColumnInfo(name = "coordinate_lat") val coordinateLat: String?,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
    @ColumnInfo(name = "is_no_coord") val isNoCoord: Boolean
) {
    constructor() : this(null,
        null,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        false
    )
}
