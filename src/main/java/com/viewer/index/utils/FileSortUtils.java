package com.viewer.index.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileSortUtils {

    public static void sort(File[] files){
        Arrays.sort(files, new FileCompare());
    }

}
class FileCompare implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
        String[] names1 = o1.getName().split("\\\\");
        String[] names2 = o2.getName().split("\\\\");
        String[] split1 = names1[names1.length-1].split("\\.");
        String[] split2 = names2[names2.length-1].split("\\.");
        try {
            int f1 = Integer.parseInt(split1[0]);
            int f2 = Integer.parseInt(split2[0]);
            return f1-f2;
        }catch (Exception e){
            System.out.println(split1[0] + "," + split2[0]);
        }
        return 0;
    }
}