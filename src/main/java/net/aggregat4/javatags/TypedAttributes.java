package net.aggregat4.javatags;

import java.io.IOException;
import java.util.Objects;

public class TypedAttributes {

  public interface TypedAttributeType<ValueType> {
    boolean isNullable();
    String getName();

    TypedAttributeType<String>
        id = str("id"),
        classAttr = str("class"),
        style = str("style"),
        titleAttr = str("title"),
        lang = str("lang"),
        translate = str("translate"),
        dir = str("dir"),
        hidden = str("hidden"),
        tabIndex = str("tabIndex"),
        accessKey = str("accessKey"),
        accessKeyLabel = str("accessKeyLabel"),
        contentEditable = str("contentEditable"),
        isContentEditable = str("isContentEditable"),
        spellcheck = str("spellcheck")
    ;
  }

  // Helper methods to easily construct attributes without the generics noise
  private static TypedAttributeType<String> str(String name) {
    return new DefaultTypedAttributeType<String>(false, name);
  }

  private static TypedAttributeType<String> strNullable(String name) {
    return new DefaultTypedAttributeType<String>(true, name);
  }

  private static class DefaultTypedAttributeType<ValueType> implements TypedAttributeType<ValueType> {
    private boolean nullable;
    private String name;

    public DefaultTypedAttributeType(boolean nullable, String name) {
      this.nullable = nullable;
      this.name = Objects.requireNonNull(name);
    }

    public boolean isNullable() {
      return nullable;
    }

    public String getName() {
      return name;
    }
  }

  public static class TypedAttribute<AT extends TypedAttributeType<ValueType>, ValueType> {
    private AT type;
    private ValueType value;

    public TypedAttribute(AT type, ValueType value) {
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
  public static TypedAttribute [] attrs(TypedAttribute... attributes) { return attributes; }

  public static <T> TypedAttribute<TypedAttributeType<T>, T> attr(TypedAttributeType<T> type, T value) {
    return new TypedAttribute<TypedAttributeType<T>, T>(type, value);
  }

  public static TypedAttribute<TypedAttributeType<String>, String> id(final String value) {
    return new TypedAttribute<TypedAttributeType<String>, String>(TypedAttributeType.id, value);
  }

}
