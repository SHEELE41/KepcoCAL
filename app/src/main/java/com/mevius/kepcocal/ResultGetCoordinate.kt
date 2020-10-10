package com.mevius.kepcocal

data class ResultGetCoordinate (
    var documents : List<DocumentItem>
)

data class DocumentItem(
    var x : Double = 0.0,
    var y : Double = 0.0
)