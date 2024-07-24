import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(
    onClickTakePhoto: () -> Unit,
) = ComposeUIViewController {
    App(
        answer = "",
        onClickTakePhoto = onClickTakePhoto,
        createBanner = {},
    )
}