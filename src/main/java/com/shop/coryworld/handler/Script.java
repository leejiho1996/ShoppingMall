package com.shop.coryworld.handler;

public class Script {
    public static String back(String message) {
        StringBuilder sb = new StringBuilder(message);
        sb.append("<script>");
        sb.append("alert('" + message + "');");
        sb.append("history.back();");
        sb.append("</script>");
        return sb.toString();
    }
}
