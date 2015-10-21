package com.github.bluzwong.learn_rx

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import groovy.transform.CompileStatic

@CompileStatic
class TestGroovy extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_groovy);
        LogJava.log("aaaaaaa from testgroovy")
    }
}