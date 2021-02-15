

package com.skripsi.berbincang.interfaces

interface SelectionInterface {
    fun toggleBrowserItemSelection(path: String)

    fun isPathSelected(path: String): Boolean
}
