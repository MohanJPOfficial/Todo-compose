package com.mohanjp.todocompose.presentation.notes

import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.util.NoteOrder
import com.mohanjp.todocompose.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)

sealed interface NotesEvent {
    data class Order(val noteOrder: NoteOrder): NotesEvent
    data class DeleteNote(val note: Note): NotesEvent
    data object RestoreNote: NotesEvent
    data object ToggleOrderSection: NotesEvent
}