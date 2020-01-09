package com.peaut.stateview

import android.content.Context
import android.view.View
import android.widget.Checkable

/**
 *
 * @ProjectName:    StateView
 * @Package:        com.peaut.stateview
 * @ClassName:      Ext
 * @Description:
 * @Author:         peaut
 * @CreateDate:     2020-01-08 19:52
 * @UpdateUser:
 * @UpdateDate:     2020-01-08 19:52
 * @UpdateRemark:
 */

//add View extension property (click last time )
var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0

//bind the singleClick with View
inline fun <T : View> T.singleClick(time: Long = 800, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

//dp to px
fun Int.toPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}