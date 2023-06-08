package com.intelligent.bot.utils.sys;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtil {

    public static final String ISO_DATE_FORMAT = "yyyyMMdd";
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATETIME_PATTERN_STRING = "HH:mm";
    public static String DATETIME_HHMMSS = "HH:mm:ss";
    public static String DATE_PATTERN = "yyyyMMddHHmmss";
    private static boolean LENIENT_DATE = false;
    private static Random random = new Random();
    private static final int ID_BYTES = 10;

    public synchronized static String generateId() {
        StringBuilder result = new StringBuilder();
        result.append(System.currentTimeMillis());
        for (int i = 0; i < ID_BYTES; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    protected static final float normalizedJulian(float JD) {

        float f = Math.round(JD + 0.5f) - 0.5f;

        return f;
    }

    public static String getDate() {
        Date date = new Date();
        return dateToString(date,"yyyy-MM-dd");
    }

    public static final Date toDate(float JD) {
        float Z = (normalizedJulian(JD)) + 0.5f;
        float W = (int) ((Z - 1867216.25f) / 36524.25f);
        float X = (int) (W / 4f);
        float A = Z + 1 + W - X;
        float B = A + 1524;
        float C = (int) ((B - 122.1) / 365.25);
        float D = (int) (365.25f * C);
        float E = (int) ((B - D) / 30.6001);
        float F = (int) (30.6001f * E);
        int day = (int) (B - D - F);
        int month = (int) (E - 1);

        if (month > 12) {
            month = month - 12;
        }

        int year = (int) (C - 4715);

        if (month > 2) {
            year--;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DATE, day);

        return c.getTime();
    }


    /**
     * 返回当月最后一天的日期
     */
    public static Date getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    public static int daysBetween(Date early, Date late) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);
        return daysBetween(c1, c2);
    }

    public static int daysBetween(Calendar early, Calendar late) {
        return (int) (toJulian(late) - toJulian(early));
    }

    public static float toJulian(Calendar c) {
        int Y = c.get(Calendar.YEAR);
        int M = c.get(Calendar.MONTH);
        int D = c.get(Calendar.DATE);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int) (365.25f * (Y + 4716));
        float F = (int) (30.6001f * (M + 1));
        return C + D + E + F - 1524.5f;
    }

    public static float toJulian(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return toJulian(c);
    }

    public static final String dateIncrease(String isoString, String fmt,
                                            int field, int amount) {

        try {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                    "GMT"));
            cal.setTime(stringToDate(isoString, fmt, true));
            cal.add(field, amount);

            return dateToString(cal.getTime(), fmt);

        } catch (Exception ex) {
            return null;
        }
    }

    public static final String roll(String isoString, String fmt, int field,
                                    boolean up) throws ParseException {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(stringToDate(isoString, fmt));
        cal.roll(field, up);

        return dateToString(cal.getTime(), fmt);
    }

    public static final String roll(String isoString, int field, boolean up) throws
            ParseException {

        return roll(isoString, DATETIME_PATTERN, field, up);
    }

    public static Date stringToDate(String dateText, String format,
                                    boolean lenient) {

        if (dateText == null) {

            return null;
        }

        DateFormat df = null;

        try {

            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }

            // setLenient avoids allowing dates  9/32/2001
            // which would otherwise parse to 10/2/2001
            df.setLenient(false);

            return df.parse(dateText);
        } catch (ParseException e) {

            return null;
        }
    }

    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(new Date().getTime());
    }


    public static Date stringToDate(String dateString, String format) {

        return stringToDate(dateString, format, LENIENT_DATE);
    }

    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
    }


    public static String dateToString(Date date, String pattern) {

        if (date == null) {

            return null;
        }

        try {

            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);

            return sfDate.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String dateToString(Date date) {
        return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
    }
    public static Date getCurrentDateTime() {
        Calendar calNow = Calendar.getInstance();
        return calNow.getTime();
    }

    public static String getCurrentDateString(String pattern) {
        return dateToString(getCurrentDateTime(), pattern);
    }

    public static String getCurrentDateString() {
        return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
    }

    public static String dateToStringWithTime() {

        return dateToString(new Date(), DATETIME_PATTERN);
    }

    public static String dateToStringWithTime(Date date) {

        return dateToString(date, DATETIME_PATTERN);
    }

    public static Date dateIncreaseByDay(Date date, int days) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static Date dateIncreaseByMonth(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.MONTH, mnt);

        return cal.getTime();
    }

    public static Date dateIncreaseByYear(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.YEAR, mnt);

        return cal.getTime();
    }


    public static String dateIncreaseByDay(String date, int days) {
        return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
    }

    public static String dateIncreaseByDay(String date, String fmt, int days) {
        return dateIncrease(date, fmt, Calendar.DATE, days);
    }

    public static String stringToString(String src, String srcfmt,
                                        String desfmt) {
        return dateToString(stringToDate(src, srcfmt), desfmt);
    }

    public static String getYear(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(
                "yyyy");
        return formater.format(date);
    }

    public static BigDecimal getCurrentTimeAsNumber() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        returnStr = f.format(date);
        return new BigDecimal(returnStr);
    }

    public static BigDecimal getCurrentDateAsNumber() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        returnStr = f.format(date);
        return new BigDecimal(returnStr);
    }

    public static String getMonth(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(
                "MM");
        return formater.format(date);
    }

    public static String getDay(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(
                "dd");
        return formater.format(date);
    }

    public static String getHour(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(
                "HH");
        return formater.format(date);
    }

    public static int getMinsFromDate(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return ((hour * 60) + min);
    }

    public static Date convertToDate(String str, boolean isExpiry) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            Calendar cal = Calendar.getInstance();
            if (isExpiry) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            dt = cal.getTime();
        }
        return dt;
    }

    public static Date getEndTime(Date endTime) {
        int dayMis = 1000 * 60 * 60 * 24;
        long resultMis = endTime.getTime() + dayMis - 1;
        return new Date(resultMis);

    }

    public static Date convertToDate(String str) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            dt = new Date();
        }
        return dt;
    }

    public static String dateFromat(Date date, int minute) {
        String dateFormat = null;
        int year = Integer.parseInt(getYear(date));
        int month = Integer.parseInt(getMonth(date));
        int day = Integer.parseInt(getDay(date));
        int hour = minute / 60;
        int min = minute % 60;
        dateFormat = String.valueOf(year)
                +
                (month > 9 ? String.valueOf(month) :
                        "0" + String.valueOf(month))
                +
                (day > 9 ? String.valueOf(day) : "0" + String.valueOf(day))
                + " "
                +
                (hour > 9 ? String.valueOf(hour) : "0" + String.valueOf(hour))
                +
                (min > 9 ? String.valueOf(min) : "0" + String.valueOf(min))
                + "00";
        return dateFormat;
    }

    public static String sDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN).format(Calendar.getInstance().getTime());
    }

    public static boolean checkStringIsDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        boolean result = true;
        if (dateStr == null || dateStr.length() == 0) {
            return false;
        }
        try {
            Date date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date getLastTimeInDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        Date tempDate = convertToDate(str);
        int dayMis = 1000 * 60 * 60 * 24;
        long curMillisecond = tempDate.getTime();
        long resultMis = curMillisecond + (dayMis - 1);
        return new Date(resultMis);
    }

    public static Date getFirstTimeInDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }
    public static int getMonthBetweenDate(Date startDate, Date endDate) {
        int result = 0;
        try {
            String date1 = startDate.toString();
            String date2 = endDate.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(sdf.parse(date1));
            c2.setTime(sdf.parse(date2));
            result = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR) * 12;
        } catch (Exception ex) {
            result = -1;
            System.out.println(ex.getMessage());
        }
        return result;
    }

    /**
     * 获取当前日期上一月第一天00:00:00时间
     * @return
     */
    public static Date getPreviousMonthFirstDay() {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        //获取当前日期上一个月月份
        calendar.add(Calendar.MONTH, -1);
        //设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.DAY_OF_MONTH,1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取计算结果当天的第一秒时间
        return DateUtil.convertToDate(sdf.format(getFirstTimeInDay(calendar.getTime())));
    }

    /**
     * 获取当前日期上一月最后一天23:59:59时间
     * @return
     */
    public static Date getPreviousMonthLastDayTime() {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,0);
        return getLastTimeInDay(calendar.getTime());
    }

    /**
     * 获取当前日期上一月最后一天日期
     * @return
     */
    public static int getPreviousMonthLastDay() {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,0);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期前一天开始时间00:00:00
     * @return
     */
    public static Date getPreviousDayFirstTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        return getFirstTimeInDay(calendar.getTime());
    }

    public static Date getNextDayFirstTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        return getFirstTimeInDay(calendar.getTime());
    }
    public static Date getPreviousDayLastTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        return getLastTimeInDay(calendar.getTime());
    }

    /**
     * 获取当前月第一天00:00:00
     * @return
     */
    public static Date getMonthFirstTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND,0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        return  calendar.getTime();
    }

    /**
     * 获取当前月最后一天23:59:59
     * @return
     */
    public static Date getMonthLastTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND,0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //将当前月加1；
        calendar.add(Calendar.MONTH, 1);
        //在当前月的下一月基础上减去1秒
        calendar.add(Calendar.SECOND, -1);
        //获得当前月最后一天
        return calendar.getTime();
    }

    /**
     * 获取当年的第一天
     * @return
     */
    public static Date getCurrYearFirst(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取当年的最后一天
     * @return
     */
    public static Date getCurrYearLast(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 判断当前是星期几
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
            String[] weekDays = { "sunday" ,"monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK)-1;
            if (w < 0){
                w = 0;
            }
            return weekDays[w];

    }

    /**实现给定某日期，判断是星期几 必须yyyy-MM-dd
     * @param date
     * @return
     */
    public static String getWeekday(String date){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Locale localeCN = Locale.SIMPLIFIED_CHINESE;
        SimpleDateFormat sdw = new SimpleDateFormat("E",localeCN);
        Date d = null;
        try {
            d = sd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdw.format(d);
    }
    /**
     * 判断时间是否在时间段内
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(String beginTime, String endTime) throws ParseException {
        //格式化日期
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Calendar date = Calendar.getInstance();
        date.setTime( df.parse(df.format(new Date())));
        Calendar begin = Calendar.getInstance();
        begin.setTime(df.parse(beginTime));

        Calendar end = Calendar.getInstance();
        end.setTime(df.parse(endTime));

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
    public static String dateToStringHHmmss(Date date) {

        return dateToString(date, DATETIME_HHMMSS);
    }
    /**
     * 获取某一时间段特定星期几的日期
     * @param dateFrom 开始时间
     * @param dateEnd 结束时间
     * @param weekDays 星期
     * @return 返回时间数组
     */
    public static String[] getDates(String dateFrom, String dateEnd, String weekDays) {
        long time = 1l;
        long perDayMilSec = 24 * 60 * 60 * 1000;
        List<String> dateList = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //需要查询的星期系数
        String strWeekNumber = weekForNum(weekDays);
        try {
            dateFrom = sdf.format(sdf.parse(dateFrom).getTime() - perDayMilSec);
            while (true) {
                time = sdf.parse(dateFrom).getTime();
                time = time + perDayMilSec;
                Date date = new Date(time);
                dateFrom = sdf.format(date);
                if (dateFrom.compareTo(dateEnd) <= 0) {
                    //查询的某一时间的星期系数
                    Integer weekDay = dayForWeek(date);
                    //判断当期日期的星期系数是否是需要查询的
                    if (strWeekNumber.contains(weekDay.toString())) {
                        dateList.add(dateFrom);
                    }
                } else {
                    break;
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        String[] dateArray = new String[dateList.size()];
        dateList.toArray(dateArray);
        return dateArray;
    }

    /**等到当期时间的周系数。星期日：1，星期一：2，星期二：3，星期三：4，星期四：5，星期五：6，星期六：7，星期日
     * @param date
     * @return
     */
    public static Integer dayForWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 得到对应星期的系数  星期日：1，星期一：2，星期二：3，星期三：4，星期四：5，星期五：6，星期六：7
     * @param weekDays 星期格式  星期一|星期二
     */
    public static String weekForNum(String weekDays){
        //返回结果为组合的星期系数
        String weekNumber = "";
        //解析传入的星期
        //多个星期数
        if(weekDays.contains("|")){
            String []strWeeks = weekDays.split("\\|");
            for(int i=0;i<strWeeks.length;i++){
                weekNumber = weekNumber + "" + getWeekNum(strWeeks[i]).toString();
            }
        }else{//一个星期数
            weekNumber = getWeekNum(weekDays).toString();
        }

        return weekNumber;

    }

    /**将星期转换为对应的系数  星期日：1，星期一：2，星期二：3，星期三：4，星期四：5，星期五：6，星期六：7,星期日
     * @param strWeek
     * @return
     */
    public static Integer getWeekNum(String strWeek){
        //默认为星期日
        Integer number = 1;
        if("星期日".equals(strWeek)){
            number = 1;
        }else if("星期一".equals(strWeek)){
            number = 2;
        }else if("星期二".equals(strWeek)){
            number = 3;
        }else if("星期三".equals(strWeek)){
            number = 4;
        }else if("星期四".equals(strWeek)){
            number = 5;
        }else if("星期五".equals(strWeek)){
            number = 6;
        }else if("星期六".equals(strWeek)){
            number = 7;
        }
        return number;
    }
    public static boolean sameDate(Date d1, Date d2) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(df.parse(DateUtil.dateToString(d1)).getTime() > df.parse(DateUtil.dateToString(d2)).getTime()){
           return true;
        }else {
            return false;
        }
    }
    public static boolean compareNewDate(Date date,String time) throws ParseException {
        SimpleDateFormat sdfd =new SimpleDateFormat("yyy-MM-dd ");
        String s = sdfd.format(new Date());
        int code = date.compareTo(sdfd.parse(s));
        if(code <= 0){
            return compareNewTime(time);
        }
        return true;

    }
    public static boolean compareNewTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        int code = df.format(new Date()).compareTo(time);
        return (code < 0);

    }
    /**
     * 计算年纪
     * @param birthDay
     * @return
     * @throws ParseException
     */
    public static int getAgeByBirth(Date birthDay) {
        try {
            int age = 0;
            Calendar cal = Calendar.getInstance();
            //出生日期晚于当前时间，无法计算
            if (cal.before(birthDay)) {
                throw new IllegalArgumentException(
                        "The birthDay is before Now.It's unbelievable!");
            }
            //当前年份
            int yearNow = cal.get(Calendar.YEAR);
            //当前月份
            int monthNow = cal.get(Calendar.MONTH);
            //当前日期
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(birthDay);
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            //计算整岁数
            age = yearNow - yearBirth;
            if (monthNow <= monthBirth) {
                if (monthNow == monthBirth) {
                    //当前日期在生日之前，年龄减一
                    if (dayOfMonthNow < dayOfMonthBirth) {age--;}
                } else {
                    //当前月份在生日之前，年龄减一
                    age--;
                }
            }
            if(age == 0){
                return  age + 1;
            }
            return  age;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 0;
        }
    }
    public static boolean dateCompareNowDate(String date) throws ParseException {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date d = sdf.parse(date);
        boolean flag = d.before(nowDate);
        if(flag){
            System.out.print("早于今天") ;
        }else{
            System.out.print("晚于今天") ;
        }
        return flag;
    }
    public static String timeSlot(int code,int number,Date date){
        StringBuilder stringBuilder = new StringBuilder();
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        switch (code){
            case 1:
                //减年
                ca.add(Calendar.YEAR, -number);
                break;
            case 2:
                //减月
                ca.add(Calendar.MONTH,-number);
                break;
            case 3:
                //减天
                ca.add(Calendar.DATE,-number);
                break;
            case 4:
                //加天
                ca.add(Calendar.DATE,+number);
                break;
            default:
        }
        stringBuilder.append(sdf.format(ca.getTime()));
        stringBuilder.append("~");
        stringBuilder.append(sdf.format(date));
        return stringBuilder.toString();
    }
    public static Integer BirthdayDays(String clidate) throws ParseException {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        // 存今天
        Calendar cToday = Calendar.getInstance();
        // 存生日
        Calendar cBirth = Calendar.getInstance();
        // 设置生日
        cBirth.setTime(myFormatter.parse(clidate));
        // 修改为本年
        cBirth.set(Calendar.YEAR, cToday.get(Calendar.YEAR));
        int days;
        if (cBirth.get(Calendar.DAY_OF_YEAR) < cToday.get(Calendar.DAY_OF_YEAR)) {
            // 生日已经过了，要算明年的了
            days = cToday.getActualMaximum(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
            days += cBirth.get(Calendar.DAY_OF_YEAR);
        } else {
            // 生日还没过
            days = cBirth.get(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
        }
        // 输出结果
        if (days == 0) {
            return 0;
        } else {
            return days;
        }
    }

    /**
     * 月份加减
     * @param date
     * @return
     * @throws ParseException
     */
    public static String  increaseDay(String date,Integer code) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = format.parse(date);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DAY_OF_MONTH, code);
        Date newDate = rightNow.getTime();
        return format.format(newDate);
    }

    public static Date getSubscribeTime(Long subscribeTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date(subscribeTime*1000L));
        return DateUtil.stringToDate(date,DATETIME_PATTERN);
    }
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }
    public static String getLocalDateTimeNow(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return formatter.format(localDateTime);
    }
    public static String timestamp2LocalDateTime(Long timeMillis) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = Instant.ofEpochMilli(timeMillis).atZone(zoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(localDateTime);
    }

    public static final DateTimeFormatter DFT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
