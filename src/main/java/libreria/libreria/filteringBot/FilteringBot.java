package libreria.libreria.filteringBot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilteringBot implements BadWords, AddRemove {
    private static final Set<String> set = new HashSet<>(List.of(badWords));

    public static boolean check(String text) {
        return set.stream()
                .anyMatch(text::contains);
    }

    public static boolean ignoreBlankCheckBadWord(String text) {
        String cpText = text.replace(" ", "");
        return check(cpText);
    }

    @Override
    public void add(String...texts) {
        set.addAll(List.of(texts));
    }

    @Override
    public void add(List<String> texts) {
        set.addAll(texts);
    }

    @Override
    public void add(Set<String> texts) {
        set.addAll(texts);
    }

    @Override
    public void remove(String...texts) {
        List.of(texts).forEach(set::remove);
    }

    @Override
    public void remove(List<String> texts) {
        texts.forEach(set::remove);
    }

    @Override
    public void remove(Set<String> texts) {
        texts.forEach(set::remove);
    }
}
