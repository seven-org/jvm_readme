import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestBufferedOutputStream {
    // 创建文件输入流对象,关联致青春.mp3
    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("D:\\copy.mp3");
        // 创建缓冲区对fis装饰
        BufferedInputStream bis = new BufferedInputStream(fis);
        // 创建输出流对象,关联copy.mp3
        FileOutputStream fos = new FileOutputStream("D:\\copy2.mp3");
        // 创建缓冲区对fos装饰
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        // 循环直接输出
        int i;
        while ((i = bis.read()) != -1) {
            bos.write(i);
        }
        bis.close();
        bos.close();
    }
}