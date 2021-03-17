package com.summer.common.util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * 对象转换
 * 根据传入字符串以及类型获取反馈的对象
 */
public class ObjectUtil {
    public static Object convert(Class<?> targetType, String s) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(s);
        return editor.getValue();
    }
}
