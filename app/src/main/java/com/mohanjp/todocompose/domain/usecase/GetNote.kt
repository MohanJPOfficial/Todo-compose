package com.mohanjp.todocompose.domain.usecase

import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.repository.NoteRepository
import javax.inject.Inject

class GetNote @Inject constructor(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(id: Int): Note? {
        return repository.getNoteById(id)
    }
}