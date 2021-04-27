package ru.skillbench.tasks.javaapi.io;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class WordFinderImpl implements WordFinder{

    private String text;
    private Set<String> words;
    private Set<String> foundWords;
    /**
     * @return Текущий текст для поиска или <code>null</code>,
     * если ни один из set-методов не был выполнен успешно.
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * Принимает готовый текст для поиска (а не читает текст из файла/потока).
     *
     * @param text Текст для поиска
     * @throws IllegalArgumentException если <code>src == null</code>
     */
    @Override
    public void setText(String text) {
        if (text == null) throw new IllegalArgumentException();
        this.text = text;
    }

    /**
     * Считывает текст из указанного потока ввода. Кодировка не важна.
     *
     * @param is Поток ввода
     * @throws IOException              в случае ошибок при чтении из потока
     * @throws IllegalArgumentException если <code>is == null</code>
     */
    @Override
    public void setInputStream(InputStream is) throws IOException {
        if (is == null) throw new IllegalArgumentException();

        try (BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte buf[] = new byte[8192];
            int n;
            while ((n = bis.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            text = bos.toString();
        } catch (IOException e) {
            text = null;
            words = null;
            foundWords = null;
            throw e;
        }
    }

    /**
     * Считывает текст из указанного файла. Кодировка не важна.
     *
     * @param filePath Путь к файлу с текстом
     * @throws IOException              в случае ошибок при чтении файла
     * @throws IllegalArgumentException если <code>filePath == null</code>
     */
    @Override
    public void setFilePath(String filePath) throws IOException {
        if (filePath == null) throw new IllegalArgumentException();

        setInputStream(new FileInputStream(filePath));
    }

    /**
     * Считывает текст из указанного файла через {@link Class#getResourceAsStream(String)}.<br/>
     * Это позволяет указывать краткое имя файла и читать его даже в том случае, если он лежит
     * в jar-нике вместе с классами и если прямая работа с файлами запрещена настройками безопасности.<br/>
     * Кодировка не важна.
     *
     * @param resourceName Короткое имя ресурса (файла), который должен лежать в том же пакете, что и класс
     * @throws IOException              в случае ошибок при чтении
     * @throws IllegalArgumentException если <code>resourceName == null</code>
     */
    @Override
    public void setResource(String resourceName) throws IOException {
        if (resourceName == null) throw new IllegalArgumentException();

        setInputStream(getClass().getClassLoader().getResourceAsStream(resourceName));
    }

    /**
     * Ищет в тексте все слова, начинающиеся с указанной последовательности символов,
     * без учета их регистра ('А' и 'а' считаются одним и тем же символом). <br/>
     * Результат возвращает в виде {@link Stream}, порядок элементов в котором не важен.<br/>
     * Если <code>begin</code> - пустая строка или <code>null</code>,
     * то результат содержит все слова, найденные в тексте.<br/>
     * Все возвращенные слова должны быть приведены к нижнему регистру.
     *
     * @param begin первые символы искомых слов
     * @return слова, начинающиеся с указанной последовательности символов
     * @throws IllegalStateException если нет текста для поиска
     *                               (если ни один setter-метод не выполнялся
     *                               или если он последний раз выполнился с ошибкой)
     */
    @Override
    public Stream<String> findWordsStartWith(String begin) {
        if (text == null) throw new IllegalStateException();

        Scanner scanner = new Scanner(text);
        words = new TreeSet<>();
        while (scanner.hasNext()) {
            words.add(scanner.next().toLowerCase());
        }

        foundWords = new TreeSet<>();
        String beginLowerCase = begin == null ? "" : begin.toLowerCase();
        words.stream().filter(w -> w.startsWith(beginLowerCase)).forEach(w -> foundWords.add(w));
        return foundWords.stream();
    }

    /**
     * Превращает слова, найденные в {@link #findWordsStartWith(String)},
     * в текст с разделителями "пробел",
     * предварительно приведя их к нижнему регистру
     * и отсортировав по алфавиту.<br/>
     * И записывает этот текст в <code>os</code>.
     *
     * @param os Поток вывода. Кодировка не важна.
     * @throws IOException           в случае ошибок при записи в поток
     * @throws IllegalStateException если поиск слов не выполнялся
     *                               (если {@link #findWordsStartWith(String)} не выполнялся после setter-метода
     *                               или если setter-метод последний раз выполнился с ошибкой)
     */
    @Override
    public void writeWords(OutputStream os) throws IOException {
        if (foundWords == null) throw new IllegalStateException();

        String[] foundWordsArray = foundWords.toArray(new String[0]);
        for (int i = 0; i < foundWordsArray.length - 1; ++i) {
            os.write((foundWordsArray[i] + " ").getBytes());
        }
        os.write(foundWordsArray[foundWordsArray.length - 1].getBytes());
    }

    public static void main(String[] args) throws IOException {
        WordFinder wordFinder = new WordFinderImpl();
        try {
            wordFinder.setResource("text.txt");
            wordFinder.findWordsStartWith("fol");
            wordFinder.writeWords(System.out);
        } catch (Exception e) {
            System.err.println(e);
            throw e;
        }
    }
}
