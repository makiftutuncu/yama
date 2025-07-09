package dev.akif.yama

data class Address(
    val street: String,
    val postalCode: String?,
    val number: String?
)

data class User(
    val email: String,
    val name: String?,
    val address: Address?
)

data class PatchAddress(
    val street: Omittable<String>,
    val postalCode: Omittable<String?>,
    val number: Omittable<String?>
) : PatchData<PatchAddress>

data class PatchUser(
    val email: String,
    val name: Omittable<String?>,
    val address: Omittable<PatchAddress?>
) : PatchData<PatchUser>
