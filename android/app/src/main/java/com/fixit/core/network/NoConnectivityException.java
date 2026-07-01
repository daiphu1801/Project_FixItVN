package com.fixit.core.network;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "Yêu cầu kết nối mạng để thực hiện chức năng này";
    }
}
