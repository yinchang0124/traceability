package com.example.keer.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.ProgressStyle;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

public class buyinfoActivity extends AppCompatActivity implements View.OnClickListener {
    private int REQUEST_CODE_SCAN = 111;

    LinearLayout linearLayout;
    TableLayout tableLayout;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_addpig);
        initXml();//代码动态创建布局
        addView();//给创建的布局中添加控件
    }
    private void initXml(){
        linearLayout = new LinearLayout(this);//创建一个线性布局
        linearLayout.setBackgroundColor(Color.WHITE);//设置背景颜色
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);//设置方向水平
        setContentView(linearLayout);

    }

    private void addView(){
        final LockTableView mLockTableView = new LockTableView(this, linearLayout, data());
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(false) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(100) //列最大宽度
                .setMinColumnWidth(60) //列最小宽度
                .setColumnWidth(1,60) //设置指定列文本宽度(从0开始计算,宽度单位dp)
                .setMinRowHeight(20)//行最小高度
                .setMaxRowHeight(60)//行最大高度
                .setTextViewSize(16) //单元格字体大小
                .setCellPadding(15)//设置单元格内边距(dp)
                .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                .setNullableString("N/A") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    //设置横向滚动监听
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    //设置横向滚动边界监听
                    @Override
                    public void onLeft(HorizontalScrollView view) {
                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
                        Log.e("滚动边界","滚动到最右边");
                    }
                })
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    //下拉刷新、上拉加载监听
                    @Override
                    public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        Log.e("表格主视图", String.valueOf(mXRecyclerView));
                        Log.e("表格所有数据", String.valueOf(mTableDatas));
                        //如需更新表格数据调用,部分刷新不会全部重绘
                        mLockTableView.setTableDatas(mTableDatas);
                        //停止刷新
                        mXRecyclerView.refreshComplete();
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        Log.e("表格主视图", String.valueOf(mXRecyclerView));
                        Log.e("表格所有数据", String.valueOf(mTableDatas));
                        //如需更新表格数据调用,部分刷新不会全部重绘
                        mLockTableView.setTableDatas(mTableDatas);
                        //停止刷新
                        mXRecyclerView.loadMoreComplete();
                        //如果没有更多数据调用
                        mXRecyclerView.setNoMore(true);
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                        Log.e("点击事件",position+"");

                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("长按事件",position+"");
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);
//属性值获取
        Log.e("每列最大宽度(dp)", mLockTableView.getColumnMaxWidths().toString());
        Log.e("每行最大高度(dp)", mLockTableView.getRowMaxHeights().toString());
        Log.e("表格所有的滚动视图", mLockTableView.getScrollViews().toString());
        Log.e("表格头部固定视图(锁列)", mLockTableView.getLockHeadView().toString());
        Log.e("表格头部固定视图(不锁列)", mLockTableView.getUnLockHeadView().toString());

    }

    private ArrayList<ArrayList<String>> data(){
        ArrayList<ArrayList<String>> tabledata=new ArrayList<ArrayList<String>>();
        ArrayList<String> row1=new ArrayList<>();
        row1.add("erc721");
        row1.add("品种");
        row1.add("重量");
        row1.add("状态");


        tabledata.add(row1);
        for(int i=0;i<20;i++){
            ArrayList<String> row=new ArrayList<String>();
            row.add(""+i);
            row.add("大约克");
            row.add("120");
            row.add("良好");
            tabledata.add(row);
        }
        return  tabledata;
    }

    @Override
    public void onClick(View v) {

//        if(v.getId()==R.id.btn_info){
//            Intent intent=new Intent(this,InfoActivity.class);
//            startActivity(intent);
//        }

        if(v.getId()==R.id.btn_orderlist){
            Intent intent=new Intent(this,buyinfoActivity.class);
            startActivity(intent);
        }

        if(v.getId()==R.id.btn_logout){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        if(v.getId()==R.id.btn_scan) {
            scan();
        }
    }

    public  void scan(){
        AndPermission.with(this)
                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Intent intent = new Intent(buyinfoActivity.this, CaptureActivity.class);

                        /**ZxingConfig是配置类
                         *可以设置是否显示底部布局，闪光灯，相册，
                         * 是否播放提示音  震动
                         * 设置扫描框颜色等
                         * 也可以不传这个参数
                         * */
                        ZxingConfig config = new ZxingConfig();
                        // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                        //  config.setShake(false);//是否震动  默认为true
                        // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(buyinfoActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }
                }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                //result.setText("扫描结果为：" + content);
                Intent intent = new Intent(this,PigInfoActivity.class);
                intent.putExtra("BigChainDB",content);
                startActivity(intent);
            }
        }
    }
}
