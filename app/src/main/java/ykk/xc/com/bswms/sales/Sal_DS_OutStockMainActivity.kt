package ykk.xc.com.bswms.sales

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.gprinter.command.EscCommand
import com.gprinter.command.LabelCommand
import kotlinx.android.synthetic.main.sal_ds_out_main.*
import ykk.xc.com.bswms.R
import ykk.xc.com.bswms.bean.ExpressNoData
import ykk.xc.com.bswms.comm.BaseActivity
import ykk.xc.com.bswms.comm.Comm
import ykk.xc.com.bswms.util.adapter.BaseFragmentAdapter
import ykk.xc.com.bswms.util.blueTooth.*
import ykk.xc.com.bswms.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER
import ykk.xc.com.bswms.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED
import java.text.DecimalFormat
import kotlin.collections.ArrayList

/**
 * 日期：2019-10-16 09:14
 * 描述：电商销售出库
 * 作者：ykk
 */
class Sal_DS_OutStockMainActivity : BaseActivity() {

    private val context = this
    private val TAG = "Sal_DS_OutStockMainActivity"
    private var curRadio: View? = null
    private var curRadioName: TextView? = null
    var isChange: Boolean = false // 返回的时候是否需要判断数据是否保存了
//    private val listMaps = ArrayList<Map<String, Any>>()
    private val df = DecimalFormat("#.####")
    val fragment1 = Sal_DS_OutStockFragment1()
    var isMainSave = false // 主表信息是否保存
    // 蓝牙打印用到的
    private var isConnected: Boolean = false // 蓝牙是否连接标识
    private val id = 0 // 设备id
    private var threadPool: ThreadPool? = null
    private val CONN_STATE_DISCONN = 0x007 // 连接状态断开
    private val PRINTER_COMMAND_ERROR = 0x008 // 使用打印机指令错误
    private val CONN_PRINTER = 0x12
    private var listMap = ArrayList<ExpressNoData>() // 打印保存的数据

    override fun setLayoutResID(): Int {
        return R.layout.sal_ds_out_main
    }

    override fun initData() {
        bundle()
        curRadio = viewRadio1
//        curRadioName = tv_radioName1
        val listFragment = ArrayList<Fragment>()
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数
//        Pur_ScInFragment1 fragment1 = new Pur_ScInFragment1();
//        Sal_OutFragment2 fragment2 = new Sal_OutFragment2();
//        Sal_OutFragment3 fragment3 = new Sal_OutFragment3();

        listFragment.add(fragment1)
        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(BaseFragmentAdapter(supportFragmentManager, listFragment))
        //设置ViewPage缓存界面数，默认为1
        viewPager.offscreenPageLimit = 1
        //ViewPager显示第一个Fragment
        viewPager!!.setCurrentItem(0)

        //ViewPager页面切换监听
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
//                when (position) {
//                    0 -> tabChange(viewRadio1!!, tv_radioName1, "表头", 0)
//                    1 -> tabChange(viewRadio2!!, tv_radioName2, "添加分录", 1)
//                    2 -> tabChange(viewRadio3!!, tv_radioName3, "表体", 2)
//                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    private fun bundle() {
        val bundle = context.intent.extras
        if (bundle != null) {
        }
    }

    @OnClick(R.id.btn_close, R.id.lin_tab1, R.id.lin_tab2, R.id.lin_tab3, R.id.btn_appointment)
    fun onViewClicked(view: View) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        when (view.id) {
            R.id.btn_close // 关闭
            -> {
                if (isChange) {
                    val build = AlertDialog.Builder(context)
                    build.setIcon(R.drawable.caution)
                    build.setTitle("系统提示")
                    build.setMessage("您有未保存的数据，继续关闭吗？")
                    build.setPositiveButton("是", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            context.finish()
                        }
                    })
                    build.setNegativeButton("否", null)
                    build.setCancelable(false)
                    build.show()

                } else {
                    context.finish()
                }
            }
            R.id.btn_appointment -> { // 预约
                context.fragment1.appointment()
            }
            R.id.lin_tab1 -> {
//                tabChange(viewRadio1!!, tv_radioName1, "表头", 0)
            }
            R.id.lin_tab2 -> {
                if(isMainSave) {
//                    tabChange(viewRadio2!!, tv_radioName2, "添加分录", 1)
                } else {
                    Comm.showWarnDialog(context,"请先完善（表头）信息！")
                }
            }
            R.id.lin_tab3 -> {
                if(isMainSave) {
//                    tabChange(viewRadio3!!, tv_radioName3, "表体", 2)
                } else {
                    Comm.showWarnDialog(context,"请先完善（表头）信息！")
                }
            }
        }
    }

    /**
     * 选中之后改变样式
     */
    private fun tabSelected(v: View, tv: TextView) {
        curRadio!!.setBackgroundResource(R.drawable.check_off2)
        v.setBackgroundResource(R.drawable.check_on)
        curRadio = v
        curRadioName!!.setTextColor(Color.parseColor("#000000"))
        tv.setTextColor(Color.parseColor("#FF4400"))
        curRadioName = tv
    }

    private fun tabChange(view: View, tv: TextView, str: String, page: Int) {
        tabSelected(view, tv)
//        tv_title.text = str
        viewPager!!.setCurrentItem(page, false)
    }

    /**
     * Fragment回调得到数据
     */
    fun setFragment1DataByPrint(list: List<ExpressNoData>) {
        listMap.clear()
        listMap.addAll(list)

        if (isConnected) {
            setFragment1Print(list)
        } else {
            // 打开蓝牙配对页面
            startActivityForResult(Intent(this, BluetoothDeviceListDialog::class.java), Constant.BLUETOOTH_REQUEST_CODE)
        }
    }

    /**
     * 打印前标
     */
    private fun setFragment1Print(list: List<ExpressNoData>) {
        list.forEach {
            val curDate = Comm.getSysDate(0)
            val tsc = LabelCommand()
            setTscBegin(tsc, 10)
            // --------------- 打印区-------------Begin

            // 上下流水结构，先画线后打印其他
            // 画横线
            tsc.addBar(-10, 90, 788, 2)
            // 画竖线
            tsc.addBar(520, 90, 2, 252)
            // （右）横线
            tsc.addBar(520, 138, 262, 2)
            // 画横线
            tsc.addBar(-10, 216, 788, 2)
            // 画横线
            tsc.addBar(-10, 346, 788, 2)
            // 画横线（收方下面）
            tsc.addBar(-10, 472, 788, 2)
            // 画横线（寄付月结下面）
            tsc.addBar(-10, 556, 788, 2)
            // 画竖线（寄方右边）
            tsc.addBar(520, 556, 2, 160)
            // 画横线（寄方下面）
            tsc.addBar(-10, 670, 520, 2)
            // 画竖线（托寄物内容右边）
            tsc.addBar(268, 670, 2, 48)
            // 画横线（回执单-顺丰速运下面）
            tsc.addBar(-10, 830, 788, 2)
            // 画横线（收件人下面）
            tsc.addBar(-10, 960, 788, 2)

            // （左）条码
            tsc.add1DBarcode(50, 108, LabelCommand.BARCODETYPE.CODE128M, 82, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, it.getT01())   // 顺丰快递单
            // （右）顺丰标快
            tsc.addText(536, 103, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT03()+"\n")   // 顺丰标快 (陆运 )
            tsc.addText(600, 146, LabelCommand.FONTTYPE.FONT_4, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2, it.getT04()+"\n") // E
            tsc.addText(10, 230, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "目的地：\n")
            tsc.addText(108, 252, LabelCommand.FONTTYPE.FONT_4, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_3, LabelCommand.FONTMUL.MUL_3, it.getT02()+"\n") // 466
            tsc.addText(540, 236, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT09()+"\n") // 店铺id
            tsc.addText(540, 310, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, curDate+"\n") // 打单日期
            tsc.addText(10, 356, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "收方："+it.getT10()+"\n") // 收方人
            tsc.addText(276, 356, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT11()+"\n") // 收方电话
//            tsc.addText(10, 388, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.get("t12")+"\n") // 收方地址
            // 收件地址超长，计算自动换行（计算两行）
            val t12 = it.getT12()
            val t12Len = t12!!.length
            if(t12Len > 28) {
                tsc.addText(10, 392, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12.substring(0, 28)+ "\n") // 收方地址
                if(t12.substring(28, t12Len).trim().length > 0) {
                    tsc.addText(10, 423, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12.substring(28, t12Len)+ "\n") // 收方地址
                }
            } else {
                tsc.addText(10, 392, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12+ "\n") // 收方地址
            }
            tsc.addText(10, 480, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT05()+"\n") // 寄付月结
            tsc.addText(178, 480, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "月结卡号："+it.getT06()+"\n") // 月结卡号
            tsc.addText(540, 480, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT07()+"\n") // 转寄协议客户
            tsc.addText(10, 566, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "寄方："+it.getT15()+"\n") // 寄付人
            tsc.addText(540, 566, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "收方签署：\n")
            tsc.addText(276, 566, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT16()+"\n") // 寄付电话
//            tsc.addText(10, 600, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.get("t17")+"\n") // 寄方地址
            // 寄件地址超长，计算自动换行（计算两行）
            val t17 = it.getT17()
            val t17Len = t17!!.length
            if(t17Len > 22) {
                tsc.addText(10, 600, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t17.substring(0, 22)+ "\n") // 寄方地址
                if(t17.substring(22, t17Len).trim().length > 0) {
                    tsc.addText(10, 631, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t17.substring(22, t17Len)+ "\n") // 寄方地址
                }
            } else {
                tsc.addText(10, 600, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t17+ "\n") // 收方地址
            }
            tsc.addText(10, 678, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "托寄物内容：\n")
            tsc.addText(278, 678, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "派件员：\n")
            tsc.addText(540, 678, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "日期：\n")
            // 寄方回执单
            val b = BitmapFactory.decodeResource(resources, R.drawable.shunfeng)
            tsc.addBitmap(18, 736, LabelCommand.BITMAP_MODE.OVERWRITE, 210, b)
            tsc.add1DBarcode(326, 736, LabelCommand.BARCODETYPE.CODE128M, 70, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, 2, 5, it.getT01()) // 顺丰快递单
            tsc.addText(10, 838, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "收件："+it.getT10()+"\n") // 收方人
            tsc.addText(276, 838, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT11()+"\n") // 收方电话
//            tsc.addText(10, 873, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.get("t12")+"\n") // 收方地址
            // 收件地址超长，计算自动换行（计算两行）
            if(t12Len > 28) {
                tsc.addText(10, 873, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12.substring(0, 28)+"\n") // 收方地址
                if(t12.substring(28, t12Len).trim().length > 0) {
                    tsc.addText(10, 904, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12.substring(28, t12Len) + "\n") // 收方地址
                }
            } else {
                tsc.addText(10, 873, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t12+"\n") // 收方地址
            }
            tsc.addText(10, 968, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT08()+"\n") // 店铺名称
            tsc.addText(460, 968, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT09()+"\n")  // 店铺id
//            tsc.addText(10, 1006, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "备注："+it.get("t13")+"\n")  // 卖家备注
            // 备注超长，计算自动换行（计算四行）
            val t13 = it.getT13()
            val t13Len = t13!!.length
            if(t13Len > 27) {
                // 第一行
                tsc.addText(10, 1006, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "备注："+t13.substring(0, 27)+"\n") // 卖家备注
                if(t13.substring(27, t13Len).length > 27) { // 第二行
                    tsc.addText(82, 1036, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t13.substring(27, 54) + "\n") // 卖家备注
                    if(t13.substring(54, t13Len).length > 27) { // 第三行
                        tsc.addText(82, 1066, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t13.substring(54, 81) + "\n") // 卖家备注
                        // 第四行
                        tsc.addText(82, 1096, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t13.substring(81, t13Len) + "\n") // 卖家备注
                    } else {
                        tsc.addText(82, 1066, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t13.substring(54, t13Len) + "\n") // 卖家备注
                    }
                } else {
                    tsc.addText(82, 1036, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, t13.substring(27, t13Len) + "\n") // 卖家备注
                }
            } else {
                // 第一行
                tsc.addText(10, 1006, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "备注："+t13+"\n") // 卖家备注
            }
            tsc.addText(10, 1160, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, it.getT14()+"\n")   // 支付时间
            tsc.addText(300, 1160, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, curDate+"\n") // 打单时间

            // --------------- 打印区-------------End
            setTscEnd(tsc)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // 当选择蓝牙的时候按了返回键
            if (data == null) return
            when (requestCode) {
                /*蓝牙连接*/
                Constant.BLUETOOTH_REQUEST_CODE -> {
                    /*获取蓝牙mac地址*/
                    val macAddress = data.getStringExtra(BluetoothDeviceListDialog.EXTRA_DEVICE_ADDRESS)
                    //初始化话DeviceConnFactoryManager
                    DeviceConnFactoryManager.Build()
                            .setId(id)
                            //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                            //设置连接的蓝牙mac地址
                            .setMacAddress(macAddress)
                            .build()
                    //打开端口
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort()
                }
            }
        }
        mHandler.postDelayed(Runnable {
            context.fragment1.refreshOnActivityResult()
        },200)
    }

    /**
     * 打印前段配置
     * @param tsc
     */
    private fun setTscBegin(tsc: LabelCommand, gap: Int) {
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(100, 150)
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(gap)
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL)
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON)
        // 设置原点坐标
        tsc.addReference(0, 0)
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON)
        // 清除打印缓冲区
        tsc.addCls()
    }

    /**
     * 打印后段配置
     * @param tsc
     */
    private fun setTscEnd(tsc: LabelCommand) {
        // 打印标签
        tsc.addPrint(1, 1)
        // 打印标签后 蜂鸣器响

        tsc.addSound(2, 100)
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255)
        val datas = tsc.command
        // 发送数据
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null) {
            return
        }
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(datas)
    }

    /**
     * 蓝牙监听广播
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                // 蓝牙连接断开广播
                ACTION_USB_DEVICE_DETACHED, BluetoothDevice.ACTION_ACL_DISCONNECTED -> mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget()
                DeviceConnFactoryManager.ACTION_CONN_STATE -> {
                    val state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1)
                    val deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1)
                    when (state) {
                        DeviceConnFactoryManager.CONN_STATE_DISCONNECT -> if (id == deviceId) {
                            tv_connState.setText(getString(R.string.str_conn_state_disconnect))
                            tv_connState.setTextColor(Color.parseColor("#666666")) // 未连接-灰色
                            isConnected = false
                        }
                        DeviceConnFactoryManager.CONN_STATE_CONNECTING -> {
                            tv_connState.setText(getString(R.string.str_conn_state_connecting))
                            tv_connState.setTextColor(Color.parseColor("#6a5acd")) // 连接中-紫色
                            isConnected = false
                        }
                        DeviceConnFactoryManager.CONN_STATE_CONNECTED -> {
                            //                            tv_connState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                            tv_connState.setText(getString(R.string.str_conn_state_connected))
                            tv_connState.setTextColor(Color.parseColor("#008800")) // 已连接-绿色
                            // 连接成功，开始打印
                            setFragment1Print(listMap!!)

                            isConnected = true
                        }
                        CONN_STATE_FAILED -> {
                            Utils.toast(context, getString(R.string.str_conn_fail))
                            tv_connState.setText(getString(R.string.str_conn_state_disconnect))
                            tv_connState.setTextColor(Color.parseColor("#666666")) // 未连接-灰色
                            isConnected = false
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CONN_STATE_DISCONN -> if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null) {
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id)
                }
                PRINTER_COMMAND_ERROR -> Utils.toast(context, getString(R.string.str_choice_printer_command))
                CONN_PRINTER -> Utils.toast(context, getString(R.string.str_cann_printer))
                MESSAGE_UPDATE_PARAMETER -> {
                    val strIp = msg.data.getString("Ip")
                    val strPort = msg.data.getString("Port")
                    //初始化端口信息
                    DeviceConnFactoryManager.Build()
                            //设置端口连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI)
                            //设置端口IP地址
                            .setIp(strIp)
                            //设置端口ID（主要用于连接多设备）
                            .setId(id)
                            //设置连接的热点端口号
                            .setPort(Integer.parseInt(strPort))
                            .build()
                    threadPool = ThreadPool.getInstantiation()
                    threadPool!!.addTask(Runnable { DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort() })
                }
                else -> {
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction(ACTION_USB_DEVICE_DETACHED)
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE)
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy()")
        DeviceConnFactoryManager.closeAllPort()
        if (threadPool != null) {
            threadPool!!.stopThreadPool()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // 按了删除键，回退键
        //        if(!isKeyboard && (event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL)) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        return if (!(event.keyCode == 240 || event.keyCode == 241)) {
            false
        } else super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish()
        }
        return false
    }
}