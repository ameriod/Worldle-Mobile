// From: https://github.com/touchlab/SwiftCoroutines

import Combine

func createPublisher<T>(flowWrapper: FlowWrapper<T>) -> AnyPublisher<T, KotlinError> {
    Deferred<Publishers.HandleEvents<PassthroughSubject<T, KotlinError>>> {
        let subject = PassthroughSubject<T, KotlinError>()
        let job = flowWrapper.subscribe { item in
            subject.send(item)
        } onComplete: {
            subject.send(completion: .finished)
        } onThrow: { error in
            subject.send(completion: .failure(KotlinError(error)))
        }
        return subject.handleEvents(receiveCancel: {
            job.cancel(cause: nil)
        })
    }.eraseToAnyPublisher()
}

func createOptionalPublisher<T>(flowWrapper: NullableFlowWrapper<T>) -> AnyPublisher<T?, KotlinError> {
    Deferred<Publishers.HandleEvents<PassthroughSubject<T?, KotlinError>>> {
        let subject = PassthroughSubject<T?, KotlinError>()
        let job = flowWrapper.subscribe { item in
            subject.send(item)
        } onComplete: {
            subject.send(completion: .finished)
        } onThrow: { error in
            subject.send(completion: .failure(KotlinError(error)))
        }
        return subject.handleEvents(receiveCancel: {
            job.cancel(cause: nil)
        })
    }.eraseToAnyPublisher()
}

func createFuture<T>(suspendWrapper: SuspendWrapper<T>) -> AnyPublisher<T, KotlinError> {
    Deferred<Publishers.HandleEvents<Future<T, KotlinError>>> {
        var job: Kotlinx_coroutines_coreJob?
        return Future { promise in
            job = suspendWrapper.subscribe(
                onSuccess: { item in promise(.success(item)) },
                onThrow: { error in promise(.failure(KotlinError(error))) }
            )
        }.handleEvents(receiveCancel: {
            job?.cancel(cause: nil)
        })
    }
    .eraseToAnyPublisher()
}

func createOptionalFuture<T>(suspendWrapper: NullableSuspendWrapper<T>) -> AnyPublisher<T?, KotlinError> {
    Deferred<Publishers.HandleEvents<Future<T?, KotlinError>>> {
        var job: Kotlinx_coroutines_coreJob?
        return Future { promise in
            job = suspendWrapper.subscribe(
                onSuccess: { item in promise(.success(item)) },
                onThrow: { error in promise(.failure(KotlinError(error))) }
            )
        }.handleEvents(receiveCancel: {
            job?.cancel(cause: nil)
        })
    }
    .eraseToAnyPublisher()
}

class KotlinError: LocalizedError {
    let throwable: KotlinThrowable
    init(_ throwable: KotlinThrowable) {
        self.throwable = throwable
    }

    var errorDescription: String? { throwable.message }
}
