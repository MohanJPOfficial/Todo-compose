package com.mohanjp.todocompose.presentation.addnote

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohanjp.todocompose.data.datasource.InvalidNoteException
import com.mohanjp.todocompose.data.datasource.Note
import com.mohanjp.todocompose.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _noteTitle = mutableStateOf(NoteTextFieldState(
        hint = "Enter title..."
    ))
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(NoteTextFieldState(
        hint = "Enter content..."
    ))
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableIntStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if(noteId != -1) {
                viewModelScope.launch {
                    noteUseCases.getNote(noteId)?.let { note ->
                        currentNoteId = noteId
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: UiAction) {
        when(uiAction) {
            is UiAction.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = uiAction.value
                )
            }
            is UiAction.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !uiAction.focusState.isFocused &&
                        noteTitle.value.text.isBlank()
                )
            }
            is UiAction.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(
                    text = uiAction.value
                )
            }
            is UiAction.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !uiAction.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }
            is UiAction.ChangeColor -> {
                _noteColor.intValue = uiAction.color
            }
            UiAction.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch(e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }
}

sealed interface UiEvent {
    data class ShowSnackBar(val message: String): UiEvent
    data object SaveNote: UiEvent
}

sealed interface UiAction {
    data class EnteredTitle(val value: String): UiAction
    data class ChangeTitleFocus(val focusState: FocusState): UiAction
    data class EnteredContent(val value: String): UiAction
    data class ChangeContentFocus(val focusState: FocusState): UiAction
    data class ChangeColor(val color: Int): UiAction
    data object SaveNote: UiAction
}

data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)