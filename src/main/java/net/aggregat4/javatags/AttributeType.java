package net.aggregat4.javatags;

public enum AttributeType {
    id("id", String.class, false),
    classAttr("class", String.class, false),
    style("style", String.class, false),
    titleAttr("title", String.class, false),
    lang("lang", String.class, false),
    translate("translate", Boolean.class, false),
    dir("dir", String.class, false),
    // dataset("dataset", String.class, false); // not sure how to model this, it is of type DOMStringMap
    hidden("hidden", Boolean.class, false),
    tabIndex("tabIndex", Long.class, false),
    accessKey("accessKey", String.class, false),
    accessKeyLabel("accessKeyLabel", String.class, false),
    contentEditable("contentEditable", String.class, false),
    isContentEditable("isContentEditable", Boolean.class, false),
    spellcheck("spellcheck", Boolean.class, false),
    ;

    private String name;
    private Class klass;
    private boolean nullable;

    AttributeType(String name, Class klass, boolean nullable) {
        this.name = name;
        this.klass = klass;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public Class getDataType() {
        return klass;
    }

    public boolean isNullable() {
        return nullable;
    }
}
