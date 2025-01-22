package com.doittogether.platform.application.global.exception.fcm;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.GlobalException;

public class FcmException extends GlobalException {
    public FcmException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
