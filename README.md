# yama `📦 → 🎁` [![](https://img.shields.io/badge/docs-0.1.0-brightgreen.svg?style=for-the-badge&logo=kotlin&color=0095d5&labelColor=333333)](https://javadoc.io/doc/dev.akif/yama-docs/latest/index.html)

yama is a small Kotlin library providing a convenient but opinionated way of patching objects. Its highlights are:

* [Omittable](core/src/main/kotlin/dev/akif/yama/Omittable.kt) type to represent values that may be omitted
  * This useful in patches because it allows users to provide only the data to be updated so the rest of the properties are not modified. It makes it explicitly possible to distinguish between some data being `null` or omitted.
* [patchUsing](core/src/main/kotlin/dev/akif/yama/PatchUsing.kt) DSL with [patched](core/src/main/kotlin/dev/akif/yama/PatchingContext.kt#L44) method
* Integration and auto-configurations for Spring Framework for json deserialization and OpenAPI generation

yama uses Kotlin reflection to provide its capabilities.

The word yama is short for _**Y**et **A**nother **M**utation **A**bstraction_. Coincidentally, it means "patch" in Turkish.

## Table of Contents

1. [Modules](#modules)
2. [Example](#example)
3. [Development & Testing](#development--testing)
4. [Limitations](#limitations)
5. [Releases](#releases)
6. [Contributing](#contributing)
7. [License](#license)

## Modules

This project consists of following modules.

| Module                | Description                                           |
|-----------------------|-------------------------------------------------------|
| [yama-core](core)     | Core implementation for patching                      |
| [yama-spring](spring) | Spring Framework integration with auto-configurations |

## Example

Here's a quick example of how to use yama:

```kotlin
import dev.akif.yama.*

// Given some data models

data class User(val id: Long, val name: String, val dateOfBirth: LocalDate, val address: Address?)
data class Address(val street: String, val number: Int?)

// Define some patch data

data class UserPatch(
  // `name` is always required
  val name: String,
  // `dateOfBirth` can be omitted or can be provided (but not as a null value)
  val dateOfBirth: Omittable<LocalDate>,
  // `address` can be omitted or can be provided (can also be null when provided)
  val address: Omittable<Address?>
): PatchData<UserPatch> // Need to implement `PatchData` as a marker

data class AddressPatch(val street: Omittable<String>, val number: Omittable<Int?>): PatchData<AddressPatch>

// Do the patching somewhere in your "business logic" code

fun getPatchedUser(user: User, data: UserPatch): User {
  return user.patchUsing(data) { u ->
    val newName = patched { ::name }
    val newDateOfBirth = patched { ::dateOfBirth }

    val newAddress = if (data.address.isOmitted) {
      u.address
    } else {
      u.address.patchUsing(data.address.getOrThrow()) { a ->
        val newStreet = patched { ::street }
        val newNumber = patched { ::number }

        if (a == null) {
          // Address was null before but a patch is provided. So, create one.
          Address(street = newStreet, number = newNumber)
        } else {
          a.copy(street = newStreet, number = newNumber)
        }
      }
    }

    u.copy(name = newName, dateOfBirth = newDateOfBirth, address = newAddress)
  }
}
```

In this example, `getPatchedUser` will return a modified copy of the `user`. This copy will have the values from properties that are present in `data`. For example, for the following patch data

```json
{
  "name": "Akif",
  "address": {
    "street": "Test",
    "number": null
  }
}
```

means

* `name` of the user will be set to `"Akif"`
* `dateOfBirth` of the user will not be changed
* `street` of the user's address will be set to `"Test"`
* `number` of the user's address will be set to `null`

There is also an example Spring Boot project in [example](example).

## Development & Testing

This project is written in Kotlin and built with Gradle. Following tasks can be used during development and testing.

* Run `./gradlew clean` to clean all build outputs
* Run `./gradlew build` to only build the application
* Run `./gradlew check` to run all checks, including tests

To test your changes during development:

1. Bump your `version` in [kotlin-jvm.gradle.kts](buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts) and append `-SNAPSHOT`.
2. Run `./gradlew publishToMavenLocal` to publish artifacts with your changes to your local Maven repository.
3. In the project you include yama, update the version of yama dependencies to your new snapshot version. Make sure you have `mavenLocal()` in the `repositories` definition in your build definition for this to work.

## Limitations

* Cannot work with `private` marked `PatchData` types
* Types implementing `PatchData` needs to provide properties in their primary constructor (e.g. use a `data class` for simplicity)
* Names of properties in `PatchData` type and the data model need to match

## Releases

Artifacts of this project are published to Maven Central along with their sources and documentations. They are versioned according to [semantic versioning](https://semver.org). CI/CD is managed by GitHub Actions. See [.github](.github) for more details on these workflows.

## Contributing

All contributions are welcome, including requests to highlight your project using this library. Please feel free to send a pull request. Thank you.

## License

This project is licensed with MIT License. See [LICENSE](LICENSE) for more details.
