package cn.schoolwow.wolongsi.util;

public class WoLongSiUtil {
    /**
     * 格式化秒数
     * */
    public static String formatSeconds(long seconds){
        String duration = null;
        if(seconds<60){
            duration = seconds+"秒";
        }else if(seconds<3600){
            duration = seconds/60+"分"+seconds%60+"秒";
        }else if(seconds<86400){
            duration = seconds/3600+"小时"+seconds%3600/60+"分"+seconds%60+"秒";
        }else{
            duration = seconds/86400+"天"+seconds%86400/3600+"小时"+seconds%3600/60+"分"+seconds%60+"秒";
        }
        return duration;
    }
}
