package ru.skillbench.tasks.text;

import java.io.PrintStream;
import java.util.*;

public class WordCounterImpl implements WordCounter{
    /**
     * Text passed for analysis
     */
    private String text = null;
    private final Map<String, Long> words = new LinkedHashMap<>();
    private boolean isTextModified = false;

    /**
     * Принимает текст для анализа
     *
     * @param text текст для анализа
     */
    @Override
    public void setText(String text) {
        this.text = text;
        isTextModified = true;
    }

    /**
     * @return текст, переданный для анализа при последнем вызове метода
     * {@link #setText(String) setText}, или <code>null</code>,
     * если указанный метод еще не вызывался или последний раз вызывался
     * с параметром <code>null</code>
     */
    @Override
    public String getText() {
        return text;
    }

    String wordProcess(String word) {
        if (!(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>')) {
            return word.replaceAll("[\\.,;:-]", "").toLowerCase(Locale.ROOT);
        } else {
            return null;
        }
    }

    /**
     * Возвращает {@link Map}&lt;{@link String}, {@link Long}&gt;, сопоставляющую каждому
     * слову (длиной не менее 1 символа) количество его вхождений в анализируемый текст.<br/>
     * Все возвращаемые слова должны быть приведены к нижнему регистру.<br/>
     * Дополнительно оценивается, если из рассмотрения исключены слова, начинающиеся с &lt;
     * и заканчивающиеся на &gt; (то есть, расположенные в угловых скобках).<br/>
     *
     * @return результат подсчета количеств вхождений слов
     * @throws IllegalStateException если не задан текст для анализа
     *                               (если метод {@link #setText(String)} еще не вызывался
     *                               или последний раз вызывался с параметром <code>null</code>)
     */
    @Override
    public Map<String, Long> getWordCounts() throws IllegalStateException {
        if (text == null) {
            throw new IllegalStateException();
        }

        if (isTextModified) {
            words.clear();
            Scanner scanner = new Scanner(text);
            String processedWord;

            while (scanner.hasNext()) {
                processedWord = wordProcess(scanner.next());
                if (processedWord != null) {
                    if (words.containsKey(processedWord)) {
                        words.replace(processedWord, words.get(processedWord) + 1L);
                    } else {
                        words.put(wordProcess(processedWord), 1L);
                    }
                }
            }
        }

        isTextModified = false;
        return words;
    }

    /**
     * Возвращает список из {@link Map.Entry}&lt;{@link String}, {@link Long}&gt;,
     * сопоставляющий каждому слову количество его вхождений в анализируемый текст
     * и упорядоченный в прядке убывания количества вхождений слова.<br/>
     * Слова с одинаковым количеством вхождений упорядочиваются в алфавитном порядке (без учета регистра!).<br/>
     * Все возвращаемые слова должны быть приведены к нижнему регистру.<br/>
     * <p>
     * ПРИМЕЧАНИЕ: при реализации рекомендуется использовать {@link #sort(Map, Comparator)}
     *
     * @return упорядоченный результат подсчета количеств вхождений слов
     * @throws IllegalStateException если не задан текст для анализа
     *                               (если метод {@link #setText(String)} еще не вызывался
     *                               или последний раз вызывался с параметром <code>null</code>)
     */
    @Override
    public List<Map.Entry<String, Long>> getWordCountsSorted() throws IllegalStateException {
        return sort(getWordCounts(), new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                int result = (int) (o2.getValue() - o1.getValue());
                if (result == 0) {
                    result = o1.getKey().compareTo(o2.getKey());
                }
                return result;
            }
        });
    }

    /**
     * Упорядочивает содержимое <code>map</code> (это слова и количество их вхождений)
     * в соответствии с <code>comparator</code>.<br/>
     * <br/>
     * ПРИМЕЧАНИЕ:Этот метод работает только со своими параметрами, но не с полями объекта {@link WordCounter}.
     *
     * @param map        Например, неупорядоченный результат подсчета числа слов
     * @param comparator
     * @return Содержимое <code>map</code> в виде списка, упорядоченного в соответствии с <code>comparator</code>
     */
    @Override
    public <K extends Comparable<K>, V extends Comparable<V>> List<Map.Entry<K, V>> sort(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort(comparator);
        return list;
    }

    /**
     * Распечатывает <code>entryList</code> (это слова и количество их вхождений)
     * в поток вывода <code>ps</code>.<br/>
     * Формат вывода следующий:
     * <ul>
     * 	<li>Каждое слово вместе с количеством вхождений выводится на отдельной строке</li>
     * 	<li>На каждой строке слово и количество вхождений разделены одним(!) пробелом,
     * никаких других символов на строке быть не должно</li>
     * </ul>
     * Все выводимые слова должны быть приведены к нижнему регистру.<br/>
     * <br/>
     * ПРИМЕЧАНИЕ: Этот метод работает только со своими параметрами, но не с полями объекта {@link WordCounter}.
     *
     * @param entries Список пар - например, результат подсчета числа слов
     * @param ps      Поток вывода - например, System.out.
     */
    @Override
    public <K, V> void print(List<Map.Entry<K, V>> entries, PrintStream ps) {
        for (Map.Entry<K, V> entry : entries) {
            ps.println(entry.getKey().toString().toLowerCase(Locale.ROOT) + " " +
                    entry.getValue().toString());
        }
    }

    public static void main(String[] args) {
        String text = "GNU LESSER GENERAL PUBLIC LICENSE\n" +
                "                       Version 3, 29 June 2007\n" +
                "\n" +
                " Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>\n" +
                " Everyone is permitted to copy and distribute verbatim copies\n" +
                " of this license document, but changing it is not allowed.\n" +
                "\n" +
                "\n" +
                "  This version of the GNU Lesser General Public License incorporates\n" +
                "the terms and conditions of version 3 of the GNU General Public\n" +
                "License, supplemented by the additional permissions listed below.\n" +
                "\n" +
                "  0. Additional Definitions.\n" +
                "\n" +
                "  As used herein, \"this License\" refers to version 3 of the GNU Lesser\n" +
                "General Public License, and the \"GNU GPL\" refers to version 3 of the GNU\n" +
                "General Public License.\n" +
                "\n" +
                "  \"The Library\" refers to a covered work governed by this License,\n" +
                "other than an Application or a Combined Work as defined below.\n" +
                "\n" +
                "  An \"Application\" is any work that makes use of an interface provided\n" +
                "by the Library, but which is not otherwise based on the Library.\n" +
                "Defining a subclass of a class defined by the Library is deemed a mode\n" +
                "of using an interface provided by the Library.\n" +
                "\n" +
                "  A \"Combined Work\" is a work produced by combining or linking an\n" +
                "Application with the Library.  The particular version of the Library\n" +
                "with which the Combined Work was made is also called the \"Linked\n" +
                "Version\".\n" +
                "\n" +
                "  The \"Minimal Corresponding Source\" for a Combined Work means the\n" +
                "Corresponding Source for the Combined Work, excluding any source code\n" +
                "for portions of the Combined Work that, considered in isolation, are\n" +
                "based on the Application, and not on the Linked Version.\n" +
                "\n" +
                "  The \"Corresponding Application Code\" for a Combined Work means the\n" +
                "object code and/or source code for the Application, including any data\n" +
                "and utility programs needed for reproducing the Combined Work from the\n" +
                "Application, but excluding the System Libraries of the Combined Work.\n" +
                "\n" +
                "  1. Exception to Section 3 of the GNU GPL.\n" +
                "\n" +
                "  You may convey a covered work under sections 3 and 4 of this License\n" +
                "without being bound by section 3 of the GNU GPL.\n" +
                "\n" +
                "  2. Conveying Modified Versions.\n" +
                "\n" +
                "  If you modify a copy of the Library, and, in your modifications, a\n" +
                "facility refers to a function or data to be supplied by an Application\n" +
                "that uses the facility (other than as an argument passed when the\n" +
                "facility is invoked), then you may convey a copy of the modified\n" +
                "version:\n" +
                "\n" +
                "   a) under this License, provided that you make a good faith effort to\n" +
                "   ensure that, in the event an Application does not supply the\n" +
                "   function or data, the facility still operates, and performs\n" +
                "   whatever part of its purpose remains meaningful, or\n" +
                "\n" +
                "   b) under the GNU GPL, with none of the additional permissions of\n" +
                "   this License applicable to that copy.\n" +
                "\n" +
                "  3. Object Code Incorporating Material from Library Header Files.\n" +
                "\n" +
                "  The object code form of an Application may incorporate material from\n" +
                "a header file that is part of the Library.  You may convey such object\n" +
                "code under terms of your choice, provided that, if the incorporated\n" +
                "material is not limited to numerical parameters, data structure\n" +
                "layouts and accessors, or small macros, inline functions and templates\n" +
                "(ten or fewer lines in length), you do both of the following:\n" +
                "\n" +
                "   a) Give prominent notice with each copy of the object code that the\n" +
                "   Library is used in it and that the Library and its use are\n" +
                "   covered by this License.\n" +
                "\n" +
                "   b) Accompany the object code with a copy of the GNU GPL and this license\n" +
                "   document.\n" +
                "\n" +
                "  4. Combined Works.\n" +
                "\n" +
                "  You may convey a Combined Work under terms of your choice that,\n" +
                "taken together, effectively do not restrict modification of the\n" +
                "portions of the Library contained in the Combined Work and reverse\n" +
                "engineering for debugging such modifications, if you also do each of\n" +
                "the following:\n" +
                "\n" +
                "   a) Give prominent notice with each copy of the Combined Work that\n" +
                "   the Library is used in it and that the Library and its use are\n" +
                "   covered by this License.\n" +
                "\n" +
                "   b) Accompany the Combined Work with a copy of the GNU GPL and this license\n" +
                "   document.\n" +
                "\n" +
                "   c) For a Combined Work that displays copyright notices during\n" +
                "   execution, include the copyright notice for the Library among\n" +
                "   these notices, as well as a reference directing the user to the\n" +
                "   copies of the GNU GPL and this license document.\n" +
                "\n" +
                "   d) Do one of the following:\n" +
                "\n" +
                "       0) Convey the Minimal Corresponding Source under the terms of this\n" +
                "       License, and the Corresponding Application Code in a form\n" +
                "       suitable for, and under terms that permit, the user to\n" +
                "       recombine or relink the Application with a modified version of\n" +
                "       the Linked Version to produce a modified Combined Work, in the\n" +
                "       manner specified by section 6 of the GNU GPL for conveying\n" +
                "       Corresponding Source.\n" +
                "\n" +
                "       1) Use a suitable shared library mechanism for linking with the\n" +
                "       Library.  A suitable mechanism is one that (a) uses at run time\n" +
                "       a copy of the Library already present on the user's computer\n" +
                "       system, and (b) will operate properly with a modified version\n" +
                "       of the Library that is interface-compatible with the Linked\n" +
                "       Version.\n" +
                "\n" +
                "   e) Provide Installation Information, but only if you would otherwise\n" +
                "   be required to provide such information under section 6 of the\n" +
                "   GNU GPL, and only to the extent that such information is\n" +
                "   necessary to install and execute a modified version of the\n" +
                "   Combined Work produced by recombining or relinking the\n" +
                "   Application with a modified version of the Linked Version. (If\n" +
                "   you use option 4d0, the Installation Information must accompany\n" +
                "   the Minimal Corresponding Source and Corresponding Application\n" +
                "   Code. If you use option 4d1, you must provide the Installation\n" +
                "   Information in the manner specified by section 6 of the GNU GPL\n" +
                "   for conveying Corresponding Source.)\n" +
                "\n" +
                "  5. Combined Libraries.\n" +
                "\n" +
                "  You may place library facilities that are a work based on the\n" +
                "Library side by side in a single library together with other library\n" +
                "facilities that are not Applications and are not covered by this\n" +
                "License, and convey such a combined library under terms of your\n" +
                "choice, if you do both of the following:\n" +
                "\n" +
                "   a) Accompany the combined library with a copy of the same work based\n" +
                "   on the Library, uncombined with any other library facilities,\n" +
                "   conveyed under the terms of this License.\n" +
                "\n" +
                "   b) Give prominent notice with the combined library that part of it\n" +
                "   is a work based on the Library, and explaining where to find the\n" +
                "   accompanying uncombined form of the same work.\n" +
                "\n" +
                "  6. Revised Versions of the GNU Lesser General Public License.\n" +
                "\n" +
                "  The Free Software Foundation may publish revised and/or new versions\n" +
                "of the GNU Lesser General Public License from time to time. Such new\n" +
                "versions will be similar in spirit to the present version, but may\n" +
                "differ in detail to address new problems or concerns.\n" +
                "\n" +
                "  Each version is given a distinguishing version number. If the\n" +
                "Library as you received it specifies that a certain numbered version\n" +
                "of the GNU Lesser General Public License \"or any later version\"\n" +
                "applies to it, you have the option of following the terms and\n" +
                "conditions either of that published version or of any later version\n" +
                "published by the Free Software Foundation. If the Library as you\n" +
                "received it does not specify a version number of the GNU Lesser\n" +
                "General Public License, you may choose any version of the GNU Lesser\n" +
                "General Public License ever published by the Free Software Foundation.\n" +
                "\n" +
                "  If the Library as you received it specifies that a proxy can decide\n" +
                "whether future versions of the GNU Lesser General Public License shall\n" +
                "apply, that proxy's public statement of acceptance of any version is\n" +
                "permanent authorization for you to choose that version for the\n" +
                "Library.";
        WordCounter wordCounter = new WordCounterImpl();
        wordCounter.setText(text);
        wordCounter.print(wordCounter.getWordCountsSorted(), System.out);
    }
}
