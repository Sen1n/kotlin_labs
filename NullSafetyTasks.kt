data class Address(
    val street: String,
    val city: String?,
    val postalCode: String?
)

data class Client(
    val id: Int,
    val name: String,
    val email: String?,
    val address: Address?,
    val extraData: Any?
)

val clientList = listOf(
    Client(
        id = 1,
        name = "Олена",
        email = "olena@example.com",
        address = Address("вул. Саксаганського, 10", "Київ", "01033"),
        extraData = "VIP-клієнт"
    ),
    Client(
        id = 2,
        name = "Богдан",
        email = null,
        address = Address("вул. Городоцька, 45", "Львів", null),
        extraData = 42
    ),
    Client(
        id = 3,
        name = "Марія",
        email = "maria@example.com",
        address = null,
        extraData = null
    ),
    Client(
        id = 4,
        name = "Дмитро",
        email = null,
        address = Address("вул. Соборна, 1", null, null),
        extraData = "Очікує дзвінка"
    )
)

// Завдання 1
fun getShippingLabel(client: Client): String {
    val addr = client.address ?: return "Самовивіз: Клієнт ${client.name} не надав адреси"
    val city = addr.city ?: "Місто не вказано"
    val postalCode = addr.postalCode ?: "Індекс невідомий"
    return "Адреса ${client.name}: ${addr.street}, $city, $postalCode"
}

// Завдання 2
fun printClientNote(client: Client) {
    // Безпечне приведення до String. Якщо extraData не String (наприклад, Int або null) — спрацює ?:
    val note = client.extraData as? String ?: "Додаткові примітки відсутні"
    println("Клієнт ${client.name} -> Примітка: $note")
}

// Завдання 4
fun getClientEmailOrThrow(client: Client): String {
    // Використовуємо стандартну функцію requireNotNull для валідації вхідних даних
    return requireNotNull(client.email) {
        "Клієнт з ID ${client.id} не має електронної пошти!"
    }
}

// Завдання 5
fun forceGetPostalCode(client: Client): String {
    // !! використовується лише тоді, коли ми архітектурно гарантуємо наявність значення, 
    // але компілятор не здатний зробити smart cast (наприклад, через мутабельність поля з іншого потоку).
    // У більшості бізнес-логіки це anti-pattern, якого слід уникати на користь ?. та ?:.
    return client.address!!.postalCode!!
}

fun main() {
    println("--- Завдання 1 ---")
    println(getShippingLabel(clientList[0]))
    println(getShippingLabel(clientList[1]))
    println(getShippingLabel(clientList[2]))

    println("\n--- Завдання 2 ---")
    clientList.forEach { printClientNote(it) }

    println("\n--- Завдання 3 ---")
    val validEmails = clientList.map { it.email }.filterNotNull()
    val shortestLength = validEmails.minOfOrNull { it.length } ?: 0
    println("База email для розсилки: $validEmails")
    println("Довжина найкоротшого email: $shortestLength")

    println("\n--- Завдання 4 ---")
    try {
        getClientEmailOrThrow(clientList[1])
    } catch (e: IllegalArgumentException) {
        println("Перехоплено виняток: ${e.message}")
    }

    println("\n--- Завдання 5 ---")
    println("Індекс клієнта Олена: ${forceGetPostalCode(clientList[0])}")
    try {
        forceGetPostalCode(clientList[1])
    } catch (e: NullPointerException) {
        println("Перехоплено очікуваний NPE: null")
    }
}
