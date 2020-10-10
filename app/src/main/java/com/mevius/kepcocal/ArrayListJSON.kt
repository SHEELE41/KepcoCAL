package com.mevius.kepcocal

import org.json.JSONArray
import org.json.JSONObject

/**
* ArrayListJSON
* ArrayList 를 JSON 으로, 혹은 JSON 을 ArrayList 로 변환
* 서버 데이터 전송, 수신 전후에 사용됨.
*/
class ArrayListJSON {
    /**
    * ArrayListJSON - arrayListToJSON
    * 기기 데이터 리스트에서 인덱스, 주소 1, 2를 JSON 형식으로 저장하기 위한 메소드
    * 서버에 보내야 하니깐...
    * 주소 데이터가 없는 인덱스 처리는 서버쪽에서?
    * 근데 안 쓸듯?
    */
    fun arrayListToJSON(machineList : ArrayList<MachineData>) : String {
        val jArray = JSONArray()     // Create JSONArray Instance and Initialize    [{"",""},{"",""},{"",""}]
        for (machineData in machineList) {
            val jObject = JSONObject()  // Create JSONObject Instance and Initialize    {"",""}
            jObject.put("index", machineData.index)
            jObject.put("address1", machineData.address1)
            jObject.put("address2", machineData.address2)
            jArray.put(jObject)
        }
        return jArray.toString()
    }

    /**
    * ArrayListJSON - JSONToArrayList
    * Kakao REST API를 이용하여 받아온 주소 대응 JSON 데이터를 좌표가 포함된 ArrayList로 반환...?
    * 굳이 안해도 되나?
    */
    fun JSONToArrayList(jString : String) {
    }
}