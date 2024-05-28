import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    // Coordinatorを介して処理を記述できる
    class Coordinator {
        var parent: ComposeView
        
        init(parent: ComposeView) {
            self.parent = parent
        }
        
        @objc func presentCameraViewController() {
            let cameraViewController = CameraViewController()
            // フルスクリーンで表示する
            cameraViewController.modalPresentationStyle = .fullScreen
            if let rootViewController = UIApplication.shared.windows.first?.rootViewController {
                // 第一引数：ViewController
                // 第二引数：アニメーションの有無
                // 第三引数：コールバック処理
                rootViewController.present(cameraViewController, animated: true, completion: nil)
            }
        }
    }
    
    // [必須] Coordinatorを登録する
    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }
    
    
    // [必須] ビューコントローラーを作る. SwiftはmakeXXXでビルダーを表す.
    func makeUIViewController(context: Context) -> UIViewController {
        // KMPで定義したViewControllerを呼び出す
        MainViewControllerKt.MainViewController(onClickTakePhoto: {
            context.coordinator.presentCameraViewController()
        })
    }

    // Viewが更新された時に呼ばれるらしいが使い所は不明
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        
    }
}

// カメラ画面
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



