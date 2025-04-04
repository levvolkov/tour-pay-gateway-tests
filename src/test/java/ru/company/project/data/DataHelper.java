package ru.company.project.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;


/**
 * Генератор тестовых данных для платежной системы.
 * Поддерживает относительные даты, случайные имена и CVC-коды.
 */
public class DataHelper {
    private static final Faker fakerEn = new Faker(new Locale("en"));
    private static final Faker fakerRu = new Faker(new Locale("ru"));

    /**
     * Генерирует месяц в формате "MM" на основе входных данных.
     * @param monthInput Может быть:
     *                   - Integer (смещение от текущего месяца: 1 = следующий месяц)
     *                   - String (конкретное значение: "13" или пустая строка)
     * @return Месяц в формате "MM" или пустую строку
     */
    public static String getMonth(Object monthInput) {
        if (monthInput instanceof Integer) {
            int relativeMonth = (Integer) monthInput;
            LocalDate date = relativeMonth >= 0
                    ? LocalDate.now().plusMonths(relativeMonth)
                    : LocalDate.now().minusMonths(-relativeMonth);
            return date.format(DateTimeFormatter.ofPattern("MM"));
        }
        String monthStr = monthInput.toString();
        if (monthStr.isEmpty()) {
            return "";
        }
        return monthStr;
    }

    /**
     * Генерирует год в формате "yy" на основе входных данных.
     * @param yearInput Может быть:
     *                  - Integer (смещение от текущего года: 1 = следующий год)
     *                  - String (конкретное значение: "25" или пустая строка)
     * @return Год в формате "yy" или пустую строку
     */
    public static String getYear(Object yearInput) {
        if (yearInput instanceof Integer) {
            int relativeYear = (Integer) yearInput;
            LocalDate date = relativeYear >= 0
                    ? LocalDate.now().plusYears(relativeYear)
                    : LocalDate.now().minusYears(-relativeYear);
            return date.format(DateTimeFormatter.ofPattern("yy"));
        }
        String yearStr = yearInput.toString();
        if (yearStr.isEmpty()) {
            return "";
        }
        return yearStr;
    }

    /**
     * Генерирует имя владельца карты по заданному формату.
     * @param format Поддерживаемые форматы:
     *               - "FIRST_NAME_HOLDER" : только имя (англ.)
     *               - "VALID_HOLDER" : имя и фамилия (англ.)
     *               - "HOLDER_CYRILLIC" : имя и фамилия (рус.)
     *               - Любая другая строка: возвращается как есть (для пустого поля и спецсимволов)
     * @throws NullPointerException если format == null
     */
    public static String getHolderNameFormat(String format) {
        Objects.requireNonNull(format, "Format cannot be null");
        String upperFormat = format.toUpperCase();

        switch (upperFormat) {
            case "FIRST_NAME_HOLDER": return fakerEn.name().firstName();
            case "VALID_HOLDER": return fakerEn.name().firstName() + " " + fakerEn.name().lastName();
            case "HOLDER_CYRILLIC": return fakerRu.name().firstName() + " " + fakerRu.name().lastName();
            default: return format; // Для тестов спецсимволов (инъекций)!
        }
    }

    /**
     * Генерирует CVC-код, заменяя '#' в строке на случайные цифры.
     * @param numberStringCvc Шаблон (например, "###" для 3 цифр)
     * @return CVC-код (например, "123" для шаблона "###")
     */
    public static String getCvc(String numberStringCvc) {
        Faker faker = new Faker();
        return faker.numerify(numberStringCvc);
    }
}