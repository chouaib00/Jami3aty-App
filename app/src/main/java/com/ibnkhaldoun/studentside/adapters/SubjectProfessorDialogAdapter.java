/*------------------------------------------------------------------------------
 - Copyright (c) 2018. This code was created by Younes Charfaoui in the process of Graduation Project for the year of  2018 , which is about creating a platform  for students and professors to help them in the communication and the get known of the university information and so on.
 -----------------------------------------------------------------------------*/

package com.ibnkhaldoun.studentside.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibnkhaldoun.studentside.R;
import com.ibnkhaldoun.studentside.Utilities.Utilities;

import java.util.List;


public class SubjectProfessorDialogAdapter extends ArrayAdapter<String> {

    private List<String> mSubjectList;

    public SubjectProfessorDialogAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mSubjectList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.subject_professor_dialog_list_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.subject_professor_dialog_title);
        textView.setText(mSubjectList.get(position));
        ImageView circleView = convertView.findViewById(R.id.subject_professor_dialog_circle);
        circleView.setColorFilter(Utilities.getCircleColor(getContext()));
        return convertView;
    }


}
