package com.example.sqlite;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sqlite.adapter.StudentAdapter;
import com.example.sqlite.data.DBManager;
import com.example.sqlite.model.Student;

import java.util.List;


public class AddStudentFragment extends Fragment {
    View vRoot;
    EditText edtName, edtPhoneNumber, edtAddress, edtEmail;
    Button btnSave;
    DBManager dbManager;
    Student student;
    StudentAdapter studentAdapter;
    List<Student> students;

    public AddStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vRoot = inflater.inflate(R.layout.fragment_add_student, container, false);
        init();
        saveStudent();
        return vRoot;
    }

    private void saveStudent() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();
                String address = edtAddress.getText().toString();
                String email = edtEmail.getText().toString();
                if (name.matches("") && phoneNumber.matches("") && address.matches("") && email.matches("")) {
                    Toast.makeText(getContext(), "Xin vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                } else {
                    student = new Student(name, phoneNumber, address, email);
                    dbManager.addStudent(student);
                    Toast.makeText(getContext(), "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void init() {
        dbManager = new DBManager(getContext());
        edtName = vRoot.findViewById(R.id.edt_name);
        edtPhoneNumber = vRoot.findViewById(R.id.edt_phone_number);
        edtAddress = vRoot.findViewById(R.id.edt_address);
        edtEmail = vRoot.findViewById(R.id.edt_email);
        btnSave = vRoot.findViewById(R.id.btn_save);
    }

}
