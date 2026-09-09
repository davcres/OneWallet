import SwiftUI
import OneWalletShared

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.doInitOneWallet()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
        }
    }
}
