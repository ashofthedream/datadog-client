package ashes.of.datadog.client;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public interface Taggable<B extends Taggable<B>> {

    /**
     * Adds tags to list
     *
     * @param tags tags
     * @return builder
     */
    default B tags(String... tags) {
        for (String tag : tags)
            tag(tag);

        return (B) this;
    }

    /**
     * Adds tags to list
     *
     * @param tags tags
     * @return builder
     */
    default B tags(Iterable<String> tags) {
        for (String tag : tags)
            tag(tag);

        return (B) this;
    }


    /**
     * Adds simple tag
     *
     * @param tag tag name
     * @return builder
     */
    default B tag(String tag) {
        return tag(tag, () -> null);
    }

    /**
     * Adds tag with subtag
     *
     * @param tag tag name
     * @param sub subtag data
     * @return builder
     */
    default B tag(String tag, @Nullable Object sub) {
        return tag(sub != null ? tag  + ':' + sub : tag);
    }

    /**
     * Adds tag with subtag
     *
     * @param tag tag name
     * @param sup subtag supplier
     * @return builder
     */
    B tag(String tag, Supplier<Object> sup);


    /**
     * @return stream of built to string tags
     */
    Stream<String> stream();

    /**
     * @return array of built to string tags
     */
    default String[] tags() {
        return stream()
                .toArray(String[]::new);
    }
}
