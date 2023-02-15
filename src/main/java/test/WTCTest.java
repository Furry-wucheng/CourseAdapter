package main.java.test;

import main.java.parser.WTCParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/***
 * @title WTCTest
 * @description
 * @author WuCheng
 * @version 1.0.0
 * @create 2023/2/15 14:37
 **/
public class WTCTest {
    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader("E:\\Desktop\\信息查询-宇_files\\queryKbForXsd.html", StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        int len;
        char[] chars = new char[1024];
        while ((len = fileReader.read(chars)) != -1) {
            stringBuilder.append(chars, 0, len);
        }
        WTCParser wtcParser = new WTCParser(stringBuilder.toString());
        wtcParser.generateCourseList();
        wtcParser.saveCourse(true);
    }
}
