package com.mevius.kepcocal

import android.util.Log
import kotlin.math.pow

/*
 * [한국전력 전산화번호 체계]
 * 예시 : 7643W867
 * [0..1] (76) : 2km 크기의 X축 블록 좌표
 * [2..3] (43) : 2km 크기의 Y축 블록 좌표
 * [4] (W) : 그 내부의 500m 크기의 알파벳 블록 좌표
 *   1   2   3   4
 * | A | B | E | F | 4
 * | C | D | G | H | 3
 * | P | Q | W | X | 2
 * | R | S | Y | Z | 1
 * [5] (8) : 알파벳 블록 내부의 50m 단위의 X축 블록 좌표
 * [6] (6) : 알파벳 블록 내부의 50m 단위의 X축 블록 좌표
 * 마지막 글자는 기기고유번호. 별 의미 없음
 */

class ComputerizedNumberCalculator () {
    var baseNumber = ""
    var targetNumber = ""

    private val re = Regex("[^0-9]")    // 혹시 모를 문자를 제거하기 위한 패턴(숫자 빼고 다 날림)

    fun getXDistance () : Int { // 양수면 동쪽 음수면 서쪽 기준으로 출력
        val diffOfFirstBlock = targetNumber.substring(0..1).toInt() - baseNumber.substring(0..1).toInt()
        val diffOfSecondBlock = alphaToXIndex(targetNumber[4]) - alphaToXIndex(baseNumber[4])
        val diffOfThirdBlock = re.replace(targetNumber, "")[4].toInt() - re.replace(baseNumber, "")[4].toInt()
        return ((diffOfFirstBlock * 2000) + (diffOfSecondBlock * 500) + (diffOfThirdBlock * 50))
    }

    fun getYDistance () : Int { // 양수면 북쪽 음수면 남쪽 기준으로 출력
        val diffOfFirstBlock = targetNumber.substring(2..3).toInt() - baseNumber.substring(2..3).toInt()
        val diffOfSecondBlock = alphaToYIndex(targetNumber[4]) - alphaToYIndex(baseNumber[4])
        val diffOfThirdBlock = re.replace(targetNumber, "")[5].toInt() - re.replace(baseNumber, "")[5].toInt()
        return ((diffOfFirstBlock * 2000) + (diffOfSecondBlock * 500) + (diffOfThirdBlock * 50))
    }

    fun getTotalDistance() : Long {
        return getXDistance().toDouble().pow(2).toLong() + getYDistance().toDouble().pow(2).toLong()
    }

    private fun alphaToXIndex (alpha : Char) : Int {
        return when (alpha){
            'A', 'C', 'P', 'R' -> 1
            'B', 'D', 'Q', 'S' -> 2
            'E', 'G', 'W', 'Y' -> 3
            'F', 'H', 'X', 'Z' -> 4
            else -> 0
            // 만약 이외의 문자열 들어오면?
        }
    }

    // 나중에 Refactoring
    private fun alphaToYIndex (alpha : Char) : Int {
        return when (alpha){
            'A', 'B', 'E', 'F' -> 4
            'C', 'D', 'G', 'H' -> 3
            'P', 'Q', 'W', 'X' -> 2
            'R', 'S', 'Y', 'Z' -> 1
            else -> 0
            // 만약 이외의 문자열 들어오면?
        }
    }
}