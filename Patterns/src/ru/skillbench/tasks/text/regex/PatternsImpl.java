package ru.skillbench.tasks.text.regex;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsImpl implements Patterns {

    private static final Pattern SQLIdentifierPattern =
            Pattern.compile("\\b[a-zA-Z]\\w{0,29}");
    private static final Pattern EmailPattern =
            Pattern.compile("\\b(([\\dA-Za-z][\\dA-Za-z-._]{0,20}[\\dA-Za-z]\\b)|([\\dA-Za-z]))" +
                            "@(([\\dA-Za-z]+[\\dA-Za-z-]*[\\dA-Za-z]+\\b)\\.)+(ru|com|net|org)\\b");
    private static final Pattern HrefTagPattern =
            Pattern.compile("<\\s*(?i)A\\s*(?:href)(?-i)\\s*=\\s*(?:\"([^\"]*)\"|([^\\s>]+))\\s*(?:>|/>)?");
    /**
     * Идентификаторы в стандартном SQL (имена таблиц, столбцов и др.) должны начинаться c буквы латинского алфавита, <br/>
     * а остальными символами могут быть как буквы латинского алфавита, так и цифры, а также знаки подчеркивания ("_").<br/>
     * Длина идентификатора не должна меньше 1 символа и не должна быть больше 30 символов.<br/>
     *
     * @return шаблон для поиска строк, удовлетворяющих вышеуказанным требованиям к идентификаторам языка SQL.
     */
    @Override
    public Pattern getSQLIdentifierPattern() {
        return SQLIdentifierPattern;
    }

    /**
     * Email имеет формат: "аккаунт"@"домен"."домен_первого_уровня"<br/>
     * "Аккаунт" должен быть длиной не более 22 символов и состоять из символов:
     * латинские буквы, цифры, знак подчеркивания ("_"), точка ("."), дефис ("-").<br/>
     * Аккаунт не может начинаться с символов дефис ("-"), точка (".") или знак подчеркивания ("_").<br/>
     * Аккаунт не может заканчиваться символом дефис ("-"), точка (".") или знак подчеркивания ("_").<br/>
     * "Домен" может быть доменом любого уровня, каждый уровень отделяется от другого символом точка (".").<br/>
     * Название домена каждого уровня должно состоять более чем из одного символа,
     * начинаться и заканчиваться буквой латинского алфавита или цифрой.<br/>
     * Промежуточными символами могут быть буквы латинского алфавита, цифры или дефис.<br/>
     * Например, следующие домены являются корректными в этом контексте: "s7" (в info@s7.ru)
     * или "e-katalog" (в "support@e-katalog.ru) или "echo.msk" (в echo@echo.msk.ru).<br/>
     * "Домен_первого_уровня" - допустим один из следующих: .ru, .com, .net, .org.
     *
     * @return шаблон для поиска email адресов, удовлетворяющих вышеуказанным требованиям.
     */
    @Override
    public Pattern getEmailPattern() {
        return EmailPattern;
    }

    /**
     * Содержащиеся на web-странице гиперссылки описываются тегом &lt;a href = ...&gt; (или &lt;a href=.../&gt;).<br/>
     * Ремарка для начинающих: в HTML &gt; - это > ("больше"), &lt; - это < ("меньше"), а комментарии пишутся в таком
     * "странном" виде, чтобы они корректно отображались в HTML-справке, которая из них генерируется через javadoc).<br/>
     * То есть, следует читать: гиперссылки описываются тегом <a href = ...> (или <a href=.../>).<br/>
     * Между символом "меньше", именем тега, названием атрибута, знаком "равно" и символом "больше"
     * могут быть следующие пробельные символы:
     * пробел, табуляция, возврат каретки (CR), перевод строки (LF), перевод формата (FF).<br/>
     * <br/>
     * Метод должен выделять в HTML-тексте теги типа "гиперссылка", здесь
     * "гиперссылкой" будем условно называть закрытый или незакрытый тег A с обязательным атрибутом HREF.<br/>
     * Имена тега A и атрибута HREF (как и другие имена в HTML) не чувствительны к регистру.<br/>
     * Значение атрибута href может быть заключено в двойные кавычки (href="значение"), хотя это необязательно.<br/>
     * Если использованы кавычки, то в значении МОГУТ быть пробельные символы (см. выше),
     * если кавычек нет - пробельных символов в значении быть не должно.<br/>
     *
     * @return шаблон для поиска в HTML-тексте тегов типа "гиперссылка".
     */
    @Override
    public Pattern getHrefTagPattern() {
        return HrefTagPattern;
    }

    /**
     * Метод возвращает список всех соответствий шаблону <code>pattern</code> в строке <code>input</code>.
     *
     * @param input   строка для поиска
     * @param pattern шаблон поиска.
     * @return Список всех соответствий (пустой список, если соответствий нет).
     */
    @Override
    public List<String> findAll(String input, Pattern pattern) {
        List<String> matches = new LinkedList<>();
        Matcher m = pattern.matcher(input);
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }

    /**
     * Метод подсчитывает в строке <code>input</code> число соответствий шаблону, заданному выражением <code>regex</code>.
     * На случай, если в <code>regex</code> встречаются буквы в конкретном регистре (как в выражении {@link #getHrefTagPattern()}),
     * следует предусмотреть, чтобы соответствием считались в т.ч. строки, содержащие те буквы в ином регистре.
     *
     * @param input строка, подлежащая проверке.
     * @param regex регулярное выражение - шаблон поиска.
     * @return число соответствий шаблону в строке <code>input</code>.
     */
    @Override
    public int countMatches(String input, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        int count = 0;
        while (m.find()) ++count;
        return count;
    }

    public static void main(String[] args) {
        Patterns patterns = new PatternsImpl();
        for (String s : patterns.findAll("<A HREF = \"http://blogs.msd n.com/bclteam\" <a HrEF = http://blogs.msdn.com/bclteam>",
                                                patterns.getHrefTagPattern())){
            System.out.println(s);
        }

        System.out.println(patterns.countMatches("<A HREF = \"http://blogs.msd n.com/bclteam\"<a HrEF = http://blogs.msdn.com/bclteam>",
                                                "hRef"));
    }
}
