package com.coc.zkqcode.jar.code.universal.aidetection

/**
 * Defines the valid click region polygon for building detection.
 * Only detections whose center falls inside this polygon should be tapped.
 */
object DetectionRegion {

    private val polygon = listOf(
        100 to 95, 100 to 550, 1120 to 550, 1120 to 305,
        1060 to 305, 1060 to 150, 1000 to 150, 1000 to 95
    )

    /**
     * Ray-casting algorithm to check if a point (px, py) is inside the polygon.
     */
    fun isInsideRegion(px: Int, py: Int): Boolean {
        var inside = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            val xi = polygon[i].first
            val yi = polygon[i].second
            val xj = polygon[j].first
            val yj = polygon[j].second
            if (((yi > py) != (yj > py)) &&
                (px < (xj - xi) * (py - yi) / (yj - yi) + xi)
            ) {
                inside = !inside
            }
            j = i
        }
        return inside
    }
}
