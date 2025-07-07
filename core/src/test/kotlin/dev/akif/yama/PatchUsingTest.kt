package dev.akif.yama

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class PatchUsingTest: WordSpec({
    "Patching" should {
        "return an object whose properties are only modified if they are present in the patch" {
            val user = User(
                email = "test@test.com",
                name = "Test User",
                address = Address(street = "Test Street", postalCode = "1234AB", number = "5")
            )
            val addressPatch = PatchAddress(
                street = "Patched Street".present(),
                postalCode = Omitted,
                number = null.present()
            )
            val userPatch = PatchUser(
                email = "patched@test.com",
                name = Omitted,
                address = addressPatch.present()
            )

            val patched = user.patchUsing(userPatch) { u ->
                u.copy(
                    email = patched { ::email },
                    name = patched { ::name },
                    address = u.address?.patchUsing(addressPatch) { a ->
                        a.copy(
                            street = patched { ::street },
                            postalCode = patched { ::postalCode },
                            number = patched { ::number }
                        )
                    }
                )
            }

            val expected = User(
                email = "patched@test.com",
                name = "Test User",
                address = Address(street = "Patched Street", postalCode = "1234AB", number = null)
            )

            patched shouldBe expected
        }
    }
})
