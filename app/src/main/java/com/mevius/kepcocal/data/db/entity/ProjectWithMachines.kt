package com.mevius.kepcocal.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

class ProjectWithMachines {
    @Embedded
    var project: Project? = null

    @Relation(parentColumn = "id", entityColumn = "project_id", entity = Machine::class)
    var machines: List<Machine> = mutableListOf()
}