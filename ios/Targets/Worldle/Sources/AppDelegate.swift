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
