package ykk.xc.com.bswms.basics.adapter

import android.app.Activity
import android.widget.TextView
import ykk.xc.com.bswms.R
import ykk.xc.com.bswms.bean.ICItemClasses
import ykk.xc.com.bswms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.bswms.util.basehelper.BaseRecyclerAdapter

class ICItemClasses_DialogAdapter(private val context: Activity, private val datas: List<ICItemClasses>) : BaseArrayRecyclerAdapter<ICItemClasses>(datas) {
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ab_icitem_classes_dialog_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ICItemClasses, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_fnumber = holder.obtainView<TextView>(R.id.tv_fnumber)
        val tv_fname = holder.obtainView<TextView>(R.id.tv_fname)
        // 赋值
        tv_row!!.setText((pos + 1).toString())
        tv_fnumber!!.setText(entity.fnumber)
        tv_fname!!.setText(entity.fname)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onClick(entity: ICItemClasses, position: Int)
    }

}
