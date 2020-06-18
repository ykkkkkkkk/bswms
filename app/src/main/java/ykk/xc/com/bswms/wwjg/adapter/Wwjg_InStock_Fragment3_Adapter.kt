package ykk.xc.com.bswms.wwjg.adapter

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ykk.xc.com.bswms.R
import ykk.xc.com.bswms.bean.ICStockBillEntry
import ykk.xc.com.bswms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.bswms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Wwjg_InStock_Fragment3_Adapter(private val context: Activity, datas: List<ICStockBillEntry>) : BaseArrayRecyclerAdapter<ICStockBillEntry>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.wwjg_in_stock_fragment3_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ICStockBillEntry, pos: Int) {
        // 初始化id
        val tv_mtlNumber = holder.obtainView<TextView>(R.id.tv_mtlNumber)
        val tv_mtlName = holder.obtainView<TextView>(R.id.tv_mtlName)
        val tv_fmode = holder.obtainView<TextView>(R.id.tv_fmode)
        val tv_num = holder.obtainView<TextView>(R.id.tv_num)
        val tv_price = holder.obtainView<TextView>(R.id.tv_price)
        val tv_money = holder.obtainView<TextView>(R.id.tv_money)
        val tv_remark = holder.obtainView<TextView>(R.id.tv_remark)
        val tv_modify = holder.obtainView<TextView>(R.id.tv_modify)
        val tv_del = holder.obtainView<TextView>(R.id.tv_del)
        val lin_button = holder.obtainView<LinearLayout>(R.id.lin_button)

        // 赋值
        tv_mtlNumber.text = entity.icItem.fnumber
        tv_mtlName.text = entity.icItem.fname
        tv_fmode?.text = entity.icItem.fmodel
        tv_num.text = df.format(entity.fqty) + entity.unit.unitName
        tv_price.text = df.format(entity.fprice)
        tv_money.text = df.format(entity.fqty * entity.fprice)
        tv_remark?.text = entity.remark

        if (entity.isShowButton) {
            lin_button!!.setVisibility(View.VISIBLE)
        } else {
            lin_button!!.setVisibility(View.GONE)
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.tv_modify // 修改
                -> if (callBack != null) {
                    callBack!!.onModify(entity, pos)
                }
                R.id.tv_del // 删除行
                -> if (callBack != null) {
                    callBack!!.onDelete(entity, pos)
                }
            }
        }
        tv_modify!!.setOnClickListener(click)
        tv_del!!.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onModify(entity: ICStockBillEntry, position: Int)
        fun onDelete(entity: ICStockBillEntry, position: Int)
    }

}
