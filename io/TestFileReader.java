import java.io.FileReader;
import java.io.IOException;

public class TestFileReader {
    public static void main(String[] args) throws IOException {
        int num = 0;
        // 字符流接收使用的char数组
        char[] buf = new char[1024];
        // 字符流、节点流打开文件类
        FileReader fr = new FileReader("TestFileReader.java");// 文件必须存在
        // FileReader.read():取出字符存到buf数组中,如果读取为-1代表为空即结束读取。
        // FileReader.read():读取的是一个字符,但是java虚拟机会自动将char类型数据转换为int数据,
        // 如果你读取的是字符A,java虚拟机会自动将其转换成97,如果你想看到字符可以在返回的字符数前加(char)强制转换如
        while ((num = fr.read(buf)) != -1) {
        }
        // 检测一下是否取到相应的数据
        for (int i = 0; i < buf.length; i++) {
            System.out.print(buf[i]);
        }
    }
}