package com.mevius.kepcocal.ui.project_detail.data

import android.content.Context
import android.util.Log
import android.widget.Filter
import com.mevius.kepcocal.data.db.entity.Machine
import java.util.*
import kotlin.collections.ArrayList


class FSVDataHelper {
    interface OnFindMachinesListener {
        fun onResults(results: List<Machine?>?)
    }

    interface OnFindSuggestionsListener {
        fun onResults(results: List<MachineSuggestion?>?)
    }

    // Static Area
    companion object{
        private var sMachineSuggestions: List<MachineSuggestion> = listOf()
        lateinit var sLiveMachineData: List<Machine>

        fun getHistory(context: Context?, count: Int): List<MachineSuggestion>? {
            val suggestionList: MutableList<MachineSuggestion> = mutableListOf()
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
            query: String?, limit: Int, simulatedDelay: Long, listener: OnFindSuggestionsListener
        ) {
            initMachineSuggestionList()
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
            object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults? {
                    val suggestionList: MutableList<Machine> = mutableListOf()
                    if (!(constraint == null || constraint.isEmpty())) {
                        for (machine in sLiveMachineData) {
                            if (machine.computerizedNumber.toUpperCase(Locale.ROOT)
                                    .startsWith(constraint.toString().toUpperCase(Locale.ROOT))
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
                    listener?.onResults(results.values as List<Machine?>)
                }
            }.filter(query)
        }

        // 좀 더 간략하게
        private fun initMachineSuggestionList() {
            if (sMachineSuggestions.isEmpty()) {
                val machineSuggestionList = arrayListOf<MachineSuggestion>()
                for (machine in sLiveMachineData){
                    machineSuggestionList.add(MachineSuggestion(machine.computerizedNumber))
                }
                sMachineSuggestions = machineSuggestionList
            }
        }
    }
}