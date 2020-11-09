package com.mevius.kepcocal.ui.project_detail.data

import android.content.Context
import android.util.Log
import android.widget.Filter
import com.mevius.kepcocal.data.db.entity.Machine
import java.util.*
import kotlin.collections.ArrayList


class FSVDataHelper {
    interface OnFindMachinesListener {
        fun onResults(results: List<MachineWrapper?>?)
    }

    interface OnFindSuggestionsListener {
        fun onResults(results: List<MachineSuggestion?>?)
    }

    companion object{
        private var sMachineWrappers: List<MachineWrapper> = arrayListOf()
        private var sMachineSuggestions: List<MachineSuggestion> = arrayListOf()
        lateinit var liveMachineData: List<Machine>

        fun getHistory(context: Context?, count: Int): List<MachineSuggestion>? {
            val suggestionList: MutableList<MachineSuggestion> = ArrayList()
            var machineSuggestion: MachineSuggestion
            for (i in sMachineSuggestions.indices) {
                machineSuggestion = sMachineSuggestions[i]
                machineSuggestion.setIsHistory(true)
                suggestionList.add(machineSuggestion)
                if (suggestionList.size == count) {
                    break
                }
            }
            return suggestionList
        }

        fun resetSuggestionsHistory() {
            for (machineSuggestion in sMachineSuggestions) {
                machineSuggestion.setIsHistory(false)
            }
        }

        fun findSuggestions(
            context: Context, query: String?, limit: Int, simulatedDelay: Long, listener: OnFindSuggestionsListener
        ) {
            initMachineSuggestionList()
            Log.d("###################################################", sMachineSuggestions[0].body.toString())
            object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults? {
                    try {
                        Thread.sleep(simulatedDelay)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    resetSuggestionsHistory()
                    val suggestionList: MutableList<MachineSuggestion> = ArrayList()
                    if (!(constraint == null || constraint.isEmpty())) {
                        for (suggestion in sMachineSuggestions) {
                            if (suggestion.body?.toUpperCase(Locale.ROOT)
                                    ?.startsWith(constraint.toString().toUpperCase(Locale.ROOT))!!
                            ) {
                                suggestionList.add(suggestion)
                                if (limit != -1 && suggestionList.size == limit) {
                                    break
                                }
                            }
                        }
                    }
                    val results = FilterResults()
                    suggestionList.sortWith { lhs, _ -> if (lhs.getIsHistory()) -1 else 0 }
                    results.values = suggestionList
                    results.count = suggestionList.size
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    listener.onResults(results.values as List<MachineSuggestion?>)
                }
            }.filter(query)
        }


        fun findMachines(query: String?, listener: OnFindMachinesListener?) {
            initMachineWrapperList()
            object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults? {
                    val suggestionList: MutableList<MachineWrapper> = ArrayList()
                    if (!(constraint == null || constraint.isEmpty())) {
                        for (machine in sMachineWrappers) {
                            if (machine.name?.toUpperCase(Locale.ROOT)
                                    ?.startsWith(constraint.toString().toUpperCase(Locale.ROOT))!!
                            ) {
                                suggestionList.add(machine)
                            }
                        }
                    }
                    val results = FilterResults()
                    results.values = suggestionList
                    results.count = suggestionList.size
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    listener?.onResults(results.values as List<MachineWrapper?>)
                }
            }.filter(query)
        }

        private fun initMachineWrapperList() {
            if (sMachineWrappers.isEmpty()) {
                sMachineWrappers = dataToWrapper()
            }
        }

        private fun initMachineSuggestionList() {
            if (sMachineWrappers.isEmpty()) {
                sMachineWrappers = dataToWrapper()
                if (sMachineSuggestions.isEmpty()) {
                    val machineSuggestionList = arrayListOf<MachineSuggestion>()
                    for (machineWrapper in sMachineWrappers){
                        machineSuggestionList.add(MachineSuggestion(machineWrapper.cnum?:""))
                    }
                    sMachineSuggestions = machineSuggestionList.toList()
                }
            }
        }

        private fun dataToWrapper(): List<MachineWrapper> {
            val machineWrapperList = mutableListOf<MachineWrapper>()
            for (machine in liveMachineData){
                machineWrapperList.add(
                    MachineWrapper().apply {
                        cnum = machine.computerizedNumber
                        name = machine.lineName + " " + machine.lineNumber
                    }
                )
                Log.d("############################################## 디버그", machineWrapperList.size.toString())
            }
            return machineWrapperList
        }
    }
}