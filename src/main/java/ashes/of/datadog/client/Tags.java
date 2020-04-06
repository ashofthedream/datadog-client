package ashes.of.datadog.client;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class Tags implements Taggable<Tags> {

    private final List<Supplier<String>> list = new ArrayList<>();

    @Override
    public Tags tag(String tag, Supplier<Object> sup) {
        list.add(() -> {
            Object o = sup.get();
            return o == null ? tag : tag + ':' + o;
        });

        return this;
    }

    @Override
    public Tags tags() {
        return this;
    }

    public List<Supplier<String>> list() {
        return list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tags)) return false;

        Tags t = (Tags) o;

        return list.equals(t.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }


    @Override
    public String toString() {
        return list.stream()
                .map(Supplier::get)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
