import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TestBufferedWriter {
    public static void main(String[] args) throws IOException {
        // File是操作文件类
        File file = new File("D:\\test.txt");// 文件必须存在
        // 字符流、节点流写出文件类
        // new FileWriter(file),这个我没加true代表覆盖文件
        Writer writer = new FileWriter(file);
        //// 字符流、处理流写出文件类
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write("\n小心");
        bw.close();
        writer.close();
    }
}