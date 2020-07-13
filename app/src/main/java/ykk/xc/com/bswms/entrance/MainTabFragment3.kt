package ykk.xc.com.bswms.entrance


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.OnClick
import ykk.xc.com.bswms.R
import ykk.xc.com.bswms.comm.BaseFragment
import ykk.xc.com.bswms.sales.*
import ykk.xc.com.bswms.warehouse.OutInStock_Search_MainActivity

/**
 * 销售
 */
class MainTabFragment3 : BaseFragment() {

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.aa_main_item3, container, false)
    }

    @OnClick(R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.relative1 -> {// 销售出库
                val bundle = Bundle()
                bundle.putInt("pageId", 11)
                bundle.putString("billType", "CGFH")
                show(OutInStock_Search_MainActivity::class.java, bundle)
//                show(Sal_OutStockMainActivity::class.java, null)
            }
            R.id.relative2 -> {// 销售出库
                show(Sal_DS_OutStockMainActivity::class.java, null)
            }
            R.id.relative3 -> {// 销售退货
                show(Sal_DS_OutStock_RED_MainActivity::class.java, null)
            }
            R.id.relative4 -> {// 快递打印
                show(Sal_DS_OutStockPrintActivity::class.java, null)
            }
            R.id.relative5 -> {// 电商退生产

            }
            R.id.relative6 // 销售装箱
            -> {
            }
        }//                show(Sal_ScOutMainActivity.class, null);
        //                show(Sal_DsOutReturnMainActivity.class, null);
        //                show(Sal_NxOutReturnMainActivity.class, null);
        //                show(Sal_DsBToRFromPurchaseInStockMainActivity.class, null);
        //                show(Sal_OutStockMainActivity.class, null);
    }
}
