package com.briup.env.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import com.briup.env.common.entity.Environment;
import com.briup.env.common.interfaces.Server;

public class ServerImpl implements Server{

    @Override
    public Collection<Environment> receive() {
        // 定义一个返回的数据
        Collection<Environment> coll = null;
        // 完成TCP/IP服务器的编写
        ServerSocket serverSocket = null;
        Socket socket = null;
        int port = 8888;
        // 对象输入流
        ObjectInputStream ois = null;

        try {
            serverSocket = new ServerSocket(port);
            // 开启监听
            System.out.println("【服务已经启动，正在监听"+port+"端口】");
            socket = serverSocket.accept();
            System.out.println(socket);

            ois = new ObjectInputStream(socket.getInputStream());
            Object obj = ois.readObject();
            coll = new LinkedList<>();
            // 比较安全的强转方式
            if(obj instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>)obj;
                for(Object o : collection) {
                    if(o instanceof Environment) {
                        Environment e = (Environment)o;
                        coll.add(e);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(socket != null)
                try {
                    socket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            if(serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return coll;
    }

    @Test
    public void test() {
        Collection<Environment> coll = new ServerImpl().receive();
        coll.forEach(System.out::println);
        System.out.println("服务器端接收的数据共："+coll.size()+"条");
    }

}
