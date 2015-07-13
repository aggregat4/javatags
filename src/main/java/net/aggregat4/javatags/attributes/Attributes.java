package net.aggregat4.javatags.attributes;

import java.util.Optional;

/**
 * All possible HTML5 attribute type definitions (generated from the spec).
 */
public final class Attributes {
    private Attributes() {}

    public static final AttributeType<String>
        // Global attributes
        id = strAttr("id"),
        classAttr = strAttr("class"),
        style = strAttr("style"),
        titleAttr = strAttr("title"),
        accessKeyLabel = strAttr("accessKeyLabel"),
        // Generated attributes
        accept = strAttr("accept"),
        acceptCharset = strAttr("acceptCharset"),
        accessKey = strAttr("accessKey"),
        action = strAttr("action"),
        align = strAttr("align"),
        alt = strAttr("alt"),
        archive = strAttr("archive"),
        autocomplete = strAttr("autocomplete"),
        behavior = strAttr("behavior"),
        bgColor = strAttr("bgColor"),
        border = strAttr("border"),
        challenge = strAttr("challenge"),
        charset = strAttr("charset"),
        cite = strAttr("cite"),
        code = strAttr("code"),
        codeBase = strAttr("codeBase"),
        colSpan = strAttr("colSpan"),
        cols = strAttr("cols"),
        content = strAttr("content"),
        contentEditable = strAttr("contentEditable"),
        coords = strAttr("coords"),
        crossOrigin = strAttr("crossOrigin"),
        currentTime = strAttr("currentTime"),
        data = strAttr("data"),
        dateTime = strAttr("dateTime"),
        defaultPlaybackRate = strAttr("defaultPlaybackRate"),
        defaultValue = strAttr("defaultValue"),
        dir = strAttr("dir"),
        dirName = strAttr("dirName"),
        direction = strAttr("direction"),
        download = strAttr("download"),
        encoding = strAttr("encoding"),
        enctype = strAttr("enctype"),
        face = strAttr("face"),
        formAction = strAttr("formAction"),
        formEnctype = strAttr("formEnctype"),
        formMethod = strAttr("formMethod"),
        formTarget = strAttr("formTarget"),
        frameBorder = strAttr("frameBorder"),
        height = strAttr("height"),
        high = strAttr("high"),
        href = strAttr("href"),
        hreflang = strAttr("hreflang"),
        hspace = strAttr("hspace"),
        htmlFor = strAttr("htmlFor"),
        httpEquiv = strAttr("httpEquiv"),
        keytype = strAttr("keytype"),
        kind = strAttr("kind"),
        label = strAttr("label"),
        lang = strAttr("lang"),
        length = strAttr("length"),
        longDesc = strAttr("longDesc"),
        loopAttrString = strAttr("loop"), // the only attribute that collides with the booleans
        low = strAttr("low"),
        max = strAttr("max"),
        maxLength = strAttr("maxLength"),
        media = strAttr("media"),
        mediaGroup = strAttr("mediaGroup"),
        method = strAttr("method"),
        min = strAttr("min"),
        minLength = strAttr("minLength"),
        name = strAttr("name"),
        optimum = strAttr("optimum"),
        pattern = strAttr("pattern"),
        placeholder = strAttr("placeholder"),
        playbackRate = strAttr("playbackRate"),
        preload = strAttr("preload"),
        rel = strAttr("rel"),
        rev = strAttr("rev"),
        rowSpan = strAttr("rowSpan"),
        rows = strAttr("rows"),
        scrollAmount = strAttr("scrollAmount"),
        scrollDelay = strAttr("scrollDelay"),
        scrolling = strAttr("scrolling"),
        selectedIndex = strAttr("selectedIndex"),
        selectionDirection = strAttr("selectionDirection"),
        selectionEnd = strAttr("selectionEnd"),
        selectionStart = strAttr("selectionStart"),
        shape = strAttr("shape"),
        size = strAttr("size"),
        span = strAttr("span"),
        src = strAttr("src"),
        srcdoc = strAttr("srcdoc"),
        srclang = strAttr("srclang"),
        start = strAttr("start"),
        step = strAttr("step"),
        tabIndex = strAttr("tabIndex"),
        target = strAttr("target"),
        text = strAttr("text"),
        title = strAttr("title"),
        type = strAttr("type"),
        useMap = strAttr("useMap"),
        value = strAttr("value"),
        valueAsNumber = strAttr("valueAsNumber"),
        volume = strAttr("volume"),
        vspace = strAttr("vspace"),
        width = strAttr("width"),
        wrap = strAttr("wrap");

    public static final AttributeType<Boolean>
        // Global attributes
        isContentEditable = boolAttr("isContentEditable"),
        // Generated attributes
        async = boolAttr("async"),
        autofocus = boolAttr("autofocus"),
        autoplay = boolAttr("autoplay"),
        checked = boolAttr("checked"),
        compact = boolAttr("compact"),
        controls = boolAttr("controls"),
        defaultAttr = boolAttr("default"),
        defaultChecked = boolAttr("defaultChecked"),
        defaultMuted = boolAttr("defaultMuted"),
        defaultSelected = boolAttr("defaultSelected"),
        defer = boolAttr("defer"),
        disabled = boolAttr("disabled"),
        formNoValidate = boolAttr("formNoValidate"),
        hidden = boolAttr("hidden"),
        indeterminate = boolAttr("indeterminate"),
        isMap = boolAttr("isMap"),
        loopAttrBool = boolAttr("loop"),
        multiple = boolAttr("multiple"),
        muted = boolAttr("muted"),
        noResize = boolAttr("noResize"),
        noValidate = boolAttr("noValidate"),
        readOnly = boolAttr("readOnly"),
        required = boolAttr("required"),
        reversed = boolAttr("reversed"),
        selected = boolAttr("selected"),
        spellcheck = boolAttr("spellcheck"),
        translate = boolAttr("translate"),
        trueSpeed = boolAttr("trueSpeed"),
        typeMustMatch = boolAttr("typeMustMatch");

    // Helper methods to easily construct attributes without the generics noise
    private static AttributeType<String> strAttr(String name, String... allowedValues) {
        return new StringAttributeType<>(name, Optional.ofNullable(allowedValues.length == 0 ? null : allowedValues));
    }

    private static AttributeType<Boolean> boolAttr(String name, Boolean... allowedValues) {
        return new BooleanAttributeType<>(name, Optional.ofNullable(allowedValues.length == 0 ? null : allowedValues));
    }

    // helper for vararg construction
    public static Attribute[] attrs(Attribute... attributes) {
        return attributes;
    }

    public static Attribute[] arr(Attribute... attributes) {
        return attributes;
    }

    public static <T> Attribute<AttributeType<T>, T> attr(AttributeType<T> type, T value) {
        return new Attribute<>(type, value);
    }

    public static <T> Attribute<AttributeType<T>, T> a(AttributeType<T> type, T value) {
        return new Attribute<>(type, value);
    }

    // TODO temporary test helper, if this is the api I prefer I will need about a hundred more of these
    public static Attribute<AttributeType<String>, String> id(final String value) {
        return new Attribute<>(id, value);
    }
}
