package com.main.themovieapp.di

import com.main.themovieapp.presentation.detail.DetailViewModel
import com.main.themovieapp.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { parameters -> DetailViewModel(get(), movieId = parameters.get()) }
}