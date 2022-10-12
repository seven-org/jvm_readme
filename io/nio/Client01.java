package nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

//TCP协议Socket:客户端
public class Client01 {
    public static void main(String[] args) throws IOException {
        // 创建套接字对象socket并封装ip与port
        Socket socket = new Socket("127.0.0.1", 8000);
        // 根据创建的socket对象获得一个输出流
        OutputStream outputStream = socket.getOutputStream();
        // 控制台输入以IO的形式发送到服务器
        System.out.println("TCP连接成功 \n请输入:");
        while (true) {
            byte[] car = new Scanner(System.in).nextLine().getBytes();
            outputStream.write(car);
            System.out.println("TCP协议的Socket发送成功");
            // 刷新缓冲区
            outputStream.flush();
        }
    }
}