import ProjectDescription
import ProjectDescriptionHelpers

let infoPlist: [String: InfoPlist.Value] = [
    "CFBundleShortVersionString": "1.0",
    "CFBundleVersion": "1",
    "UIMainStoryboardFile": "",
    "UILaunchStoryboardName": "LaunchScreen"
]

let name = "Worldle"

let project = Project(
    name: name,
    organizationName: "nordeck.in",
    packages: [
        .remote(url: "https://github.com/exyte/SVGView", requirement: .exact("1.0.4")),
        .remote(url: "https://github.com/ameriod/ForestKit", requirement: .exact("0.1.0")),
        .remote(url: "https://github.com/hmlongco/Resolver", requirement: .exact("1.5.0")),
    ],
    targets: [
        Target(name: name,
               platform: .iOS,
               product: .app,
               bundleId: "com.nordeck.app.\(name)",
               infoPlist: .extendingDefault(with: infoPlist),
               sources: [
                   "Targets/\(name)/Sources/**"
               ],
               resources: [
                   "Targets/\(name)/Resources/**",
                   "../common/src/androidMain/assets/**"
               ],
               scripts: [
                   .swfitlint,
                   .swiftformat
               ],
               dependencies: [
                   .xcframework(path: "../common/build/XCFrameworks/debug/common.xcframework"),
                   .package(product: "SVGView"),
                   .package(product: "ForestKit"),
                   .package(product: "Resolver"),
               ])
    ],
    additionalFiles: [
        "../README.md"
    ]
)

extension TargetScript {

    static var swfitlint: TargetScript {
        .pre(
            script:
            """
            if which swiftlint >/dev/null; then
              swiftlint
            else
              echo "warning: SwiftLint not installed, download from https://github.com/realm/SwiftLint"
            fi
            """,
            name: "swiftlint"
        )
    }

    static var swiftformat: TargetScript {
        .pre(
            script:
            """
            if which swiftformat >/dev/null; then
            swiftformat .
            else
            echo "warning: SwiftFormat not installed, download from https://github.com/nicklockwood/SwiftFormat"
            fi
            """,
            name: "swiftformat"
        )
    }
}
