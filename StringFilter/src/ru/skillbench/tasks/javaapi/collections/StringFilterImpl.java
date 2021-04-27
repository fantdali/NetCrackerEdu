package ru.skillbench.tasks.javaapi.collections;

import javax.swing.text.html.HTMLDocument;
import java.util.*;

public class StringFilterImpl implements StringFilter{
    private final Set<String> strings = new HashSet<>();

    private interface Filter{
        boolean filter(String s, String chars);
    }
    /**
     * Добавляет строку s в набор, приводя ее к нижнему регистру.
     * Если строка s уже есть в наборе, ничего не делает.
     *
     * @param s может быть null
     */
    @Override
    public void add(String s) {
        if (s != null) {
            strings.add(s.toLowerCase(Locale.ROOT));
        } else {
            strings.add(null);
        }
    }

    /**
     * Удаляет строку s из набора (предварительно приведя ее к нижнему регистру).
     *
     * @param s может быть null
     * @return true если строка была удалена, false если строка отсутствовала в наборе.
     */
    @Override
    public boolean remove(String s) {
        if (s != null) {
            return strings.remove(s.toLowerCase(Locale.ROOT));
        } else {
            return strings.remove(null);
        }
    }

    /**
     * Очищает набор - удаляет из него все строки
     */
    @Override
    public void removeAll() {
        strings.clear();
    }

    /**
     * Возвращает набор (коллекцию), в котором хранятся строки.
     * В наборе не может быть двух одинаковых строк, однако может быть null.
     */
    @Override
    public Collection<String> getCollection() {
        return strings;
    }

    private Iterator<String> filterStrings(Filter f, String condition) {
        Set<String> filteredStrings = new HashSet<>();
        for (String s : strings) {
            if (s != null && f.filter(s, condition)) {
                filteredStrings.add(s);
            }
        }
        return filteredStrings.iterator();
    }

    /**
     * Ищет и возвращает все строки, содержащие указанную последовательность символов.<br/>
     * Если <code>chars</code> - пустая строка или <code>null</code>,
     * то результат содержит все строки данного набора.<br/>
     *
     * @param chars символы, входящие в искомые строки
     *              (все символы, являющиеся буквами, - в нижнем регистре)
     * @return строки, содержащие указанную последовательность символов
     */
    @Override
    public Iterator<String> getStringsContaining(String chars) {
        Filter f = new Filter() {
            @Override
            public boolean filter(String s, String chars) {
                return chars == null || s.contains(chars);
            }
        };
        return filterStrings(f, chars);
    }

    /**
     * Ищет и возвращает строки, начинающиеся с указанной последовательности символов,
     * (без учета регистра). <br/>
     * Если <code>begin</code> - пустая строка или <code>null</code>,
     * то результат содержит все строки данного набора.<br/>
     *
     * @param begin первые символы искомых строк
     *              (для сравнения со строками набора символы нужно привести к нижнему регистру)
     * @return строки, начинающиеся с указанной последовательности символов
     */
    @Override
    public Iterator<String> getStringsStartingWith(String begin) {
        Filter f = new Filter() {
            @Override
            public boolean filter(String s, String begin) {
                return begin == null || s.startsWith(begin.toLowerCase(Locale.ROOT));
            }
        };
        return filterStrings(f, begin);
    }

    private boolean formatMatching(String s, String f) {
        if (f.isEmpty()) {
            return true;
        }
        if (s.length() != f.length()) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (f.charAt(i) == '#') {
                if (!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            } else if (s.charAt(i) != f.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ищет и возвращает все строки, представляющие собой число в заданном формате.<br/>
     * Формат может содержать символ # (место для одной цифры от 0 до 9) и любые символы.
     * Примеры форматов: "(###)###-####" (телефон), "# ###" (целое число от 1000 до 9999),
     * "-#.##" (отрицательное число, большее -10, с ровно двумя знаками после десятичной точки).<br/>
     * Упрощающее ограничение: в строке, удовлетворяющей формату, должно быть ровно столько символов,
     * сколько в формате (в отличие от стандартного понимания числового формата,
     * где некоторые цифры на месте # не являются обязательными).<br/>
     * Примечание: в данной постановке задачи НЕ предполагается использование регулярных выражений
     * или какого-либо высокоуровневого API (эти темы изучаются позже).<br/>
     * Если <code>format</code> - пустая строка или <code>null</code>,
     * то результат содержит все строки данного набора.<br/>
     *
     * @param format формат числа
     * @return строки, удовлетворяющие заданному числовому формату
     */
    @Override
    public Iterator<String> getStringsByNumberFormat(String format) {
        Filter f = new Filter() {
            @Override
            public boolean filter(String s, String format) {
                return format == null || formatMatching(s, format);
            }
        };
        return filterStrings(f, format);
    }

    /**
     * Ищет и возвращает строки, удовлетворяющие заданному шаблону поиска, содержащему символы *
     * в качестве wildcards (на месте * в строке может быть ноль или больше любых символов).<br/>
     * <a href="http://en.wikipedia.org/wiki/Wildcard_character#File_and_directory_patterns">Про * wildcard</a>.<br/>
     * Примеры шаблонов, которым удовлетворяет строка "distribute": "distr*", "*str*", "di*bute*".<br/>
     * Упрощение: достаточно поддерживать всего два символа * в шаблоне (их может быть и меньше двух).<br/>
     * Примечание: в данной постановке задачи НЕ предполагается использование регулярных выражений
     * и какого-либо высокоуровневого API (эти темы изучаются позже), цель - применить методы String.<br/>
     * Если <code>pattern</code> - пустая строка или <code>null</code>,
     * то результат содержит все строки данного набора.<br/>
     *
     * @param pattern шаблон поиска (все буквы в нем - в нижнем регистре)
     * @return строки, удовлетворяющие заданному шаблону поиска
     */
    @Override
    public Iterator<String> getStringsByPattern(String pattern) {
        Filter f = new Filter() {
            @Override
            public boolean filter(String s, String pattern) {
                return pattern == null || s.matches(pattern);
            }
        };
        return filterStrings(f, pattern.replaceAll("\\*", ".*"));
    }

    public static void main(String[] args) {
        StringFilter stringFilter = new StringFilterImpl();
        stringFilter.add(null);
        stringFilter.add("(765)-8843 11");
        stringFilter.add("IDpref2349dnc");

        for (Iterator<String> it = stringFilter.getStringsContaining("pref"); it.hasNext(); ) {
            String s = it.next();
            System.out.println(s);
        }
        System.out.println();
        for (Iterator<String> it = stringFilter.getStringsStartingWith("ID"); it.hasNext(); ) {
            String s = it.next();
            System.out.println(s);
        }
        System.out.println();
        for (Iterator<String> it = stringFilter.getStringsByNumberFormat("(###)-#### ##"); it.hasNext(); ) {
            String s = it.next();
            System.out.println(s);
        }
        System.out.println();
        for (Iterator<String> it = stringFilter.getStringsByPattern("*ef*"); it.hasNext(); ) {
            String s = it.next();
            System.out.println(s);
        }
    }
}
