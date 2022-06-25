package com.nordeck.app.worldle.common

import com.nordeck.app.worldle.common.model.FileLoader
import com.nordeck.app.worldle.common.model.GameViewModelCommon
import com.nordeck.app.worldle.common.model.Repository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule)
}

fun initKoin() = initKoin {}

val commonModule = module {

    single<FileLoader> {
        BundleFileLoader()
    }

    single<HistoryDatabase> {
        createHistoryDatabase()
    }

    single<Repository> {
        Repository(
            fileLoader = get(),
            historyDatabase = get()
        )
    }

    single<ScopeProvider>(named(ScopeProviderQualifier.GLOBAL)) {
        SharedGlobalScopeProvider()
    }

    single<ScopeProvider>(named(ScopeProviderQualifier.MAIN)) {
        SharedMainScopeProvider()
    }

    factory<GameViewModelCommon> {
        GameViewModelCommon(
            repository = get(),
            scope = get<ScopeProvider>(qualifier = named(ScopeProviderQualifier.GLOBAL)).scope
        )
    }
}

enum class ScopeProviderQualifier {
    MAIN, GLOBAL
}

class GameViewModelHelper : KoinComponent {

    private val viewModel: GameViewModelCommon by inject()

    fun viewModel(): GameViewModelCommon = viewModel
}

class ScopeProviderHelper : KoinComponent {

    private val main: ScopeProvider by inject(named(ScopeProviderQualifier.MAIN))
    private val global: ScopeProvider by inject(named(ScopeProviderQualifier.GLOBAL))

    fun main(): ScopeProvider = main

    fun global(): ScopeProvider = global
}
