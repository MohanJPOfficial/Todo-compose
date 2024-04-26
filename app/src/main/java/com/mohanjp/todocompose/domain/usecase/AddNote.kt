package com.mohanjp.todocompose.domain.usecase

import com.mohanjp.todocompose.data.datasource.InvalidNoteException
import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.repository.NoteRepository
import javax.inject.Inject

class AddNote @Inject constructor(
    private val repository: NoteRepository
) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if(note.title.isBlank()) {
            throw InvalidNoteException("The title of the note can't be empty.")
        }
        if(note.content.isBlank()) {
            throw InvalidNoteException("The content of the note can't be empty.")
        }
        repository.insertNote(note)
    }
}