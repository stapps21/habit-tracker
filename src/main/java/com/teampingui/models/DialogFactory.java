package com.teampingui.models;

import javafx.scene.Parent;

public class DialogFactory {

    public static ErrorDialog createDialog(DialogType type, Parent parent) {
        return switch (type) {
            case ERROR -> new ErrorDialog(parent, "#e3675b", "#ec4333");
            case WARNING -> new ErrorDialog(parent, "#ff6600", "#ce5200");
            case INFO -> new ErrorDialog(parent, "#e2b222", "#9c790f");
            case SUCCESS -> new ErrorDialog(parent, "#18be18", "#0d760d");
        };
    }

}
