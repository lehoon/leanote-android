package org.houxg.leamonax.editor;


import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuillCallbackHandler {

    private static final String TAG = "QuillCallbackHandler";

    private OnJsEditorStateChangedListener mListener;
    private Gson mGson = new Gson();

    public QuillCallbackHandler(OnJsEditorStateChangedListener listener) {
        this.mListener = listener;
    }

    @JavascriptInterface
    public void onFormatChanged(String formats) {
        Log.i(TAG, "onFormatChanged(), formats=" + formats);
        if (mListener == null) {
            return;
        }
        Map<Editor.Style, Object> formatStatusMap = parseFormats(formats);
        mListener.onFormatChanged(formatStatusMap);
    }

    @JavascriptInterface
    public void onCursorChanged(int index, String formats) {
        Log.i(TAG, "onCursorChanged(), index=" + index + ", formats=" + formats);
        if (mListener == null) {
            return;
        }
        Map<Editor.Style, Object> formatStatusMap = parseFormats(formats);
        mListener.onCursorChanged(index, formatStatusMap);
    }

    @JavascriptInterface
    public void onHighlighted(int index, int length, String formats) {
        Log.i(TAG, "onHighlighted(), index=" + index + ", formats=" + formats);
        if (mListener == null) {
            return;
        }
        Map<Editor.Style, Object> formatStatusMap = parseFormats(formats);
        mListener.onCursorChanged(index, formatStatusMap);
    }

    @JavascriptInterface
    public void gotoLink(String title, String link) {
        if (mListener == null) {
            return;
        }
        mListener.gotoLink(title, link);
    }

    @NonNull
    private Map<Editor.Style, Object> parseFormats(String formats) {
        Map<String, Object> formatsMap = mGson.fromJson(formats, Map.class);
        Map<Editor.Style, Object> formatStatusMap = new HashMap<>();
        for (Map.Entry<String, Object> format : formatsMap.entrySet()) {
            switch (format.getKey()) {
                case "bold":
                    formatStatusMap.put(Editor.Style.BOLD, getBoolean((Boolean) format.getValue()));
                    break;
                case "list":
                    if ("bullet".equals(format.getValue())) {
                        formatStatusMap.put(Editor.Style.UNORDER_LIST, true);
                        formatStatusMap.put(Editor.Style.ORDER_LIST, false);
                    } else if ("ordered".equals(format.getValue())) {
                        formatStatusMap.put(Editor.Style.ORDER_LIST, true);
                        formatStatusMap.put(Editor.Style.UNORDER_LIST, false);
                    } else {
                        formatStatusMap.put(Editor.Style.UNORDER_LIST, false);
                        formatStatusMap.put(Editor.Style.ORDER_LIST, false);
                    }
                    break;
                case "italic":
                    formatStatusMap.put(Editor.Style.ITALIC, getBoolean((Boolean) format.getValue()));
                    break;
                case "blockquote":
                    formatStatusMap.put(Editor.Style.BLOCK_QUOTE, getBoolean((Boolean) format.getValue()));
                    break;
                case "header":
                    Double headerLevel = ((Double)format.getValue());
                    formatStatusMap.put(Editor.Style.HEADER, headerLevel != null && headerLevel > 0);
                    break;
                case "link":
                    Object linkValue = format.getValue();
                    if (!(linkValue instanceof String) && !(linkValue instanceof List)){
                        linkValue = null;
                    }
                    formatStatusMap.put(Editor.Style.LINK, linkValue);
                    break;
            }
        }
        return formatStatusMap;
    }

    private boolean getBoolean(Boolean value) {
        return value == null ? false : value;
    }
}
