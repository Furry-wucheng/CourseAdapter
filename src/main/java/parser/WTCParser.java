package main.java.parser;

import bean.Course;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 武汉职业技术学院新教务系统适配
 * 网址：http://jwxt.wtc.edu.cn/
 * 统一认证 -> 登录 -> 信息查询 -> 我的课表
 * @title WTCParser
 * @description
 * @author WuCheng
 * @version 1.0.0
 * @create 2023/2/15 14:39
 **/
public class WTCParser extends Parser {

    Pattern dayAndStartNodeReg = Pattern.compile("Cell(\\d)(\\d+)");
    Pattern weekPattern1 = Pattern.compile("(\\d{1,2})-(\\d{1,2})");
    Pattern weekPattern2 = Pattern.compile("(\\d{1,2})周?");

    public WTCParser(@NotNull String source) {
        super(source);
    }

    @NotNull
    @Override
    public List<Course> generateCourseList() {
        List<Course> courses = new ArrayList<>();
        Document document = Jsoup.parse(getSource());
        Elements timetable = document.select("tbody").select("tr");
        for (Element tr : timetable) {
            Elements td = tr.getElementsByClass("cell"); //获取到课程的列
            for (Element cell : td) {
                if ("".equals(cell.text()) || cell.text() == null) {
                    continue;//跳过空行
                }
                //处理每行内的课程
                courses.addAll(getCourse(cell));
            }
        }
        return courses;
    }

    public List<Course> getCourse(Element cell) {
        List<Course> courses = new ArrayList<>();
        int day = 0;
        int startNode = 0; //开始节数

        //通过id正则匹配课程所在的周数和第几节课
        Matcher m = dayAndStartNodeReg.matcher(cell.id());

        if (m.find()) {
            day = Integer.valueOf(m.group(1));
            startNode = Integer.valueOf(m.group(2));
        } else {
            System.out.println("未匹配到课程的周数");
            return null;
        }
        Integer endNode = Integer.valueOf(cell.attr("rowspan")) + startNode - 1;

        String[] info = cell.text().replaceAll("\\(.*?\\)", "").split(" ");

        for (int i = 0; i < info.length; i += 6) {
            String[] week = info[i+4].split(",");
//            System.out.println(Arrays.toString(week));
            for (int j = 0; j < week.length; j++) {
                String name = info[i+2];
                String teacher = info[i+3];
                String room = info[i+5];

                int[] startAndEndWeek = getStartAndEndWeek(week[j]);
                int startWeek = startAndEndWeek[0];
                int endWeek = startAndEndWeek[1];

                Course course = new Course(name,day,room,teacher,startNode,endNode,startWeek,endWeek,0,0f,"","","");

//                System.out.println(course);
                courses.add(course);
            }
        }
        return courses;
    }

    public int[] getStartAndEndWeek(String week){ //分离周数
        Matcher weekMatcher1 = weekPattern1.matcher(week);
        Matcher weekMatcher2 = weekPattern2.matcher(week);
        int startWeek = 0;
        int endWeek = 0;
        if (weekMatcher1.find()){
            startWeek = Integer.valueOf(weekMatcher1.group(1));
            endWeek = Integer.valueOf(weekMatcher1.group(2));
        } else if (weekMatcher2.find()){
            startWeek = Integer.valueOf(weekMatcher2.group(1));
            endWeek = startWeek;
        }
        return new int[]{startWeek, endWeek};
    }
}
