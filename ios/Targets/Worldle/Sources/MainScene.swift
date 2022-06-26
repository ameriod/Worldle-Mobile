import Resolver
import SVGView
import SwiftUI

@main
struct MainScene: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            GameView()
        }
    }
}

struct GameView: View {

    @InjectedObject var viewModel: ViewModel

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
                            share(state: state)
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

    func share(state: GameViewModelState) {
        let textToShare = "\(Bundle.main.appName) \(state.guesses.count)/5 - \(state.hasWonGame ? "Won" : "Lost"): \(state.sharePercent)% "
        let activityVC = UIActivityViewController(activityItems: [textToShare], applicationActivities: nil)
        UIApplication.shared.keyWindow?.rootViewController?.present(activityVC, animated: true, completion: nil)
    }
}

struct GuessView: View {

    var guess: Guess

    var body: some View {
        HStack {
            Text(guess.country.name)
            Spacer()
            Text(guess.getDistanceFrom())
            Image(systemName: guess.imageName)
                .foregroundColor(.blue)
                .rotationEffect(.degrees(Double(guess.direction.rotation)))
            Text("\(guess.proximityPercent)%")
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct SuggestionView: View {

    var suggestion: Country
    var input: String
    var action: () -> Void

    var body: some View {
        HighlightText(text: suggestion.name, match: input, hightlightColor: .blue)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
            .onTapGesture(perform: action)
    }
}

struct HighlightText: View {

    var text: String
    var match: String
    var hightlightColor: Color

    var body: some View {
        StyledText(verbatim: text)
            .style(.foregroundColor(hightlightColor), ranges: {
                [
                    $0.range(of: match),
                    $0.range(of: match.lowercased()),
                ]
            })
            .style(.bold(), ranges: {
                [
                    $0.range(of: match),
                    $0.range(of: match.lowercased()),
                ]
            })
    }
}

class ViewModel: ObservableObject {

    private let commonVM: GameViewModelCommon
    private let scopeProvider: ScopeProvider
    @Published var state: GameViewModelState?
    @Published var input = ""

    init(
        commonVM: GameViewModelCommon,
        scopeProvider: ScopeProvider
    ) {
        self.commonVM = commonVM
        self.scopeProvider = scopeProvider
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
        code + "_suggestion"
    }
}

extension Guess: Identifiable {

    public var id: String {
        country.code + "_guess"
    }
}

extension Guess {

    var imageName: String {
        if direction.isDirection {
            return "arrow.up"
        }
        return "star.fill"
    }
}
