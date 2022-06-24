import common
import SVGView
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
        if let state = viewModel.state {
            ScrollView {
                LazyVStack(spacing: 8) {
                    SVGView(contentsOf: state.countryToGuess.svgPath)
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 300, alignment: .top)
                    ForEach(state.guesses) {
                        GuessView(guess: $0)
                    }
                    if state.hasWonGame || state.hasLostGame {
                        if state.hasLostGame {
                            Text(state.countryToGuess.name)
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, alignment: .center)
                                .padding()
                                .background(.black)
                                .padding(.horizontal, -16)
                        }
                        Button("Share") {
                            // TODO share
                        }
                        #if DEBUG
                        Button("Reset") {
                            viewModel.resetGame()
                        }
                        #endif
                    } else {
                        TextField("Country, teritory...", text: $viewModel.input)
                        ForEach(state.suggestions) { suggestion in
                            SuggestionView(suggestion: suggestion, input: viewModel.input) {
                                viewModel.onSuggestionSelected(suggestion: suggestion)
                            }
                        }
                    }

                }
                .padding(.horizontal, 16)
            }
            Spacer()
        } else {
            Text("Loading...")
        }
    }
}

struct GuessView: View {

    var guess: Guess

    var body: some View {
        HStack {
            Text(guess.country.name)
            Spacer()
            Text(guess.formattedDistance)
            Image(systemName: guess.imageName)
                .foregroundColor(.blue)
                .rotationEffect(.degrees(Double(guess.direction.rotation)))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct SuggestionView: View {

    var suggestion: Country
    var input: String
    var action: () -> Void

    var body: some View {
        // TODO highlight
        Text(suggestion.name)
            // HighlightedText(text: suggestion.name, matching: input, hightlightColor: .blue)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
            .onTapGesture(perform: action)
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
    @Published var state: GameViewModelState?
    @Published var input = ""

    init(repository: Repository) {
        commonVM = GameViewModelCommon(repository: repository, scope: scopeProvider.scope, date: "1/11/1911")
        createOptionalPublisher(flowWrapper: PlatformKt.getState(commonVM, scope: scopeProvider))
            .removeDuplicates()
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
            .sink(receiveCompletion: {
                Forest.e("Error with state flow: \($0)")
            }, receiveValue: { [unowned self] state in
                self.state = state
                if state?.guessInput != input {
                    input = state?.guessInput ?? ""
                }
            })
            .withLifetime(of: self)

        $input.sink { [unowned self] input in
            self.onGuessUpdated(input: input)
        }
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

extension Country {

    var svgPath: URL {
        Bundle.main.resourceURL!.appendingPathComponent(code.lowercased()).appendingPathExtension("svg")
    }
}

extension Country: Identifiable {

    public var id: String {
        code
    }
}

extension Guess: Identifiable {

    public var id: String {
        country.code
    }
}

extension Guess {

    var formattedDistance: String {
        if NSLocale.current.usesMetricSystem {
            return "\(ConversionMath.companion.metersToKilometers(meters: distanceFromMeters)) km"
        }
        return "\(ConversionMath.companion.metersToMiles(meters: distanceFromMeters)) mi"
    }

    var imageName: String {
        if direction.isDirection {
            return "arrow.up"
        }
        return "star.fill"
    }
}
