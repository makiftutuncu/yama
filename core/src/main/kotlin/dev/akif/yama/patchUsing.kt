package dev.akif.yama

fun <Source : Any, Patch : PatchData<Patch>> Source.patchUsing(
    data: Patch,
    build: PatchingContext<Source, Patch>.(Source) -> Source
): Source =
    PatchingContext(this, data).build(this)
