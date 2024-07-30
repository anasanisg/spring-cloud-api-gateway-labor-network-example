package com.tooltracker.backend.walltools.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;

public class CollectionUtils {

    /*
     * This functions used to compare between two list & find not existing elements
     */
    public static List<Long> getNotExistingElements(List<Long> full, List<Long> toCompareWith) {
        return full.stream()
                .filter(number -> !toCompareWith.contains(number))
                .collect(Collectors.toList());
    }

    /*
     * Used to extract ids from list of objects
     */
    public static <T, R> List<R> extractIds(List<T> list, Function<? super T, ? extends R> idExtractor) {
        return list.stream()
                .map(idExtractor)
                .collect(Collectors.toList());
    }

}
