package com.ldb.android.example.wrappedaidl.api;

/**
 * Created by lsp on 2016/9/2.
 */
public class MessageApi {
    public static final int MSG_STORE_TEXT = 0;
    public static final int MSG_STORE_CALLBACK = 1;
    public static final int MSG_GET_DATA = 2;
    public static final int MSG_GET_DATA_CALLBACK = 3;

    public static final String ARG_STORE_TEXT = "store_text";
    public static final String ARG_STORE_CALLBACK_RESULT = "store_callback_result";
    public static final String ARG_STORE_CALLBACK_INFO = "store_callback_info";

    public static final String ARG_GET_DATA_SINCE = "get_data_since";
    public static final String ARG_GET_DATA_SIZE = "get_data_size";
    public static final String ARG_GET_DATA_RESULT = "get_data_result";
    public static final String ARG_GET_DATA_RESULT_COUNT = "get_data_result_count";

}
