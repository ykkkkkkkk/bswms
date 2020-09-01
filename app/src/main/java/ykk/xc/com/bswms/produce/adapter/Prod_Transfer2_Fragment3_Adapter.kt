package ykk.xc.com.bswms.produce.adapter

import android.app.Activity
import android.text.Html
import android.view.View
import android.widget.TextView
import ykk.xc.com.bswms.R
import ykk.xc.com.bswms.bean.ICStockBillEntry
import ykk.xc.com.bswms.comm.Comm
import ykk.xc.com.bswms.util.basehelper.BaseArrayRecyclerAdapter
import ykk.xc.com.bswms.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class Prod_Transfer2_Fragment3_Adapter(private val context: Activity, datas: List<ICStockBillEntry>) : BaseArrayRecyclerAdapter<ICStockBillEntry>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.prod_transfer2_fragment3_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ICStockBillEntry, pos: Int) {
        // 初始化id
        val tv_row = holder.obtainView<TextView>(R.id.tv_row)
        val tv_mtlNumber = holder.obtainView<TextView>(R.id.tv_mtlNumber)
        val tv_mtlName = holder.obtainView<TextView>(R.id.tv_mtlName)
        val tv_batchNo = holder.obtainView<TextView>(R.id.tv_batchNo)
        val tv_fmodel = holder.obtainView<TextView>(R.id.tv_fmodel)
        val tv_num = holder.obtainView<TextView>(R.id.tv_num)
        val tv_sourceQty = holder.obtainView<TextView>(R.id.tv_sourceQty)
//        val view_del = holder.obtainView<View>(R.id.view_del)
        val view_check = holder.obtainView<View>(R.id.view_check)
        val tv_inStockQty = holder.obtainView<TextView>(R.id.tv_inStockQty)
        val tv_stockName = holder.obtainView<TextView>(R.id.tv_stockName)
        val tv_stockAreaName = holder.obtainView<TextView>(R.id.tv_stockAreaName)
        val tv_storageRackName = holder.obtainView<TextView>(R.id.tv_storageRackName)
        val tv_stockPosName = holder.obtainView<TextView>(R.id.tv_stockPosName)
        val tv_outStockQty = holder.obtainView<TextView>(R.id.tv_outStockQty)
        val tv_stockName2 = holder.obtainView<TextView>(R.id.tv_stockName2)
        val tv_stockAreaName2 = holder.obtainView<TextView>(R.id.tv_stockAreaName2)
        val tv_storageRackName2 = holder.obtainView<TextView>(R.id.tv_storageRackName2)
        val tv_stockPosName2 = holder.obtainView<TextView>(R.id.tv_stockPosName2)

        // 赋值
        tv_row.text = (pos+1).toString()
        tv_mtlName.text = entity.icItem.fname
        tv_mtlNumber.text = Html.fromHtml("代码:&nbsp;<font color='#6a5acd'>"+entity.icItem.fnumber+"</font>")
        if(Comm.isNULLS(entity.strBatchCode).length > 0) {
            tv_batchNo.visibility = View.VISIBLE
            tv_batchNo.text = Html.fromHtml("批次:&nbsp;<font color='#6a5acd'>" + entity.strBatchCode + "</font>")
        } else {
            tv_batchNo.visibility = View.INVISIBLE
        }
        tv_fmodel.text = Html.fromHtml("规格型号:&nbsp;<font color='#6a5acd'>"+ Comm.isNULLS(entity.icItem.fmodel)+"</font>")

        tv_num.text = Html.fromHtml("实发数:&nbsp;<font color='#FF4400'>"+ df.format(entity.fqty) +"</font>")
        tv_sourceQty.text = Html.fromHtml("应发数:&nbsp;<font color='#6a5acd'>"+ df.format(entity.fsourceQty) +"</font>&nbsp;<font color='#666666'>"+ entity.unit.unitName +"</font>")
        tv_inStockQty.text = Html.fromHtml("库存:&nbsp;<font color='#000000'>"+ df.format(entity.inStockQty) +"</font>")
        if(entity.fqty > entity.outStockQty) {
            tv_outStockQty.text = Html.fromHtml("库存:&nbsp;<font color='#FF0000'>"+ df.format(entity.outStockQty) +"</font>")
        } else {
            tv_outStockQty.text = Html.fromHtml("库存:&nbsp;<font color='#000000'>" + df.format(entity.outStockQty) + "</font>")
        }
        val parent = tv_row.parent as View
        if(entity.isShowButton) {
            parent.setBackgroundResource(R.drawable.back_style_check1_true)
            view_check.setBackgroundResource(R.drawable.check_true)
        } else {
            parent.setBackgroundResource(R.drawable.back_style_check1_false)
            view_check.setBackgroundResource(R.drawable.check_false)
        }

        // 显示调入仓库组信息
        if(entity.stock != null ) {
            tv_stockName.visibility = View.VISIBLE
            tv_stockName.text = Html.fromHtml("调入仓库:&nbsp;<font color='#000000'>"+entity.stock.stockName+"</font>")
        } else {
            tv_stockName.visibility = View.GONE
        }
        if(entity.stockArea != null ) {
            tv_stockAreaName.visibility = View.VISIBLE
            tv_stockAreaName.text = Html.fromHtml("库区:&nbsp;<font color='#000000'>"+entity.stockArea.fname+"</font>")
        } else {
            tv_stockAreaName.visibility = View.GONE
        }
        if(entity.storageRack != null ) {
            tv_storageRackName.visibility = View.VISIBLE
            tv_storageRackName.text = Html.fromHtml("货架:&nbsp;<font color='#000000'>"+entity.storageRack.fnumber+"</font>")
        } else {
            tv_storageRackName.visibility = View.GONE
        }
        if(entity.stockPos != null ) {
            tv_stockPosName.visibility = View.VISIBLE
            tv_stockPosName.text = Html.fromHtml("库位:&nbsp;<font color='#000000'>"+entity.stockPos.stockPositionName+"</font>")
        } else {
            tv_stockPosName.visibility = View.GONE
        }
        // 显示调出仓库组信息
        if(entity.stock2 != null ) {
            tv_stockName2.visibility = View.VISIBLE
            tv_stockName2.text = Html.fromHtml("调出仓库:&nbsp;<font color='#000000'>"+entity.stock2.stockName+"</font>")
        } else {
            tv_stockName2.visibility = View.GONE
        }
        if(entity.stockArea2 != null ) {
            tv_stockAreaName2.visibility = View.VISIBLE
            tv_stockAreaName2.text = Html.fromHtml("库区:&nbsp;<font color='#000000'>"+entity.stockArea2.fname+"</font>")
        } else {
            tv_stockAreaName2.visibility = View.GONE
        }
        if(entity.storageRack2 != null ) {
            tv_storageRackName2.visibility = View.VISIBLE
            tv_storageRackName2.text = Html.fromHtml("货架:&nbsp;<font color='#000000'>"+entity.storageRack2.fnumber+"</font>")
        } else {
            tv_storageRackName2.visibility = View.GONE
        }
        if(entity.stockPos2 != null ) {
            tv_stockPosName2.visibility = View.VISIBLE
            tv_stockPosName2.text = Html.fromHtml("库位:&nbsp;<font color='#000000'>"+entity.stockPos2.stockPositionName+"</font>")
        } else {
            tv_stockPosName2.visibility = View.GONE
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                /*R.id.view_del -> { // 删除行
                    if (callBack != null) {
                        callBack!!.onDelete(entity, pos)
                    }
                }*/
                R.id.view_check -> { // 选中行
                    if (callBack != null) {
                        callBack!!.onCheck(v, entity, pos)
                    }
                }
            }
        }
//        view_del.setOnClickListener(click)
        view_check.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onDelete(entity: ICStockBillEntry, position: Int)
        fun onCheck(v :View, entity: ICStockBillEntry, position: Int)
    }

}
