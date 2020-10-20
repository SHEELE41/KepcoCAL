package com.mevius.kepcocal

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

/**
 * [ComputerizedNumberCalculator] Class
 * 두 기기의 전산화번호를 통해 X축 거리, Y축 거리, 총 거리를 계산하는 메소드를 가진 Class
 * 나중에 최종 좌표 구하는 과정까지 한번에?
 * 그러기 위해서는 기준 기기 좌표까지 필드로 받아야 함.
 * */
class ComputerizedNumberCalculator() {
    var baseNumber = ""     // 거리 계산의 기준이 되는 기기의 전산화번호
    var targetNumber = ""   // 좌표를 구하고 싶은 기기의 전산화번호
    private val re = Regex("[^0-9]")    // 혹시 모를 문자를 제거하기 위한 패턴(숫자 빼고 다 날림)

    // Base에서 Target까지 가야하는 X축 방향 거리 계산 메소드
    fun getXDistance(): Int { // 양수면 동쪽 음수면 서쪽 기준으로 출력
        val diffOfFirstBlock =
            targetNumber.substring(0..1).toInt() - baseNumber.substring(0..1).toInt()
        val diffOfSecondBlock = alphaToXIndex(targetNumber[4]) - alphaToXIndex(baseNumber[4])
        val diffOfThirdBlock =
            re.replace(targetNumber, "")[4].toInt() - re.replace(baseNumber, "")[4].toInt()
        return ((diffOfFirstBlock * 2000) + (diffOfSecondBlock * 500) + (diffOfThirdBlock * 50))
    }

    // Base에서 Target까지 가야하는 Y축 방향 거리 계산 메소드
    fun getYDistance(): Int { // 양수면 북쪽 음수면 남쪽 기준으로 출력
        val diffOfFirstBlock =
            targetNumber.substring(2..3).toInt() - baseNumber.substring(2..3).toInt()
        val diffOfSecondBlock = alphaToYIndex(targetNumber[4]) - alphaToYIndex(baseNumber[4])
        val diffOfThirdBlock =
            re.replace(targetNumber, "")[5].toInt() - re.replace(baseNumber, "")[5].toInt()
        return ((diffOfFirstBlock * 2000) + (diffOfSecondBlock * 500) + (diffOfThirdBlock * 50))
    }

    // Base와 Target 사이의 종합 거리 계산 메소드
    fun getTotalDistance(): Long {
        return getXDistance().toDouble().pow(2).toLong() + getYDistance().toDouble().pow(2).toLong()
    }

    // 전산화번호 특성상 각 축 방향의 거리를 계산하기 위해 알파벳 -> 숫자값 변환이 필요
    private fun alphaToXIndex(alpha: Char): Int {
        return when (alpha) {
            'A', 'C', 'P', 'R' -> 1
            'B', 'D', 'Q', 'S' -> 2
            'E', 'G', 'W', 'Y' -> 3
            'F', 'H', 'X', 'Z' -> 4
            else -> 0
            // 만약 이외의 문자열 들어오면?
        }
    }

    // 나중에 Refactoring
    private fun alphaToYIndex(alpha: Char): Int {
        return when (alpha) {
            'A', 'B', 'E', 'F' -> 4
            'C', 'D', 'G', 'H' -> 3
            'P', 'Q', 'W', 'X' -> 2
            'R', 'S', 'Y', 'Z' -> 1
            else -> 0
            // 만약 이외의 문자열 들어오면?
        }
    }
}