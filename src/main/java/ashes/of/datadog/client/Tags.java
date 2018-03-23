package ashes.of.datadog.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Tags implements Taggable<Tags> {

    private final List<Supplier<String>> tags = new ArrayList<>();

    @Override
    public Tags tag(String tag, Supplier<Object> sup) {
        tags.add(() -> {
            Object o = sup.get();
            return o == null ? tag : tag + ':' + o;
        });
        return this;
    }


    public Stream<String> stream() {
        return tags.stream()
                .map(Supplier::get);
    }


    public Tags copy() {
        Tags copy = new Tags();
        copy.tags.addAll(this.tags);

        return copy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tags)) return false;

        Tags t = (Tags) o;

        return tags.equals(t.tags);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }


    @Override
    public String toString() {
        return stream().collect(Collectors.joining(", ", "[", "]"));
    }
}