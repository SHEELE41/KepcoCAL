package com.mevius.kepcocal.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mevius.kepcocal.data.db.dao.MachineDao
import com.mevius.kepcocal.data.db.entity.Machine
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.data.network.GeocodeApiHelper
import com.mevius.kepcocal.data.network.model.ResultGetCoordinate
import com.mevius.kepcocal.utils.ComputerizedNumberCalculator
import com.mevius.kepcocal.utils.ExcelHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineRepositoryImpl @Inject constructor(
    private val localDataSource: MachineDao,
    private val remoteDataSource: GeocodeApiHelper,
    private val excelHelper: ExcelHelper
): MachineRepository {
    override val allMachines: LiveData<List<Machine>> = localDataSource.getAll()

    override suspend fun insert(machine: Machine) {
        localDataSource.insert(machine)
    }

    override suspend fun update(machine: Machine) {
        localDataSource.update(machine)
    }

    override suspend fun delete(machine: Machine) {
        localDataSource.delete(machine)
    }

    override fun getMachinesByProjectId(projectId: Long): LiveData<List<Machine>> {
        return localDataSource.getMachinesByProjectId(projectId)
    }

    override suspend fun insertMachinesFromExcel(scope: CoroutineScope, project: Project) {
        val machineList = excelHelper.readProjectAsList(Uri.parse(project.uri))
        insertProjectMachineData(scope, project.id!!, machineList)
    }

    private suspend fun insertProjectMachineData(
        scope: CoroutineScope,
        currentProjectId: Long,
        machineList: List<Machine>
    ) = scope.launch {
        // Log.d("##################################Launch2", this.coroutineContext[Job].toString())

        /*
        * [Coroutine]
        * 1. machineList 안의 machineData 하나를 가져옴
        * 2. 그 안의 address 1, 2 값을 검사하여 둘 중 하나라도 있으면 API 요청 대상 주소 machineAddr 값으로 설정
        * 3. 주소 둘 다 비어있으면 그 machineData 는 그냥 스킵. API 요청조차 하지 않음. (noCoordMachineArrayList 에 추가)
        * 4. 주소 있으면 해당 주소로 API 요청
        * 5. Response 에서 좌표 정보 빼와서 현재 machineData 객체의 x, y 필드에 set (어차피 해당 machineData 의 주소를 받아서 검색한거라 Index 신경쓸 필요는 없음)
        * 6. 좌표 조회 결과 유효하지 않으면 noCoordMachineArrayList 에 추가
        * 7. 부모 launch : machineList for 문을 감싸고 있으며, 이 for 문으로 여러개 생성된 각각의 자식 Coroutine 들이 모두 완료될 때 까지 대기.
        * 8. 자식 launch : for 문 안에서 machineList.size 개 만큼 실행되며, GeocoderAPI.getCoordinate()가 suspend 함수라서 Response 처리를 완료할 때까지 대기. (하나의 부모 Coroutine 이라고 할 수 있음)
        * 9. 자식 launch 의 범위는 꼭 마커 찍는 곳까지 묶어줘야 함.
        * 10. 모든 launch 다 끝나면(join) 좌표 누락된 noCoordMachineArrayList 의 기기들 좌표 계산해서 찍어줌.
        */

        launch {    // 부모 코루틴은 생성된 자식 코루틴들이 모두 완료될 때 까지 대기
            // Log.d("##################################Launch3", this.coroutineContext[Job].toString())
            for (machineData in machineList) {
                // 비어있지 않은 주소를 machineAddr에 전달
                val machineAddr: String = if (machineData.address1 != "") {
                    machineData.address1
                } else if (machineData.address2 != "") {
                    machineData.address2
                } else {
                    // 주소 둘 다 비어있으면 그냥 다음으로 넘어감
                    machineData.isNoCoord = true
                    continue
                }

                // Import 는 다 Retrofit2로 (Not OkHttp3!)
                launch {
                    // Log.d("##################################Launch4", this.coroutineContext[Job].toString())
                    val response = remoteDataSource.getCoordinate(machineAddr)
                    if (response.isSuccessful) {
                        /*
                        * >> machineData에 좌표 데이터 넣어주기 전에 고려해야 할 것
                        * 1. Response의 documents 리스트가 비어있지는 않은가?
                        * 2. response.body()가 null은 아닌가?    // response 자체는 null이 아니지만 body는 가능성 존재
                        * 3. documents 자체가 null이 될 수도 있나...? X
                        */
                        val resultInstance: ResultGetCoordinate? = response.body()
                        resultInstance?.let {
                            if (it.documents.isNotEmpty()) {
                                machineData.coordinateLng = it.documents[0].x.toString()
                                machineData.coordinateLat = it.documents[0].y.toString()
                            }
                        }
                    }

                    if (machineData.coordinateLng != "" && machineData.coordinateLat != "") {   // 좌표가 둘 다 비어있지 않다면
                        machineData.projectId = currentProjectId
                        localDataSource.insert(machineData)

                    } else {  // 좌표 하나라도 invalid 할 시 바로 좌표누락기기리스트에 넣어버림
                        machineData.isNoCoord = true
                    }
                }  // for문마다 생기는 launch의 마지막 : 여기까지 한 작업의 단위로 묶어 비동기로 던져줘야 함.
            }
        }.join()    // 부모 launch 종료에 맞춤
        // 마찬가지로 비동기실행
        launch { calculateInvalidAddrMachineData(currentProjectId, machineList) }
    }

    /**
     * [calculateInvalidAddrMachineData] function
     * 좌표 정보가 유효하지 않은 기기들을 지도상에 표시하는 메소드
     * 좌표 정보가 유효한 가까운 기기를 기준점으로 하여 전산화번호 연산 후 자신의 좌표 도출
     */
    private suspend fun calculateInvalidAddrMachineData(
        currentProjectId: Long,
        machineList: List<Machine>
    ) {
        // 전산화번호 계산기 객체 선언
        val cNumberCalculator = ComputerizedNumberCalculator()

        // 좌표 정보가 존재하지 않는 기기들을 순회하는 반복문
        machineList.forEach {
            if (it.isNoCoord) {
                var closestDistance: Long = Long.MAX_VALUE  // 이 부분도 좀 고쳤으면.
                var closestMachine: Machine? = null

                cNumberCalculator.targetNumber =
                    it.computerizedNumber  // 한 순회마다 noCoordMachine 에 대한 전산화번호로 갱신

                // 위도 경도 정보가 제대로 존재하면서 noCoordMachine과 가장 가까운 기기를 찾기 위한 반복문
                for (machine in machineList) {
                    cNumberCalculator.baseNumber =
                        machine.computerizedNumber   // 한 순회마다 machineList 안의 machine 에 대한 전산화번호로 갱신
                    if (machine.coordinateLat != "" && machine.coordinateLng != "") {    // 좌표 있는 기기 찾으면 둘 사이의 거리 계산(좌표 없는 기기 - 현재 기기)
                        // 정렬처럼 갈수록 더 짧은 거리로 갱신하면 되겠네
                        if (closestDistance > cNumberCalculator.getTotalDistance()) {
                            closestDistance = cNumberCalculator.getTotalDistance()
                            closestMachine = machine
                        }
                    }
                }

                // 위도 경도 정보가 존재하는 가장 가까운 기기가 존재한다면 그 기기를 기준으로 좌표 계산 후 지도에 추가
                closestMachine?.let { itClosestMachine ->
                    cNumberCalculator.baseNumber =
                        itClosestMachine.computerizedNumber    // for문을 계속 돌면서 마지막 machine의 값이 되어있을 것이므로 여기서는 갱신해줘야함.
                    // 좌표 계산 루틴
                    it.apply {
                        projectId = currentProjectId
                        coordinateLng =
                            (itClosestMachine.coordinateLng.toDouble() + cNumberCalculator.getLngDelta()).toString()    // 127
                        coordinateLat =
                            (itClosestMachine.coordinateLat.toDouble() + cNumberCalculator.getLatDelta()).toString()    // 37
                    }
                    localDataSource.insert(it)
                }
            }
        }
    }
}