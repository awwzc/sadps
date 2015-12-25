package com.sk.deploy.support;

public enum LogLevel {

    INFO(null, 0), WARN("<span style=\"color:yellow;\">%s</span>", 1), ERROR("<span style=\"color:red;\">%s</span>", 2);

    private LogLevel(String style, int code) {
        this.style = style;
        this.code = code;
    }

    private String style;
    private int code;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}