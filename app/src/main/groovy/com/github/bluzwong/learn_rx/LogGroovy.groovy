package com.github.bluzwong.learn_rx

import android.util.Log
import groovy.transform.CompileStatic

class LogGroovy {
    static def print(msg) {
        Log.i("ccf", msg?.toString()?:"null")
    }
}