package com.briup.env.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

import org.junit.Test;

import com.briup.env.common.entity.Environment;
import com.briup.env.common.interfaces.Client;

public class ClientImpl implements Client{

    @Override
    public void send(Collection<Environment> coll) {
        // 定义网络模块的客户端
        Socket socket = null;
        String ip = "127.0.0.1";
        int port = 8888;
        // 对象输出流
        ObjectOutputStream oos = null;
        try {
            System.out.println("【客户端正在访问<"+ip+":"+port+">服务】");
            socket = new Socket(ip,port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(coll);
            oos.flush();
            oos.close();
            System.out.println("【数据清单发送成功！】");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    @Test
    public void test() {
        new ClientImpl().send(null);
    }

}
