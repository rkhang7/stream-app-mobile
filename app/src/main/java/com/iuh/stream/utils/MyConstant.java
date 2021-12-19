package com.iuh.stream.utils;

public class MyConstant {
//    public static final String BASE_URL = "http://192.168.100.3:3000/";
//    public static final String BASE_URL = "http://10.0.2.2:3000/";
//    http://ec2-3-0-93-24.ap-southeast-1.compute.amazonaws.com:3000/
    public static final String BASE_URL = "http://ec2-3-0-93-24.ap-southeast-1.compute.amazonaws.com:3000/";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String PASSWORD = "password";
    public static final String TAG = "STREAM_APP";
    public static final String TEXT_TYPE = "text";
    public static final String IMAGE_TYPE = "image";
    public static final String FILE_TYPE = "file";
    public static final String USER_KEY = "USER";
    public static final String CONTENT_KEY = "content";
    public static final String GROUP_KEY = "group";
    public static final String NUMBER_MEMBER = "size";
    public static final String GROUP_CHAT_ID = "group_id";
    public static final String CHAT_ID = "chat_id";
    public static final String LIST_MEMBER_KEY = "list_member";



    // gửi lời mời kết bạn
    public static final String ADD_FRIEND_REQUEST = "add-friend";
    public static final String ADD_FRIEND_RESPONSE = "add-friend-res";

    // đồng ý kết bạn
    public static final String ACCEPT_FRIEND_REQUEST = "accept-friend";
    public static final String ACCEPT_FRIEND_RESPONSE = "accept-friend-res";

    // xóa bạn
    public static final String CANCEL_FRIEND_REQUEST = "cancel-friend";
    public static final String CANCEL_FRIEND_RESPONSE = "cancel-friend-res";

    // hủy lời mời kết bạn khi  ta gửi cho mình
    public static final String CANCEL_FRIEND_REQUEST_REQUEST = "cancel-friend-req";
    public static final String CANCEL_FRIEND_REQUEST_RESPONSE = "cancel-friend-req-res";

    // hủy lời mời kết bạn khi mình gửi cho người ta
    public static final String CANCEL_FRIEND_INV_REQUEST = "cancel-friend-inv";
    public static final String CANCEL_FRIEND_INV_RESPONSE = "cancel-friend-inv-res";

    public static final String RENDER_FILE_REQUEST = "render-file";
    public static final String RENDER_FILE_RESPONSE = "render-file-res";

    public static final String RENDER_IMAGE_REQUEST = "render-image";
    public static final String RENDER_IMAGE_RESPONSE = "render-image-res";

    public static final String PRIVATE_MESSAGE = "private-message";
    public static final String MESSAGE_SENT = "message-sent";

    public static final String READ_MESSAGE = "read-message";
    public static final String RECEIVE_MESSAGE = "receive-message";
    public static final String CREATE_GROUP = "group-created";
    public static final String  GROUP_NOTIFICATION = "group-notification";
    public static final String RECALL_LINE = "recall-line";
}
