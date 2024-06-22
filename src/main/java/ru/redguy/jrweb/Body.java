package ru.redguy.jrweb;

public abstract class Body {
    protected Context context;
    public Body(Context context) {
        this.context = context;
    }

    protected abstract void parse();
}