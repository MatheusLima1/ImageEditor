package com.matheuslima.imageeditor.di

import com.matheuslima.imageeditor.utils.PermissionResolver
import com.matheuslima.imageeditor.utils.PermissionResolverImpl
import org.koin.dsl.module

val utilModule = module {
    single<PermissionResolver> { PermissionResolverImpl() }
}