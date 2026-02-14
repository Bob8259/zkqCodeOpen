package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IWorkerColors {
    val MainBaseWorker: ColorSchema
    val GoblinWorker: ColorSchema
    val GoblinWorker2: ColorSchema
    val BuilderBaseWorker: ColorSchema
}

object WorkerColors : IWorkerColors {
    override val MainBaseWorker: ColorSchema = ColorSchema.parse(
        452,
        10,
        853,
        82,
        "769BE7",
        "-7|8|CED8E6,-15|-1|90B5ED,-8|-14|6177BB,-3|-21|99C2F0,6|-18|2458C0,9|-13|5E65A1,12|-8|7282C7,5|5|8B92A1,-9|9|CAD4E1",
        0,
        0.9,
    )
    override val GoblinWorker: ColorSchema = ColorSchema.parse(
        452,
        10,
        853,
        82,
        "37AB98",
        "-10|8|57D5CD,-20|0|3B9C8F,-20|-7|2C9098,-8|-12|155D68,-1|-17|48EED9,5|-18|4DFADA,7|-6|2C5A4E,13|-2|49DFC6,3|4|3CAA97",
        0,
        0.9,
    )
    override val GoblinWorker2: ColorSchema = ColorSchema.parse(
        395,
        10,
        654,
        81,
        "16686C",
        "2|2|6E9EA4,-6|2|1A2432,-13|-5|207783,-7|-6|34AEAA,2|-11|49E7D2,3|-11|48E8D3,11|-11|42C4A9,14|-10|3EAE9D,11|-2|48DFD5",
        0,
        0.9,
    )
    override val BuilderBaseWorker: ColorSchema = ColorSchema.parse(
        518,
        5,
        1009,
        78,
        "4375DB",
        "6|-15|A7D0FE,11|-19|3C6BC9,16|-10|8CAAE2,16|-9|403427,18|0|467CD6,7|12|3C69CD,4|12|416FD0,2|12|3E6BCB",
        0,
        0.9,
    )
}
