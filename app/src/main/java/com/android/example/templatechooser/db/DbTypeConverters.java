
package com.android.example.templatechooser.db;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.util.StringUtil;

import com.android.example.templatechooser.vo.Design;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DbTypeConverters {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Integer> stringToIntList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return StringUtil.splitToIntList(data);
    }

    @TypeConverter
    public static String intListToString(List<Integer> ints) {
        return StringUtil.joinIntoString(ints);
    }

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(data.split(","));
    }

    @TypeConverter
    public static String stringListToString(List<String> strings) {
        StringBuilder string = new StringBuilder();
        for(String s : strings) {
            string.append(s).append(",");
        }
        return string.toString();
    }

    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    @TypeConverter
    public static List<Design.Variation> variationStringToVariationList(String variations) {
        if (variations == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Design.Variation>>() {}.getType();
        return gson.fromJson(variations, listType);
    }

    @TypeConverter
    public static String variationListToVariationString(List<Design.Variation> variations) {
        return gson.toJson(variations);
    }
}
