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

    var body: some View {
        Spacer()
            .background(Color.red)
    }
}
