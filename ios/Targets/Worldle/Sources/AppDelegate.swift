@_exported import common
import ForestKit
import UIKit

public typealias ForestKitLogging = ForestKit
public let Forest = ForestKitLogging.instance

class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        KoinKt.doInitKoin()
        #if DEBUG
        Forest.plant(
            ForestKit.PrintTree()
        )
        #endif
        return true
    }

}

private let scopeProviderHelper = ScopeProviderHelper()

let mainScope = scopeProviderHelper.main()
let globalScope = scopeProviderHelper.global()

extension UIApplication {

    var keyWindow: UIWindow? {
        // Get connected scenes
        UIApplication.shared.connectedScenes
            // Keep only active scenes, onscreen and visible to the user
            .filter { $0.activationState == .foregroundActive }
            // Keep only the first `UIWindowScene`
            .first(where: { $0 is UIWindowScene })
            // Get its associated windows
            .flatMap { $0 as? UIWindowScene }?.windows
            // Finally, keep only the key window
            .first(where: \.isKeyWindow)
    }

}

extension Bundle {

    var appName: String {
        object(forInfoDictionaryKey: "CFBundleDisplayName") as? String ?? object(forInfoDictionaryKey: "CFBundleName") as? String ?? ""
    }
}
