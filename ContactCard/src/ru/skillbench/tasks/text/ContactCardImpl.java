package ru.skillbench.tasks.text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactCardImpl implements ContactCard {
    /**
     * Contact information.
     */
    private String fullName;
    private String organization;
    private String gender = null;
    private Calendar birthDay = null;
    private HashMap<String, String> phones = new HashMap<>();

    /**
     * Основной метод парсинга: создает экземпляр карточки из источника данных (Scanner),
     * содержащего следующие текстовые данные о человеке (5 полей):<br/>
     * 1) FN - Полное имя - обязательное поле<br/>
     * 2) ORG - Организация - обязательное поле<br/>
     * 3) GENDER - Пол - один символ: F или M. Это поле в данных может отсутствовать.<br/>
     * 4) BDAY - Дата рождения - в следующем формате: "DD-MM-YYYY", где DD - 2 цифры, обозначающие день,
     * MM - 2 цифры, обозначающие месяц, YYYY - 4 цифры, обозначающие год.
     * Это поле в данных может отсутствовать.<br/>
     * 5) TEL - Номер телефона - ровно 10 цифр, не включающие код страны. Полей TEL может быть 0 или несколько,
     * и разные поля TEL различаются значением обязательного атрибута TYPE, например:
     * TEL;TYPE=HOME,VOICE:4991112233<br/>
     * <p/>
     * Каждое из этих полей в источнике данных расположено на отдельной строке;
     * строки в стандарте vCard отделяются символом CRLF (\r\n).<br/>
     * Имя поля отделяется от его значения двоеточием, например: GENDER:F<br/>
     * Если нужно, можно предположить, что порядок полей будет именно такой, как выше.<br/>
     * Но первой строкой всегда идет BEGIN:VCARD, последней - END:VCARD.<br/>
     * Пример содержимого Scanner:<br/>
     * <code>
     * BEGIN:VCARD
     * FN:Forrest Gump
     * ORG:Bubba Gump Shrimp Co.
     * BDAY:06-06-1944
     * TEL;TYPE=WORK,VOICE:4951234567
     * TEL;TYPE=CELL,VOICE:9150123456
     * END:VCARD
     * </code>
     * <p/>
     * ПРИМЕЧАНИЕ: Такой метод в реальных приложениях был бы static, однако
     * система проверки учебных задач проверяет только не-статические методы.
     *
     * @param scanner Источник данных
     * @return {@link ContactCard}, созданный из этих данных
     * @throws InputMismatchException Возникает, если структура или значения данных не соответствуют формату,
     *                                описанному выше; например, если после названия поля нет двоеточия или дата указана в ином формате
     *                                или номер телефона содержит неверное число цифр.
     * @throws NoSuchElementException Возникает, если данные не содержат обязательных полей
     *                                (FN, ORG, BEGIN:VCARD, END:VCARD)
     */
    @Override
    public ContactCard getInstance(Scanner scanner) throws InputMismatchException,
            NoSuchElementException {

        String currentStr = scanner.nextLine();
        Matcher matcher = null;
        Pattern pattern = null;

        pattern = Pattern.compile("BEGIN:VCARD");
        matcher = pattern.matcher(currentStr);
        if (!matcher.find()) {
            throw new NoSuchElementException();
        } else {
            currentStr = scanner.nextLine();
        }

        pattern = Pattern.compile("FN:");
        matcher = pattern.matcher(currentStr);
        if (!matcher.find()) {
            throw new NoSuchElementException();
        } else {
            fullName = new String(currentStr.substring("FN:".length()));
            currentStr = scanner.nextLine();
        }

        pattern = Pattern.compile("ORG:(.*)$");
        matcher = pattern.matcher(currentStr);
        if (!matcher.find()) {
            throw new NoSuchElementException();
        } else {
            organization = new String(currentStr.substring("ORG:".length()).trim());
            currentStr = scanner.nextLine();
        }

        pattern = Pattern.compile("GENDER:[MF]$");
        matcher = Pattern.compile("GENDER:").matcher(currentStr);
        if (matcher.find()) {
            matcher = pattern.matcher(currentStr);
            if (!matcher.find()) {
                throw new InputMismatchException();
            }

            gender = currentStr.substring("GENDER:".length()).trim();
            currentStr = scanner.nextLine();
        }

        pattern = Pattern.compile("BDAY:\\d{2}-\\d{2}-\\d{4}$");
        matcher = Pattern.compile("BDAY:").matcher(currentStr);
        if (matcher.find()) {
            matcher = pattern.matcher(currentStr);
            if (!matcher.find()) {
                throw new InputMismatchException();
            }
            String date = currentStr.substring("BDAY:".length()).trim();
            currentStr = scanner.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            birthDay = new GregorianCalendar();

            try {
                birthDay.setTime(sdf.parse(date));
            } catch (ParseException e) {
                throw new InputMismatchException();
            }
        }

        pattern = Pattern.compile("TEL;TYPE=([a-zA-Z]+):");
        matcher = pattern.matcher(currentStr);
        Pattern specialPattern = Pattern.compile("TEL;TYPE=([a-zA-Z]+):(\\d{10})$");
        Matcher specialMatcher = specialPattern.matcher(currentStr);
        while (matcher.find()) {
            if (!specialMatcher.find()) {
                throw new InputMismatchException();
            }

            String temp1 = currentStr.substring("TEL;TYPE=".length(), currentStr.indexOf(':'));
            String temp2 = currentStr.substring(currentStr.indexOf(':') + 1);
            phones.put(temp1, temp2);

            currentStr = scanner.nextLine();
            matcher = pattern.matcher(currentStr);
            specialMatcher = specialPattern.matcher(currentStr);
        }

        pattern = Pattern.compile("END:VCARD$");
        matcher = pattern.matcher(currentStr);
        if (!matcher.find()) {
            throw new NoSuchElementException();
        }

        return this;
    }

    /**
     * Метод создает {@link Scanner} и вызывает {@link #getInstance(Scanner)}
     *
     * @param data Данные для разбора, имеющие формат, описанный в {@link #getInstance(Scanner)}
     * @return {@link ContactCard}, созданный из этих данных
     */
    @Override
    public ContactCard getInstance(String data) {
        Scanner scanner = new Scanner(data);
        return getInstance(scanner);
    }

    /**
     * @return Полное имя - значение vCard-поля FN: например, "Forrest Gump"
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * @return Организация - значение vCard-поля ORG: например, "Bubba Gump Shrimp Co."
     */
    @Override
    public String getOrganization() {
        return organization;
    }

    /**
     * Если поле GENDER отсутствует в данных или равно "M", этот метод возвращает false
     *
     * @return true если этот человек женского пола (GENDER:F)
     */
    @Override
    public boolean isWoman() {
        return ((gender == null) || gender.equals("M")) ? false : true;
    }

    /**
     * ПРИМЕЧАНИЕ: в современных приложениях рекомендуется для работы с датой применять java.time.LocalDate,
     * однако такие классы как java.util.Calendar или java.util.Date необходимо знать.
     *
     * @return День рождения человека в виде {@link Calendar}
     * @throws NoSuchElementException Если поле BDAY отсутствует в данных
     */
    @Override
    public Calendar getBirthday() throws NoSuchElementException {
        if (birthDay == null) {
            throw new NoSuchElementException();
        } else {
            return birthDay;
        }
    }

    /**
     * ПРИМЕЧАНИЕ: В реализации этого метода рекомендуется использовать {@link DateTimeFormatter}
     *
     * @return Возраст человека на данный момент в виде {@link Period}
     * @throws NoSuchElementException Если поле BDAY отсутствует в данных
     */
    @Override
    public Period getAge() throws NoSuchElementException {
        if (birthDay == null) {
            throw new NoSuchElementException();
        } else {
            LocalDate startDate = LocalDate.of(birthDay.get(Calendar.YEAR),
                    birthDay.get(Calendar.MONTH) + 1,
                    birthDay.get(Calendar.DAY_OF_MONTH));
            LocalDate endDate = LocalDate.now();
            Period period = Period.between(startDate, endDate);

            return period;
        }
    }

    /**
     * @return Возраст человека в годах: например, 74
     * @throws NoSuchElementException Если поле BDAY отсутствует в данных
     */
    @Override
    public int getAgeYears() throws NoSuchElementException {
        Period period = getAge();
        return period.getYears();
    }

    /**
     * Возвращает номер телефона в зависимости от типа.
     *
     * @param type Тип телефона, который содержится в данных между строкой "TEL;TYPE=" и двоеточием
     * @return Номер телефона - значение vCard-поля TEL, приведенное к следующему виду: "(123) 456-7890"
     * @throws NoSuchElementException если в данных нет телефона указанного типа
     */
    @Override
    public String getPhone(String type) throws NoSuchElementException {
        if ((phones.isEmpty()) || (phones.get(type) == null)) {
            throw new NoSuchElementException();
        } else {
            StringBuilder number = new StringBuilder(phones.get(type));
            String temp1 = "(" + number.substring(0, 3) + ") ";
            String temp2 = number.substring(3, 6) + "-";
            String temp3 = number.substring(6, 10);

            number.setLength(0);
            return number.append(temp1).append(temp2).append(temp3).toString();
        }
    }
}
