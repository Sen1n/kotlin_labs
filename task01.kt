interface Refundable {
    fun refund(amount: Double): Boolean
}

interface EReceiptable {
    val receiptEmail: String
    
    fun sendReceipt() {
        println(" [E-Receipt] Електронний чек надіслано на пошту: $receiptEmail")
    }
}

abstract class PaymentMethod(
    val transactionId: String,
    val amount: Double,
    val currency: String = "UAH"
) {
    init {
        // перевіряємо, щоб сума завжди була додатною
        require(amount > 0.0) { "сума платежу повинна бути більшою за нуль" }
    }

    // кожен тип платежу реалізує свою логіку списання
    abstract fun processPayment(): Boolean

    // базовий вивід інформації, який можна розширити
    open fun printDetails() {
        println("Транзакція #$transactionId | Сума: $amount $currency")
    }
}

class CreditCardPayment(
    transactionId: String,
    amount: Double,
    currency: String = "UAH",
    val cardNumber: String,
    override val receiptEmail: String
) : PaymentMethod(transactionId, amount, currency), Refundable, EReceiptable {

    override fun processPayment(): Boolean {
        println("Списання $amount $currency з картки (закінчується на ${cardNumber.takeLast(4)})... Успішно!")
        return true
    }

    override fun refund(amount: Double): Boolean {
        println("Повернення $amount $currency на картку ${cardNumber.takeLast(4)} виконано.")
        return true
    }

    override fun printDetails() {
        super.printDetails()
        println("  └ Метод: Банківська картка (**** ${cardNumber.takeLast(4)})")
    }
}

// криптоплатіж: підтримує повернення, але без електронних чеків
class CryptoPayment(
    transactionId: String,
    amount: Double,
    currency: String = "UAH",
    val cryptoAddress: String,
    val networkFee: Double
) : PaymentMethod(transactionId, amount, currency), Refundable {

    override fun processPayment(): Boolean {
        println("Відправка $amount $currency (+ комісія $networkFee) на гаманець ${cryptoAddress.take(6)}...${cryptoAddress.takeLast(4)}... Підтверджено!")
        return true
    }

    override fun refund(amount: Double): Boolean {
        // повертаємо кошти лише якщо сума більша за комісію мережі
        if (amount > networkFee) {
            println("Крипто-повернення ${amount - networkFee} $currency (з урахуванням комісії мережі) надіслано.")
            return true
        } else {
            println("Помилка: сума повернення менша за комісію мережі.")
            return false
        }
    }

    override fun printDetails() {
        super.printDetails()
        println("  └ Метод: Криптовалюта (Адреса: ${cryptoAddress.take(6)}...)")
    }
}

// оплата при отриманні: надсилає чек, але без онлайн-повернення
class CashOnDeliveryPayment(
    transactionId: String,
    amount: Double,
    currency: String = "UAH",
    val deliveryAddress: String,
    override val receiptEmail: String
) : PaymentMethod(transactionId, amount, currency), EReceiptable {

    override fun processPayment(): Boolean {
        println("Замовлення зареєстровано. Оплата $amount $currency буде здійснена кур'єру за адресою: $deliveryAddress.")
        return true
    }

    override fun printDetails() {
        super.printDetails()
        println("  └ Метод: Післяплата (Адреса: $deliveryAddress)")
    }

    override fun sendReceipt() {
        // перевизначаємо дефолтну поведінку для післяплати
        println(" [SMS & E-Receipt] Фіскальний чек зареєстровано та продубльовано на $receiptEmail")
    }
}

// пакетна обробка повернень
fun processMassRefund(items: List<PaymentMethod>, refundPercentage: Double) {
    var successCount = 0
    for (payment in items) {
        // динамічна перевірка типу та smart cast до refundable
        if (payment is Refundable) {
            val refundAmount = payment.amount * refundPercentage
            if (payment.refund(refundAmount)) {
                successCount++
            }
        }
    }
    println("Успішно оброблено повернень: $successCount з ${items.size} транзакцій.")
}

fun main() {
    // поліморфна колекція різних типів платежів
    val payments: List<PaymentMethod> = listOf(
        CreditCardPayment("TXN-001", 2500.0, "UAH", "4149499988884321", "client@gmail.com"),
        CryptoPayment("TXN-002", 10000.0, "UAH", "0x71C8A9D987B29C", 50.0),
        CashOnDeliveryPayment("TXN-003", 1200.0, "UAH", "м. Львів, Відділення №5", "receiver@lviv.ua")
    )

    println("==================================================")
    println("        ОБРОБКА ПОТОЧНИХ ТРАНЗАКЦІЙ")
    println("==================================================")
    
    for (payment in payments) {
        payment.printDetails()
        payment.processPayment()
        
        if (payment is EReceiptable) {
            payment.sendReceipt()
        }
        println("--------------------------------------------------")
    }

    println()
    println("==================================================")
    println("       МАСОВЕ ПОВЕРНЕННЯ КОШТІВ (50%)")
    println("==================================================")
    
    processMassRefund(payments, 0.50)
}
