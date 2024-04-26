package com.mohanjp.todocompose.featurenote.domain.usecase

import com.google.common.truth.Truth
import com.mohanjp.todocompose.data.datasource.InvalidNoteException
import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.repository.NoteRepository
import com.mohanjp.todocompose.domain.usecase.AddNote
import com.mohanjp.todocompose.featurenote.data.repository.FakeRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddNoteTest {

    private lateinit var addNote: AddNote
    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        repository = FakeRepository()
        addNote = AddNote(repository)
    }

    @Test
    fun `Throw exception when title is blank`() = runBlocking {
        val note = Note(
            title = "",
            content = "something",
            timestamp = 0,
            color = 0
        )

        try {
            addNote(note)
        } catch (e: InvalidNoteException) {
            Truth.assertThat(e.message).isEqualTo("The title of the note can't be empty.")
        }
    }

    @Test
    fun `Throw exception when content is blank`() = runBlocking {
        val note = Note(
            title = "something",
            content = "",
            timestamp = 0,
            color = 0
        )

        try {
            addNote(note)
        } catch (e: InvalidNoteException) {
            Truth.assertThat(e.message).isEqualTo("The content of the note can't be empty.")
        }
    }

    @Test
    fun `Check if the note was successfully inserted`() = runBlocking {
        val note = Note(
            title = "title",
            content = "content",
            timestamp = 0,
            color = 0,
            id = 7
        )

        addNote(note)

        Truth.assertThat(repository.getNoteById(note.id!!)).isNotNull()
    }
}