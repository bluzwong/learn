package com.github.bluzwong.learn_rx.touch;

/**
 * Created by wangzhijie on 2015/11/9.
 */
public class Util {
    public static final int ACTION_DOWN             = 0;
    public static final int ACTION_UP               = 1;
    public static final int ACTION_MOVE             = 2;
    public static final int ACTION_CANCEL           = 3;

    public static String actionToString(int code) {
        switch (code) {
            case ACTION_DOWN: {
                return "ACTION_DOWN";
            }
            case ACTION_UP: {
                return "ACTION_UP";
            }
            case ACTION_MOVE: {
                return "ACTION_MOVE";
            }
            case ACTION_CANCEL: {
                return "ACTION_CANCEL";
            }
        }
        return "unknown";
    }
}
