package com.example.demomvp.XuLyDangNhap

public class PresenterLogicXuLyDangNhap : PresenterImpXuLyDangNhap {
    lateinit var viewXuLyDangNhap: ViewXuLyDangNhap

    fun PresenterXuLyDangNhap(viewXuLyDangNhap: ViewXuLyDangNhap) {
        this.viewXuLyDangNhap = viewXuLyDangNhap
    }

    override fun kiemTraDangNhap(tenDangNhap: String, matKhau: String) {
        if (tenDangNhap.equals("trung") && matKhau.equals("123")) {
            // tra ra view Dang nhap thanh cong
            viewXuLyDangNhap.DangNhapThanhCong()
        } else {
            // tra ra view dang nhap that bai
            viewXuLyDangNhap.DangNhapThatBai()
        }
    }

}
