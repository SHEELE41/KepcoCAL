package com.mevius.kepcocal.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["project_id"],
        onDelete = CASCADE
    )]
)
data class Machine(
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 기기 인덱스(not Null)
    @ColumnInfo(name = "project_id", index = true) var projectId: Long?,    // 외래키(not Null)

    // 엑셀에서 불러올 때 CREATE_NULL_AS_BLANK을 이용하였기 때문에 Null이 될 일은 없음
    @ColumnInfo(name = "machine_id_in_excel") var machineIdInExcel: String,    // 실제 파일에 적혀있는 기기 연번(MachineData의 index)
    @ColumnInfo(name = "branch") var branch: String,
    @ColumnInfo(name = "computerized_number") var computerizedNumber: String,
    @ColumnInfo(name = "line_name") var lineName: String,
    @ColumnInfo(name = "line_number") var lineNumber: String,
    @ColumnInfo(name = "company") var company: String,
    @ColumnInfo(name = "manufacturing_year") var manufacturingYear: String,
    @ColumnInfo(name = "manufacturing_date") var manufacturingDate: String,
    @ColumnInfo(name = "manufacturing_number") var manufacturingNumber: String,
    @ColumnInfo(name = "first_address") var address1: String,
    @ColumnInfo(name = "second_address") var address2: String,
    @ColumnInfo(name = "coordinate_lng") var coordinateLng: String,
    @ColumnInfo(name = "coordinate_lat") var coordinateLat: String,
    @ColumnInfo(name = "is_done") var isDone: Boolean,
    @ColumnInfo(name = "is_no_coord") var isNoCoord: Boolean
): Parcelable {
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
