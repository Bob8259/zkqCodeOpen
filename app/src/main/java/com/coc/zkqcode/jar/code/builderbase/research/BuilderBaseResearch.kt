package com.coc.zkqcode.jar.code.builderbase.research

import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch

object BuilderBaseResearch {
    suspend fun research() {
        if (BuilderBaseWorkerAndResearch.detectResearch()) {
            //TODO
        }
    }

}