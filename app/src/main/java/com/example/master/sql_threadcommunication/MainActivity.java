package com.example.master.sql_threadcommunication;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
/*这个程序实现了点击按钮向数据库存储数据的功能及UI更新功能*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /*公共变量区*/
    public static int flag=0;//线程执行控制位；
    public static final String TAG = "MainActivity";//LogCat调试用
    public int i = 1;
    private Handler mHandler;//将mHandler指定轮询的Looper
    public Button button;//添加数据按钮
    public TextView textView;
    /*公共变量区结束*/

    /*SQL代码变量区*/
    private String url,user,password;
    public static Connection conn=null;
    public static java.sql.Statement statement=null;
    public static ResultSet rs=null;
    HandlerThread thread = new HandlerThread("handler thread");//实例化一个特殊的线程HandlerThread，必须给其指定一个名字
    /*SQL代码变量区结束*/

    /*SQL方法区*/
    private void conn_set()
    {
        // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
        String ip = "192.168.186.154";//记住每天开始调试程序前对IP地址进行手动更新
        int port = 3306;
        String dbName = "test";
        url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName; // 构建连接mysql的字符串
        user = "root";
        password =null;
    }
    private void sql_query()
    {
        String sql_query ="select xyz_name from xyz" +
                " where xyz_id=2318" ;
        try {
            // 执行sql查询语句并获取查询信息
            rs=statement.executeQuery(sql_query);
            while (rs.next())
            {
                Log.e(MainActivity.TAG,rs.getString(1));
            }
        } catch (SQLException e) {

            Log.e(MainActivity.TAG, e.toString());
        }
    }
    private void sql_update()
    {
        String sql_update="insert into xyz(xyz_id,xyz_name) values(null,'小傻子');";
        try {
            statement.executeUpdate(sql_update);
        }catch (SQLException e)
        {
            Log.e(MainActivity.TAG,e.toString());
        }
    }

    /*SQL方法区结束*/

/*公共方法区*/

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.button)
        {

            mHandler = new Handler(thread.getLooper()) //将mHandler与thread相关联
            {
                public void handleMessage(android.os.Message msg) {
                    /*下面开始业务逻辑处理*/
                    conn_set();//设置连接
                    // 3.连接JDBC
                    try {
                        conn= DriverManager.getConnection(url, user, password);
                        statement= conn.createStatement();
                        Log.i("MainActivity", "远程连接成功!");
                    } catch (SQLException e) {
                        Log.e("MainActivity", "远程连接失败!请检查IP地址是否更新"+e.toString());
                    }
                    sql_update();
                    Log.e("MainActivity","更新数据库成功！");

                    /*使用runOnUIThread完成UI更新动作*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("数据库更新完成");
                        }
                    });

                    /*子线程中不能修改UI，此处代码需要修改，否则报错*///textView.setText("添加数据库数据成功");
                    //sql_update();
                    try {
                        conn.close();
                        Log.d("MainActivity","成功关闭连接");
                    } catch (SQLException e) {
                        Log.d("MainActivity", "关闭连接失败");
                    }
                }
            };


            Message message_HandlerThread=new Message();
            message_HandlerThread.what=1;
            mHandler.sendMessage(message_HandlerThread);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        textView=findViewById(R.id.textView);
        button.setOnClickListener(this);
        thread.start();//开启HandlerThread
    }

//主线程handler部分

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    /*公共方法区结束*/
}

/*
*
* */
/*这一句注释仅作为GitHub测试更新的用途，可删掉*/
