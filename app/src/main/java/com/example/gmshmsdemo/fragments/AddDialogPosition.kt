package com.example.gmshmsdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.gmshmsdemo.model.LandMarkObject
import com.example.gmshmsdemo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddDialogPosition private constructor (val initialPosition: LandMarkObject, val finalPosition: LandMarkObject, val onSuccess: (String) -> Unit) :BottomSheetDialogFragment() {

    private lateinit var rootView:View
    companion object {
        fun newInstance(initialPosition: LandMarkObject, finalPosition: LandMarkObject, onSuccess: (String)->Unit): AddDialogPosition =
            AddDialogPosition(initialPosition,finalPosition,onSuccess).apply {
                /*
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
                 */
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(
            R.layout.bottom_sheet, container,
            false
        )
        val walkButton= rootView.findViewById<Button>(R.id.walk)
        val cyclingButton= rootView.findViewById<Button>(R.id.cycling)
        val driveButton= rootView.findViewById<Button>(R.id.drive)
        val initialPositionText= rootView.findViewById<TextView>(R.id.initial_destination)
        val finalPositionText= rootView.findViewById<TextView>(R.id.arrival_destination)

        walkButton.setOnClickListener {
            onSuccess("walking")
            dismiss()
        }
        cyclingButton.setOnClickListener {
            onSuccess("bicycling")
            dismiss()
        }
        driveButton.setOnClickListener {
            onSuccess("driving")
            dismiss()
        }
        initialPositionText.text="latitude:${initialPosition.latitude}\nlongitude:${initialPosition.longitude}"
        finalPositionText.text="latitude:${finalPosition.latitude}\nlongitude:${finalPosition.longitude}"
        return rootView
    }
}