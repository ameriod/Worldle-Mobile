// From: https://github.com/touchlab/SwiftCoroutines
package com.nordeck.app.worldle.common

import co.touchlab.stately.freeze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SuspendWrapper<T : Any>(
    private val scope: ScopeProvider,
    private val suspender: suspend () -> T
) {
    init {
        freeze()
    }

    fun subscribe(
        onSuccess: (item: T) -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = scope.scope.launch {
        try {
            onSuccess(suspender().freeze())
        } catch (error: Throwable) {
            onThrow(error.freeze())
        }
    }.freeze()
}

class NullableSuspendWrapper<T>(
    private val scope: ScopeProvider,
    private val suspender: suspend () -> T
) {
    init {
        freeze()
    }

    fun subscribe(
        onSuccess: (item: T) -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = scope.scope.launch {
        try {
            onSuccess(suspender().freeze())
        } catch (error: Throwable) {
            onThrow(error.freeze())
        }
    }.freeze()
}

class FlowWrapper<T : Any>(
    private val scope: ScopeProvider,
    private val flow: Flow<T>
) {
    init {
        freeze()
    }

    fun subscribe(
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = flow
        .onEach { onEach(it.freeze()) }
        .catch { onThrow(it.freeze()) } // catch{} before onCompletion{} or else completion hits rx first and ends stream
        .onCompletion { onComplete() }
        .launchIn(scope.scope)
        .freeze()
}

class NullableFlowWrapper<T>(
    private val scope: ScopeProvider,
    private val flow: Flow<T>
) {
    init {
        freeze()
    }

    fun subscribe(
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = flow
        .onEach { onEach(it.freeze()) }
        .catch { onThrow(it.freeze()) } // catch{} before onCompletion{} or else completion hits rx first and ends stream
        .onCompletion { onComplete() }
        .launchIn(scope.scope)
        .freeze()
}

interface ScopeProvider {
    val scope: CoroutineScope
}

class SharedMainScopeProvider : ScopeProvider {
    override val scope: CoroutineScope
        get() = MainScope()
}

class SharedGlobalScopeProvider : ScopeProvider {
    override val scope: CoroutineScope
        get() = GlobalScope
}
