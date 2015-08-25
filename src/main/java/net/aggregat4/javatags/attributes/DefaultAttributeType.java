package net.aggregat4.javatags.attributes;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

abstract class DefaultAttributeType<ValueType> implements AttributeType<ValueType> {
    private String name;
    private boolean globalAttribute;
    private Collection<String> tags;
//    private Class<ValueType> type;

    public DefaultAttributeType(String name) {
        this.name = Objects.requireNonNull(name);
        this.globalAttribute = true;
        this.tags = Collections.<String>emptyList();
    }

    public DefaultAttributeType(String name, Collection<String> tags) {
        this.name = Objects.requireNonNull(name);
        this.globalAttribute = false;
        this.tags = Objects.requireNonNull(tags);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isGlobalAttribute() {
        return globalAttribute;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultAttributeType<?> that = (DefaultAttributeType<?>) o;

        if (globalAttribute != that.globalAttribute) return false;
        if (!name.equals(that.name)) return false;
        return !(tags != null ? !tags.equals(that.tags) : that.tags != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (globalAttribute ? 1 : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultAttributeType{" +
            "name='" + name + '\'' +
            '}';
    }
}
