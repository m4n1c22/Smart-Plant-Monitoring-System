package com.parse.tutorials.pushnotifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.EditText;

public class InsertDeviceID extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_insert_device_id, null))
                // Set Dialog Icon

                        // Set Dialog Title
                .setTitle("Input DeviceID")

                        // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        EditText newId = (EditText) getDialog().findViewById(R.id.new_device_id);

                        MainActivity mainActivity = (MainActivity)getActivity();
                        mainActivity.setNewDeviceId(newId.getText().toString());
                    }
                })

                        // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                });
       return builder.create();
    }
}