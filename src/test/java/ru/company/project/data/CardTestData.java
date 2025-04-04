package ru.company.project.data;

/**
 * Тестовые данные для валидации платежей и кредитов по картам
 *
 * <p><b>Внимание:</b> Все данные являются тестовыми и не должны использоваться в production-среде.</p>
 *
 * <p>Структура данных:</p>
 * <ul>
 *   <li>Статусы карт</li>
 *   <li>Номера карт (различные сценарии)</li>
 *   <li>Данные по датам (месяц/год)</li>
 *   <li>Данные владельца карты</li>
 *   <li>CVC/CVV коды</li>
 *   <li>Системные сообщения</li>
 * </ul>
 */
public class CardTestData {

    // =============================================
    // 1. Статусы карт
    // =============================================
    /** Статус одобренной карты */
    public static final String STATUS_APPROVED = "APPROVED";
    /** Статус отклонённой карты */
    public static final String STATUS_DECLINED = "DECLINED";

    // =============================================
    // 2. Тестовые номера карт
    // =============================================
    /** Номер карты с APPROVED-статусом (тестовый) */
    public static final String APPROVED_CARD = "4444 4444 4444 4441";
    /** Номер карты с DECLINED-статусом (тестовый) */
    public static final String DECLINED_CARD = "4444 4444 4444 4442";
    /** Пустое поле номера карты */
    public static final String EMPTY_CARD = "";
    /** Заведомо невалидный номер карты */
    public static final String INVALID_CARD = "0000 0000 0000 0000";
    /** Короткий номер карты (15 цифр вместо 16) */
    public static final String SHORT_CARD = "4444 4444 4444 444";

    // =============================================
    // 3. Данные по месяцам
    // =============================================
    /** Пустой месяц */
    public static final String EMPTY_MONTH = "";
    /** Несуществующий месяц (13) */
    public static final String INVALID_MONTH = "13";
    /** Нулевой месяц (00) */
    public static final String MONTH_OF_ZEROS = "00";
    /** Текущий месяц (генерируется динамически в {@link DataHelper#getMonth(Object)}) */
    public static final int CURRENT_MONTH = 0;

    // =============================================
    // 4. Данные по годам
    // =============================================
    /** Текущий год (генерируется динамически в {@link DataHelper#getYear(Object)}) */
    public static final int CURRENT_YEAR = 0;
    /** Пустой год */
    public static final String EMPTY_YEAR = "";

    // =============================================
    // 5. Данные владельца карты
    // =============================================
    /** Пустое поле владельца */
    public static final String EMPTY_HOLDER = "";
    /** Владелец со спецсимволами (недопустимыми) */
    public static final String HOLDER_SPECIAL_CHARS = "Ivan@ Ivan#";
    /** Слишком длинное имя владельца (>50 символов) */
    public static final String LONG_HOLDER = "Longholderlongholderlongholderslongholderlongholderlongholderss";
    /** Имя с цифрами (недопустимо) */
    public static final String HOLDER_NUMBER = "Ivan123 Ivan123";
    /** Только имя без фамилии, генерируется динамически в {@link DataHelper#getHolderNameFormat(String)} */
    public static final String FIRST_NAME_HOLDER = "FIRST_NAME_HOLDER";
    /** Валидное имя и фамилия (латиница), генерируется динамически в {@link DataHelper#getHolderNameFormat(String)} */
    public static final String VALID_HOLDER = "VALID_HOLDER";
    /** Кириллическое имя и фамилия, генерируется динамически в {@link DataHelper#getHolderNameFormat(String)} */
    public static final String HOLDER_CYRILLIC = "HOLDER_CYRILLIC";

    // =============================================
    // 6. CVC/CVV коды
    // =============================================
    /** Пустой CVC код */
    public static final String EMPTY_CVC = "";
    /** Валидный CVC код (3 цифры), генерируется динамически в {@link DataHelper#getCvc(String)} */
    public static final String VALID_CVC = "###";
    /** Короткий CVC код (2 цифры), генерируется динамически в {@link DataHelper#getCvc(String)} */
    public static final String CVC_SHORT = "##";

    // =============================================
    // 7. Системные сообщения
    // =============================================
    /** Сообщение об отказе банка */
    public static final String BANK_TRANSACTION_REFUSAL = "Ошибка! Банк отказал в проведении операции.";
    /** Сообщение об успешной операции */
    public static final String BANK_TRANSACTION_APPROVAL = "Успешно Операция одобрена Банком.";
}