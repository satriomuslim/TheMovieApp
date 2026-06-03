package com.main.themovieapp.di

import com.main.themovieapp.data.repository.MovieRepositoryImpl
import com.main.themovieapp.domain.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
}