package dev.akif.yama

/**
 * Convenience method to patch this object using given data
 *
 * Example:
 * ```kotlin
 * data class Foo(val bar: String)
 *
 * data class FooPatch(val bar: Omittable<String>) : PatchData<Foo>
 *
 * val foo: Foo = Foo("test")
 * val data: FooPatch = FooPatch("test2")
 *
 * val newFoo: Foo = foo.patchUsing(data) {
 *     // `patched` method is only available within this `patchUsing` block
 *     // `[this]::` refers to `foo` **within** the `patched` lambda call
 *     val patchedBar: String = patched { ::bar }
 *
 *     // `it` refers to `foo` **within** `patchUsing` block
 *     it.copy(bar = patchedBar)
 * }
 * ```
 *
 * @param data Data to be used for the patch
 * @param build Block to define how to do the patching
 *
 * @param Source Type of the object to patch
 * @param Patch Type of the data used in patching
 *
 * @receiver Object being patched
 *
 * @return Patched object
 *
 * @see [PatchData]
 * @see [PatchingContext]
 * @see [PatchingContext.patched]
 */
fun <Source : Any, Patch : PatchData<Patch>> Source.patchUsing(
    data: Patch,
    build: PatchingContext<Source, Patch>.(Source) -> Source
): Source = PatchingContext(this, data).build(this)
