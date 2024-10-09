package com.snapgames.demo.entity;

import java.awt.*;

public class TextObject extends Entity<TextObject> {
    private String text = "";
    private Font font;

    public TextObject(String name) {
        super(name);
    }

    public TextObject setText(String t) {
        this.text = t;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public TextObject setFont(Font f) {
        this.font = f;
        return this;
    }

    public Font getFont() {
        return this.font;
    }

}
