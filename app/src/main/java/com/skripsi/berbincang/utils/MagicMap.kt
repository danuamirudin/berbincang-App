

package com.skripsi.berbincang.utils

import java.util.concurrent.ConcurrentHashMap

class MagicMap : ConcurrentHashMap<Int, Any>() {
    fun add(element: Any): Int {
        val key = System.identityHashCode(element)
        super.put(key, element)
        return key
    }
}
