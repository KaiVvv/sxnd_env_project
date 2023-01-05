package com.briup.env.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Test;

import com.briup.env.common.entity.Environment;
import com.briup.env.common.interfaces.DbStore;
import com.briup.jdbc.util.JdbcUtil;

public class DbStoreImpl implements DbStore{

    @Override
    public void dbstore(Collection<Environment> coll) {
        // 准备连接对象
        Connection conn = JdbcUtil.getConnectionFomeDruid();
        // 准备ps对象
        PreparedStatement ps = null;
        // 记录入库的操作的时间
        long startTime = System.currentTimeMillis();
        // 定义一个计数器
        int count = 0;
        // 定义记录前一天天数的变量
        int preDay = 0;
        try {
            // 遍历
            for(Environment e : coll) {
                // 比较粗糙的一种方式
                // System.out.println(e.getTime().toString().split("-")[2].substring(0,2));
                // 比较正规的方式
                // 创建一个日历对象
                Calendar calendar = Calendar.getInstance();
                // 设置时间
                calendar.setTime(e.getTime());
                // 取出天数
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                // 根据天数放入到数据库不同的表里
                // 构建不同的sql语句，思考：什么时候才需要重新构建一个sql语句呢？
                if(preDay != day) {
                    // 如果前一天和这一天不是同一天，需要重新创建ps
                    String sql = "insert into env_detail_"+day+" values(?,?,?,?,?,?,?,?,?)";
                    // 创建之前，清空提交
                    if(ps != null) {
                        ps.executeBatch();
                        ps.clearBatch();
                    }
                    // 创建ps对象
                    ps = conn.prepareStatement(sql);
                }

                // 给ps对象输入数据
                ps.setString(1, e.getName());
                ps.setString(2, e.getSrcId());
                ps.setString(3, e.getDesId());
                ps.setString(4, e.getAddress());
                ps.setInt(5, e.getCount());
                ps.setString(6, e.getCmd());
                ps.setFloat(7, e.getData());
                ps.setInt(8, e.getStatus());
                ps.setTimestamp(9, e.getTime());
                // 加入到批处理中
                ps.addBatch();
                count++;
                // 每1000条提交一次
                if(count % 1000 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            // for循环的外面再提交一次
            ps.executeBatch();
            ps.clearBatch();

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            JdbcUtil.close(conn, ps, null);
        }


        long endTime = System.currentTimeMillis();
        System.out.println("【入库操作共耗时："+(endTime-startTime)+"毫秒，共入库："+coll.size()+"条数据】");
    }

    @Test
    public void test() {
        new DbStoreImpl().dbstore(new ServerImpl().receive());
    }

}
