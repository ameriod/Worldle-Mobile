import ProjectDescription

/// Project helpers are functions that simplify the way you define your project.
/// Share code to create targets, settings, dependencies,
/// Create your own conventions, e.g: a func that makes sure all shared targets are "static frameworks"
/// See https://docs.tuist.io/guides/helpers/

extension Project {
    /// Helper function to create the Project for this ExampleApp
    public static func app(
        name: String, platform: Platform,
        additionalFiles: [FileElement]
    ) -> Project {
        let targets = makeAppTargets(
            name: name,
            platform: platform,
            dependencies: [
                .xcframework(path: "../common/build/XCFrameworks/debug/common.xcframework")
            ]
        )
        return Project(
            name: name,
            organizationName: "nordeck.in",
            targets: targets,
            additionalFiles: additionalFiles
        )
    }

    /// Helper function to create the application target and the unit test target.
    private static func makeAppTargets(
        name: String,
        platform: Platform,
        dependencies: [TargetDependency]
    ) -> [Target] {
        let platform: Platform = platform
        let infoPlist: [String: InfoPlist.Value] = [
            "CFBundleShortVersionString": "1.0",
            "CFBundleVersion": "1",
            "UIMainStoryboardFile": "",
            "UILaunchStoryboardName": "LaunchScreen"
        ]

        let mainTarget = Target(
            name: name,
            platform: platform,
            product: .app,
            bundleId: "com.nordeck.\(name)",
            infoPlist: .extendingDefault(with: infoPlist),
            sources: [
                "Targets/\(name)/Sources/**"
            ],
            resources: [
                "Targets/\(name)/Resources/**",
                "../common/src/androidMain/assets/**"
            ],
            scripts: appScript,
            dependencies: dependencies
        )

        let testTarget = Target(
            name: "\(name)Tests",
            platform: platform,
            product: .unitTests,
            bundleId: "com.nordeck.\(name)Tests",
            infoPlist: .default,
            sources: [
                "Targets/\(name)/Tests/**"
            ],
            dependencies: [
                .target(name: "\(name)")
            ]
        )
        return [mainTarget, testTarget]
    }

    static var swfitlint: TargetScript {
        .pre(
            script: "swiftLint",
            name:
            """
            if which swiftlint >/dev/null; then
              swiftlint
            else
              echo "warning: SwiftLint not installed, download from https://github.com/realm/SwiftLint"
            fi
            """
        )
    }

    static var swiftformat: TargetScript {
        .pre(
            script: "swiftformat",
            name:
            """
            if which swiftformat >/dev/null; then
            swiftformat .
            else
            echo "warning: SwiftFormat not installed, download from https://github.com/nicklockwood/SwiftFormat"
            fi
            """
        )
    }

    static var appScript: [TargetScript] {
        [
            swfitlint,
            swiftformat
        ]
    }
}
