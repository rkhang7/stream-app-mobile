package com.iuh.stream.models.responce;

public class UpdateUserResponse {
    private int code;
    private String message;

    public UpdateUserResponse() {
    }

    public UpdateUserResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UpdateUserResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
