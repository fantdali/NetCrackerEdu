package ru.skillbench.tasks.text.regex;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurriculumVitaeImpl implements CurriculumVitae {

    private String text;
    private Map<String, String> hideMap = new HashMap<>();
    private static final String FULL_NAME_PATTERN =
            "([A-Z][a-z]*[a-z.]) ([A-Z][a-z]*[a-z.]) ?([A-Z][a-z]*[a-z.])?";

    public CurriculumVitaeImpl() {
    }

    /**
     * Задает текст резюме.<br/>
     * О реализации: текст НЕ должен анализировать в этом методе.
     *
     * @param text Текст резюме
     */
    @Override
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Рекомендуется вызывать этот метод во всех остальных методах вашего класса.
     *
     * @return Текущий текст резюме (который мог измениться не только методом setText, но и методами update*).
     * @throws IllegalStateException Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public String getText() throws IllegalStateException {
        if (text != null) {
            return text;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Возвращает список телефонов в том же порядке, в котором они расположены в самом резюме.<br/>
     * О реализации: используйте {@link #PHONE_PATTERN} для поиска телефонов;
     * используйте группы этого регулярного выражения, чтобы извлечь код региона и extension из найденных номеров;
     * если код региона или extension не присутствует в номере, объект {@link Phone} должен хранить отрицательное значение.
     *
     * @return Список, который не может быть <code>null</code>, но может быть пустым (если ни одного телефона не найдено).
     * @throws IllegalStateException Если текст резюме не был задан путем вызова {@link #setText(String)}.
     * @see Phone
     */
    @Override
    public List<Phone> getPhones() throws IllegalStateException {
        List<Phone> phoneList = new LinkedList<>();
        Pattern p = Pattern.compile(PHONE_PATTERN);
        Matcher m = p.matcher(getText());
        while (m.find()) {
            int areaCode = m.group(1) == null ? -1 : Integer.parseInt(m.group(2));
            int extCode = m.group(6) == null ? -1 : Integer.parseInt(m.group(7));
            phoneList.add(new Phone(m.group(), areaCode, extCode));
        }
        return phoneList;
    }

    /**
     * Возвращает полное имя, т.е. ПЕРВУЮ часть текста резюме, которая удовлетворяет такие критериям:
     * <ol>
     * <li>полное имя содержит 2 или 3 слова, разделенных пробелом (' ');</li>
     * <li>каждое слово содержит не меньше двух символов;</li>
     * <li>первый символ слова - это заглавная латинская буква (буква английского алфавита в upper case);</li>
     * <li>последний символ слова - это либо точка ('.'), либо строчная(lower case) латинская буква;</li>
     * <li>не первые и не последние символы слова - это только строчные (lower case) латинские буквы.</li>
     * </ol>
     *
     * @return Полное имя (в точности равно значению в тексте резюме)
     * @throws NoSuchElementException Если резюме не содержит полного имени, которое удовлетворяет критериям.
     * @throws IllegalStateException  Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public String getFullName() throws NoSuchElementException, IllegalArgumentException {
        Pattern p = Pattern.compile(FULL_NAME_PATTERN);
        Matcher m = p.matcher(getText());
        if (m.find()) {
            return m.group().trim();
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Возвращает имя (первое слово из полного имени {@link #getFullName()}).
     *
     * @throws NoSuchElementException Если резюме не содержит полного имени.
     * @throws IllegalStateException  Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public String getFirstName() throws NoSuchElementException, IllegalArgumentException {
        String fullName = getFullName();
        return fullName.substring(0, fullName.indexOf(' '));
    }

    /**
     * Возвращает отчество (второе слово из полного имени {@link #getFullName()})
     * или <code>null</null>, если полное имя состоит только из двух слов.
     *
     * @throws NoSuchElementException Если резюме не содержит полного имени.
     * @throws IllegalStateException  Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public String getMiddleName() throws NoSuchElementException, IllegalArgumentException {
        String fullName = getFullName();
        String[] namesArray = fullName.split(" ");
        if (namesArray.length == 2) {
            return null;
        } else {
            return namesArray[1];
        }
    }

    /**
     * Возвращает фамилию (последнее слово из полного имени {@link #getFullName()}).
     *
     * @throws NoSuchElementException Если резюме не содержит полного имени.
     * @throws IllegalStateException  Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public String getLastName() throws NoSuchElementException, IllegalArgumentException {
        String fullName = getFullName();
        return fullName.substring(fullName.lastIndexOf(' ')).trim();
    }

    /**
     * Заменяет фамилию на <code>newLastName</code> в тексте резюме.
     *
     * @param newLastName Не может быть null
     * @throws NoSuchElementException Если резюме не содержит полного имени.
     * @throws IllegalStateException  Если текст резюме не был задан путем вызова {@link #setText(String)}.
     * @see #getLastName()
     */
    @Override
    public void updateLastName(String newLastName) throws NoSuchElementException, IllegalArgumentException {
        if (newLastName != null) {
            setText(getText().replace(getLastName(), newLastName));
        }
    }

    /**
     * Заменяет <code>oldPhone.getNumber()</code> на <code>newPhone.getNumber()</code> в тексте резюме.<br/>
     * О реализации: использование regex здесь ведет к большему объему кода, чем вызов не связанных с
     * регулярными выражениями методов {@link String} (или метода {@link String} и метода {@link StringBuilder}).
     *
     * @param oldPhone Не может быть null
     * @param newPhone Не может быть null
     * @throws IllegalArgumentException Если резюме не содержит текста, равного <code>oldPhone.getNumber()</code>.
     * @throws IllegalStateException    Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public void updatePhone(Phone oldPhone, Phone newPhone) throws IllegalArgumentException, IllegalStateException {
        if (newPhone != null && oldPhone != null && !oldPhone.equals(newPhone)) {
            List<Phone> phoneList = getPhones();

            if (!getText().contains(oldPhone.getNumber())) {
                throw new IllegalArgumentException();
            } else {
                setText(getText().replace(oldPhone.getNumber(), newPhone.getNumber()));
            }
        }
    }

    /**
     * Ищет строку <code>piece</code> в тексте резюме и скрывает ее, то есть заменяет каждый символ из
     * <code>piece</code> на символ 'X', за исключениеми следующих разделительных символов: ' ', '.' и '@'.
     * Число символов 'X' равно числу замененных символов.<br/>
     * Например: "John A. Smith" заменяется на "XXXX X. XXXXX", "john@hp.com" - на "XXXX@XX.XXX".<br/>
     * Эта замена может быть отменена путем вызова {@link #unhideAll()}.
     *
     * @param piece Не может быть null
     * @throws IllegalArgumentException Если резюме не содержит текста, равного <code>piece</code>.
     * @throws IllegalStateException    Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public void hide(String piece) throws IllegalArgumentException, IllegalStateException {
        if (piece != null && !getText().contains(piece)) {
            throw new IllegalArgumentException();
        } else {
            String privatePiece = piece.replaceAll("[^ \\.@]", "X");
            setText(getText().replaceAll(piece, privatePiece));
            hideMap.put(privatePiece, piece);
        }
    }

    /**
     * Ищет строку <code>phone</code> в тексте резюме и скрывает ее, то есть, заменяет все ЦИФРЫ из
     * <code>phone</code> на символ 'X'.<br/>
     * Например: "(123)456 7890" заменяется на "(XXX)XXX XXXX".<br/>
     * Эта замена может быть отменена путем вызова {@link #unhideAll()}.
     *
     * @param phone Не может быть null
     * @throws IllegalArgumentException Если резюме не содержит текста, равного <code>phone</code>.
     * @throws IllegalStateException    Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public void hidePhone(String phone) throws IllegalArgumentException, IllegalStateException {
        if (phone != null && !getText().contains(phone)) {
            throw new IllegalArgumentException();
        } else {
            String privatePhone = phone.replaceAll("[0-9]", "X");
            setText(getText().replace(phone, privatePhone));
            hideMap.put(privatePhone, phone);
        }
    }

    /**
     * Отменяет все изменения, сделанные методами {@link #hide(String)} и {@link #hidePhone(String)},
     * т.е. заменяет куски текста с символами 'X' в текущем тексте резюме (скрытые куски, вставленные ранее)
     * на соответствующие куски из исходного текста резюме.<br/>
     * Примечание: в резюме не может быть двух (или более) одинаковых скрытых кусков (одинаковых куско с 'X').<br/>
     * О реализации: исходные и скрытые куски следует хранить в некой коллекции.
     * Кроме того, эта коллекция должна очищаться при вызове {@link #setText(String)}.
     *
     * @return Число кусков, замененных в тексте резюме при выполнении метода
     * @throws IllegalStateException Если текст резюме не был задан путем вызова {@link #setText(String)}.
     */
    @Override
    public int unhideAll() throws IllegalStateException {
        int hidePiecesNumber = hideMap.size();
        getText();

        for (Map.Entry<String, String> entry : hideMap.entrySet()) {
            setText(getText().replace(entry.getKey(), entry.getValue()));
        }
        hideMap.clear();
        return hidePiecesNumber;
    }

    public static void main(String[] args) {
        CurriculumVitae CV = new CurriculumVitaeImpl();
    }
}