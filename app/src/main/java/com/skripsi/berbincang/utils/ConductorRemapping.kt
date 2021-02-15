

package com.skripsi.berbincang.utils

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.skripsi.berbincang.controllers.ChatController

object ConductorRemapping {
    fun remapChatController(router: Router, internalUserId: Long, roomTokenOrId: String, bundle: Bundle, replaceTop: Boolean) {
        val tag = "$internalUserId@$roomTokenOrId"
        if (router.getControllerWithTag(tag) != null) {
            val backstack = router.backstack
            var routerTransaction: RouterTransaction? = null
            for (i in 0 until router.backstackSize) {
                if (tag == backstack[i].tag()) {
                    routerTransaction = backstack[i]
                    backstack.remove(routerTransaction)
                    break
                }
            }

            backstack.add(routerTransaction)
            router.setBackstack(backstack, HorizontalChangeHandler())
        } else {
            if (!replaceTop) {
                router.pushController(RouterTransaction.with(ChatController(bundle))
                        .pushChangeHandler(HorizontalChangeHandler())
                        .popChangeHandler(HorizontalChangeHandler()).tag(tag))
            } else {
                router.replaceTopController(RouterTransaction.with(ChatController(bundle))
                        .pushChangeHandler(HorizontalChangeHandler())
                        .popChangeHandler(HorizontalChangeHandler()).tag(tag))
            }
        }
    }
}
