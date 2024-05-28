import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    class Coordinator {
        var parent: ComposeView
        
        init(parent: ComposeView) {
            self.parent = parent
        }
        
        @objc func presentCameraViewController() {
            let cameraViewController = CameraViewController()
            if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
                rootViewController.present(cameraViewController, animated: true, completion: nil)
            }
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }
    
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(onClickTakePhoto: {
            context.coordinator.presentCameraViewController()
        })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        
    }
}

class CameraViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .blue
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



