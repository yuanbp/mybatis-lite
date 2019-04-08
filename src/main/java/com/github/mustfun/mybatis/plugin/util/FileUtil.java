package org.mybatis.gen.utils;

import java.io.File;

/**
 * @description FileUtil
 * @author chieftain
 * @create 2019-01-31 21:30
 */
public class FileUtil {

    public static final String Unix_PATH_SEPARATOR = "/";
    public static final String Wins_PATH_ROOT = ":";

    public static boolean isAbsolutePath(String path)
    {
        if ((path.startsWith("/")) || (path.indexOf(":") > 0)) {
            return true;
        }
        return false;
    }

    public static boolean exist(String path)
    {
        File file = new File(path);
        if (file.isDirectory()) {
            return true;
        }
        return false;
    }

}
