package dev.akif.yama

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class PatchDataTest :
    WordSpec({
        "Converting patch data to a map" should {
            "only include present values in the map" {
                val addressPatch = PatchAddress(street = "Patched Street".present(), postalCode = Omitted, number = Omitted)
                val userPatch = PatchUser(email = "test@test.com", name = Omitted, address = addressPatch.present())

                userPatch.toMap() shouldBe
                    mapOf(
                        "email" to "test@test.com",
                        "address" to addressPatch
                    )
            }

            "throw exception when non-null property is given a null value" {
                @Suppress("UNCHECKED_CAST")
                val e =
                    shouldThrow<CannotPatchToNullException> {
                        val street = Present::class.constructors.first().call(null) as Omittable<String>
                        PatchAddress(street = street, postalCode = Omitted, number = Omitted).toMap()
                    }
                e.name shouldBe "street"
            }
        }
    })
