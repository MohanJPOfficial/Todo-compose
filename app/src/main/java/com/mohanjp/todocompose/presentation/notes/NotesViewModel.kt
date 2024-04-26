package com.mohanjp.todocompose.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.usecase.NoteUseCases
import com.mohanjp.todocompose.domain.util.NoteOrder
import com.mohanjp.todocompose.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesUseCase: NoteUseCases
): ViewModel() {

    private val _uiState = mutableStateOf(NotesState())
    val uiState: State<NotesState> = _uiState

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when(event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    notesUseCase.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.Order -> {
                if(
                    uiState.value.noteOrder::class == event.noteOrder::class &&
                    uiState.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }

                getNotes(event.noteOrder)
            }
            NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    notesUseCase.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            NotesEvent.ToggleOrderSection -> {
                _uiState.value = uiState.value.copy(
                    isOrderSectionVisible = !uiState.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()

        getNotesJob = notesUseCase.getNotes(noteOrder).onEach { notes ->
            _uiState.value = uiState.value.copy(
                notes = notes,
                noteOrder = noteOrder
            )
        }.launchIn(viewModelScope)
    }
}