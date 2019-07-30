package com.example.sqlite;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sqlite.adapter.StudentAdapter;
import com.example.sqlite.data.DBManager;
import com.example.sqlite.model.Student;

import java.util.ArrayList;
import java.util.List;


public class ListStudentFragment extends Fragment {
    View vRoot;
    StudentAdapter studentAdapter;
    DBManager dbManager;
    List<Student> students;

    ListView rvListStudent;

    public ListStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_list_student, container, false);
        init();
        configRv();
        return vRoot;
    }

    private void configRv() {
        if (studentAdapter == null) {
            studentAdapter = new StudentAdapter(getContext(), R.layout.item_student_layout, students);
        }
        rvListStudent.setAdapter(studentAdapter);
        rvListStudent.not

    }

    private void init() {
        dbManager = new DBManager(getContext());
         students = dbManager.getAllStudent();
        rvListStudent = vRoot.findViewById(R.id.rv_list);
    }

}
