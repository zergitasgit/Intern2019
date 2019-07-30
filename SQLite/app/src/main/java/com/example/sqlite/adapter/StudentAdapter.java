package com.example.sqlite.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sqlite.R;
import com.example.sqlite.model.Student;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int resource;
    private List<Student> listStudent;


    public StudentAdapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listStudent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvId = (TextView) convertView.findViewById(R.id.tv_item_id);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_item_address);
            viewHolder.tvEmail = (TextView) convertView.findViewById(R.id.tv_item_emal);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_item_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Student student = listStudent.get(position);
        int id = student.getmId();
        String numberAsString = Integer.toString(id);
        viewHolder.tvId.setText(numberAsString);
        viewHolder.tvName.setText(student.getmName());
        viewHolder.tvPhone.setText(student.getmPhoneNumber());
        viewHolder.tvAddress.setText(student.getmAddress());
        viewHolder.tvEmail.setText(student.getmEmail());
        return convertView;

    }

    public class ViewHolder {
        private TextView tvId;
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvEmail;
        private TextView tvAddress;

    }
}
