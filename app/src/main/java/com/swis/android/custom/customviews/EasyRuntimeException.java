package com.swis.android.custom.customviews;

/**
 * Created by Nitesh Singh(killer) on 9/1/2016.
 */
public class EasyRuntimeException extends RuntimeException {

    public EasyRuntimeException() {
    }

    public EasyRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public EasyRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EasyRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
