package dev.akif.example.user.domain

import dev.akif.yama.Omittable
import dev.akif.yama.PatchData
import java.time.LocalDate

data class PatchUserRequest(
    val name: Omittable<String>,
    val dateOfBirth: Omittable<LocalDate?>
) : PatchData<PatchUserRequest>
