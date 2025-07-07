package dev.akif.yama

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

private val presentTestValues = listOf(true, 1, 2L, 3.0, 'a', "foo", null as String?)

private val alternativeTestValues = listOf(false, 0, 0L, 0.0, ' ', "", "null")

class OmittableTest : WordSpec({
    "Omittable" When {
        "constructing" should {
            "return present values" {
                presentTestValues.forEach {
                    Omittable.present(it).apply { isPresent shouldBe true }
                }
            }

            "return present values using extension" {
                presentTestValues.forEach {
                    it.present().isPresent shouldBe true
                }
            }

            "return omitted value" { Omittable.omitted.apply { isOmitted shouldBe true } }
        }

        "getting or providing an alternative" should {
            "return the value when it is present" {
                (presentTestValues zip alternativeTestValues).forEach { (value, alternative) ->
                    Omittable.present(value).getOrElse { alternative } shouldBe value
                }
            }

            "return the alternative when it is omitted" {
                Omitted.getOrElse { "default" } shouldBe "default"
            }
        }

        "getting or throwing" should {
            "return the value when it is present" {
                presentTestValues.forEach { Omittable.present(it).getOrThrow() shouldBe it }
            }

            "throw NoSuchElementException when it is omitted" {
                val error = shouldThrow<NoSuchElementException> { Omitted.getOrThrow() }
                error.message shouldContain "omitted value"
            }
        }

        "checking if present" should {
            "return true for present values" {
                presentTestValues.forEach { Omittable.present(it).isPresent shouldBe true }
            }

            "return false for omitted value" { Omitted.isPresent shouldBe false }
        }

        "checking if omitted" should {
            "return false for present values" {
                presentTestValues.forEach { Omittable.present(it).isOmitted shouldBe false }
            }

            "return true for omitted value" { Omitted.isOmitted shouldBe true }
        }

        "running a side effect when present" should {
            "run for present values" {
                var counter = 0
                presentTestValues.forEach { Omittable.present(it).whenPresent { counter++ } }
                counter shouldBe presentTestValues.size
            }

            "do nothing for omitted value" {
                var counter = 0
                Omitted.whenPresent { counter++ }
                counter shouldBe 0
            }
        }

        "running a side effect when omitted" should {
            "run for omitted value" {
                var counter = 0
                Omitted.whenOmitted { counter++ }
                counter shouldBe 1
            }

            "do nothing for present values" {
                var counter = 0
                presentTestValues.forEach { Omittable.present(it).whenOmitted { counter++ } }
                counter shouldBe 0
            }
        }
    }
})
