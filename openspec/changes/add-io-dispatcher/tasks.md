# Tasks

## 1. Implementation

- [ ] 1.1 `ChangelogViewModel.Factory` に `ioDispatcher` を追加して `ChangelogViewModel` に渡し、コンストラクタの既定値 `Dispatchers.IO` を外す
- [ ] 1.2 内部オーバーロードの `ChangelogContent` に `ioDispatcher` を追加し、`Factory` に渡す
- [ ] 1.3 公開 `ChangelogContent` に `ioDispatcher: CoroutineDispatcher = Dispatchers.IO` を追加し、KDoc に `@param ioDispatcher` とテストでの使い道を書く
- [ ] 1.4 変更前のシグネチャの公開 `ChangelogContent` を `@Deprecated(level = DeprecationLevel.HIDDEN)` で残す
- [ ] 1.5 `kotlinx-coroutines-core` の依存を `api` に変える
- [ ] 1.6 `Factory` で作った ViewModel が渡した dispatcher で解析することを確かめるテストを `ChangelogViewModelTest` に追加する
- [ ] 1.7 README に `ioDispatcher` の使い方と UI テストでの例を書く

## 2. Verification

- [ ] 2.1 `./gradlew testDebugUnitTest` が通ることを確認する
- [ ] 2.2 `./gradlew assembleDebug` が通ることを確認する
