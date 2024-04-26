package com.mohanjp.todocompose.di

import android.app.Application
import androidx.room.Room
import com.mohanjp.todocompose.data.datasource.NoteDatabase
import com.mohanjp.todocompose.data.repository.NoteRepositoryImpl
import com.mohanjp.todocompose.domain.repository.NoteRepository
import com.mohanjp.todocompose.domain.usecase.AddNote
import com.mohanjp.todocompose.domain.usecase.DeleteNote
import com.mohanjp.todocompose.domain.usecase.GetNote
import com.mohanjp.todocompose.domain.usecase.GetNotes
import com.mohanjp.todocompose.domain.usecase.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }
}