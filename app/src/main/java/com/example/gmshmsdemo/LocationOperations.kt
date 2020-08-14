package com.example.gmshmsdemo

import androidx.appcompat.app.AppCompatActivity

interface LocationOperations {
    fun checkLocationSolver(
        resolve: Boolean,
        activity: AppCompatActivity,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )
    fun removeCallback()
}