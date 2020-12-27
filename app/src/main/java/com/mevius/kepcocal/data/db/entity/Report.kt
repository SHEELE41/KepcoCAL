package com.mevius.kepcocal.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey(autoGenerate = true) var id: Long?,   // 데이터베이스에서 사용할 자동 생성 프로젝트 인덱스
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "interval") var interval: Int,
    @ColumnInfo(name = "is_xls") var isXls: Boolean
    // uri는 어차피 앱 기본 저장소 주소를 이용할 것이므로 상관없음.
    // 보고서 타이틀 텍스트가 곧 파일명이 됨
    // xlsx 인지 xls 인지는 따로 저장해야하는 듯
)