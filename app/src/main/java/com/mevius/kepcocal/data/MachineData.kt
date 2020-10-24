package com.mevius.kepcocal.data

data class MachineData (var computerizedNumber : String){   // 전산화번호 3
    var index = ""    // 연번 1
    var branch = ""  // 2차 사업소 2
    var lineName = ""  // 선로명 4
    var lineNumber = ""    // 선로번호 5
    var company = ""  // 제작회사 6
    var manufacturingYear = ""  // 제조년 7
    var manufacturingDate = ""   // 제조월 8
    var manufacturingNumber = ""  // 제조번호 9
    var address1 = "" // 주소1 10
    var address2 = "" // 주소2 11

    // api 요청 후에도 coordinateLng, coordinateLat가 없다는 것은 주소가 둘 다 없거나 잘못된 주소일 경우.
    // 따라서 두 가지 경우 모두 고려해 주어야 함.
    var coordinateLng = ""  // Longitude : 경도 : X축 (127...)
    var coordinateLat = ""  // Latitude : 위도 : Y축 (37...)
}