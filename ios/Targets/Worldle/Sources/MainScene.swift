import common
import SwiftUI

@main
struct MainScene: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    let repository = Repository(fileLoader: BundleFileLoader(), historyDatabase: PlatformKt.createHistoryDatabase())

    var body: some Scene {
        WindowGroup {
            GameView(ViewModel(repository: repository))
        }
    }
}

struct GameView: View {

    @ObservedObject var viewModel: ViewModel

    init(_ viewModel: ViewModel) {
        self.viewModel = viewModel
    }

    var body: some View {
        Spacer()
            .background(Color.red)
    }
}

class BundleFileLoader: FileLoader {
    func getStringFromFile(fileName: String) -> String {
        let filepath = Bundle.main.resourcePath! + "/\(fileName)"
        do {
            return try String(contentsOfFile: filepath)
        } catch {
            fatalError("Error contents could not be loaded for \(fileName)")
        }
    }
}

class ViewModel: ObservableObject {

    private let commonVM: GameViewModelCommon
    private let scopeProvider: ScopeProvider = SharedGlobalScopeProvider()

    init(repository: Repository) {
        commonVM = GameViewModelCommon(repository: repository, scope: scopeProvider.scope, date: "1/11/1911")
        createOptionalPublisher(flowWrapper: PlatformKt.getState(commonVM, scope: scopeProvider))
            .sink(receiveCompletion: {
                print("Error \($0)")

            }, receiveValue: {
                print("Value \($0)")
            })
            .withLifetime(of: self)
    }

    func onGuessDone() {
        commonVM.onGuessDone()
    }

    func onGuessUpdated(input: String) {
        commonVM.onGuessUpdated(input: input)
    }

    func onSuggestionSelected(suggestion: Country) {
        commonVM.onSuggestionSelected(suggestion: suggestion)
    }

    func resetGame() {
        commonVM.resetGame()
    }

}
