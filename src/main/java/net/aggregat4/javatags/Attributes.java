package net.aggregat4.javatags;

import java.io.IOException;
import java.util.Objects;

public class Attributes {

  public interface AttributeType<ValueType> {
    boolean isNullable();
    String getName();

    AttributeType<String>
        id = strAttr("id"),
        classAttr = strAttr("class"),
        style = strAttr("style"),
        titleAttr = strAttr("title"),
        lang = strAttr("lang"),
        translate = strAttr("translate", "", "yes", "no"), // TODO runtime check for allowedValues
        dir = strAttr("dir"),
        hidden = strAttr("hidden"),
        accessKey = strAttr("accessKey"),
        accessKeyLabel = strAttr("accessKeyLabel"),
        contentEditable = strAttr("contentEditable"),
        spellcheck = strAttr("spellcheck")
    ;

    AttributeType<Boolean>
      isContentEditable = boolAttr("isContentEditable")
    ;

    AttributeType<Long>
        tabIndex = longAttr("tabIndex")
    ;
  }

  // Helper methods to easily construct attributes without the generics noise
  private static AttributeType<String> strAttr(String name, String ... allowedValues) {
    if (allowedValues != null) {
      return new DefaultAttributeType<String>(false, name, allowedValues);
    } else {
      return new DefaultAttributeType<String>(false, name);
    }
  }
  private static AttributeType<String> strAttrNullable(String name, String ... allowedValues) {
    if (allowedValues != null) {
      return new DefaultAttributeType<String>(true, name, allowedValues);
    } else {
      return new DefaultAttributeType<String>(true, name);
    }
  }
  private static AttributeType<Boolean> boolAttr(String name) {
    return new DefaultAttributeType<Boolean>(false, name);
  }
  private static AttributeType<Boolean> boolAttrNullable(String name) {
    return new DefaultAttributeType<Boolean>(true, name);
  }
  private static AttributeType<Long> longAttr(String name) {
    return new DefaultAttributeType<Long>(false, name);
  }
  private static AttributeType<Long> longAttrNullable(String name) {
    return new DefaultAttributeType<Long>(true, name);
  }

  private static class DefaultAttributeType<ValueType> implements AttributeType<ValueType> {
    private boolean nullable;
    private String name;
    private ValueType [] allowedValues;

    public DefaultAttributeType(boolean nullable, String name) {
      this.nullable = nullable;
      this.name = Objects.requireNonNull(name);
    }

    public DefaultAttributeType(boolean nullable, String name, ValueType[] allowedValues) {
      this(nullable, name);
      this.allowedValues = Objects.requireNonNull(allowedValues);
    }

    public boolean isNullable() {
      return nullable;
    }

    public String getName() {
      return name;
    }
  }

  public static class Attribute<AT extends AttributeType<ValueType>, ValueType> {
    private AT type;
    private ValueType value;

    public Attribute(AT type, ValueType value) {
      this.type = Objects.requireNonNull(type);
      if (! type.isNullable()) {
        Objects.requireNonNull(
            value,
            String.format("Value can not be null since the attribute '%s' is non-nullable",
                type.getName()));
      }
      this.value = value;
    }

    public AT getType() {
      return type;
    }

    public ValueType getValue() {
      return value;
    }

    public void render(Appendable appendable) throws IOException {
      appendable.append(" ");
      appendable.append(type.getName());
      if (value != null) {
        appendable.append("=");
        appendable.append("\"");
        appendable.append(value.toString());
        appendable.append("\"");
      }
    }
  }

  // helper for vararg construction
  public static Attribute[] attrs(Attribute... attributes) { return attributes; }

  public static <T> Attribute<AttributeType<T>, T> attr(AttributeType<T> type, T value) {
    return new Attribute<AttributeType<T>, T>(type, value);
  }

  public static Attribute<AttributeType<String>, String> id(final String value) {
    return new Attribute<AttributeType<String>, String>(AttributeType.id, value);
  }

}
