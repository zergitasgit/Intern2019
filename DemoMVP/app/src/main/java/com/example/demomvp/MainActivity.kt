package com.example.demomvp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.demomvp.XuLyDangNhap.PresenterLogicXuLyDangNhap
import com.example.demomvp.XuLyDangNhap.ViewXuLyDangNhap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewXuLyDangNhap {

     var presenterLogicXuLyDangNhap =PresenterLogicXuLyDangNhap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_dang_nhap.setOnClickListener {
            var tenDanNhap =edt_ten_dang_nhap.text.toString()
            var matKhau = edt_mat_khau.text.toString()
            presenterLogicXuLyDangNhap.PresenterXuLyDangNhap(this)
            presenterLogicXuLyDangNhap.kiemTraDangNhap(tenDanNhap,matKhau)
        }
    }


    override fun DangNhapThanhCong() {
        Toast.makeText(this, "dang nhap thanh cong", Toast.LENGTH_SHORT).show()
    }

    override fun DangNhapThatBai() {
        Toast.makeText(this, "dang nhap thai bai", Toast.LENGTH_SHORT).show()
    }
}
