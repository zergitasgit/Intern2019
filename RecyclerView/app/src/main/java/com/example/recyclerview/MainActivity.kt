package com.example.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview.model.Nhac
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var data: ArrayList<Nhac> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conFigRvMain()
    }

    private fun conFigRvMain() {
        setData()
        rv_main.layoutManager = LinearLayoutManager(this)
        val mainAdapter = MainAdapter(data, this)
        mainAdapter.notifyDataSetChanged()
        rv_main.adapter = mainAdapter


    }

    private fun setData() {
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))
        data.add(Nhac("Hãy Trao cho anh", "Sơn Tùng MTP", R.drawable.img_son_tung, false))
        data.add(Nhac("Chắc ai đó sẽ về", "Mèo Đẹp Trai", R.drawable.img_anh_1, false))
        data.add(Nhac("Buông đôi tay nhau ra", "Mèo Cute", R.drawable.img_anh_2, false))
        data.add(Nhac("We don't thuộc về nhau", "Mèo Khá Bảnh", R.drawable.img_anh_3, false))


    }

}
