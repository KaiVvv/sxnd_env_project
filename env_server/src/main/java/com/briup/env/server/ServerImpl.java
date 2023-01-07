package com.briup.env.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.briup.env.common.entity.Environment;
import com.briup.env.common.interfaces.Configuration;
import com.briup.env.common.interfaces.Server;

public class ServerImpl implements Server{

    private int port;

    @Override
    public void init(Properties properties) {
        port = Integer.parseInt(properties.getProperty("port"));
    }

    @Override
    public Collection<Environment> receive() {
        // 定义一个返回的数据
       Logger logger=Logger.getLogger(ServerImpl.class);
        Collection<Environment> coll = null;
        // 完成TCP/IP服务器的编写
        ServerSocket serverSocket = null;
        Socket socket = null;
        // 对象输入流

        ObjectInputStream ois = null;
            try {
                serverSocket = new ServerSocket(port);
                // 开启监听
                socket = serverSocket.accept();//1
                logger.debug("连接的客户端主机名：" + socket.getInetAddress().getHostName());//1
                logger.debug("连接的客户端主机地址：" + socket.getInetAddress().getHostAddress());
                // System.out.println("【服务已经启动，正在监听"+port+"端口】");
                // socket = serverSocket.accept();
                // System.out.println(socket);

                ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                coll = new LinkedList<>();
                // 比较安全的强转方式
                if (obj instanceof Collection<?>) {
                    Collection<?> collection = (Collection<?>) obj;
                    for (Object o : collection) {
                        if (o instanceof Environment) {
                            Environment e = (Environment) o;
                            coll.add(e);
                        }
                    }
                }
                logger.debug("服务端成功接收客户端发送的数据！");
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
                logger.debug(e.getMessage());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null)
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                if (serverSocket != null)
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        logger.error(e.getMessage());
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



    @Override
    public void config(Configuration configuration) {
        // TODO Auto-generated method stub

    }

}
