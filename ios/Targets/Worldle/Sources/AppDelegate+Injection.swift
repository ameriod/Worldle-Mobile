import Resolver

extension Resolver: ResolverRegistering {
    public static func registerAllServices() {

        let scopeProviderHelper = ScopeProviderHelper()

        register {
            scopeProviderHelper.main()
        }
        .implements(ScopeProvider.self, name: .mainScopeProvider)
        .scope(.application)

        register {
            scopeProviderHelper.global()
        }
        .implements(ScopeProvider.self, name: .globalScopeProvider)
        .scope(.application)

        register {
            ViewModel(
                commonVM: GameViewModelHelper().viewModel(),
                scopeProvider: resolve(name: .globalScopeProvider)
            )
        }

    }
}

extension Resolver.Name {
    
    static var globalScopeProvider: Resolver.Name? {
        .name(fromString: "Global")
    }
    
    static var mainScopeProvider: Resolver.Name? {
        .name(fromString: "Main")
    }
}
