# Add an ioDispatcher parameter to the public ChangelogContent API

## Why

`ChangelogViewModel` は変更履歴の XML を `Dispatchers.IO` で読み込むが、公開 `ChangelogContent` にはその dispatcher を差し替える口が無い。そのため利用側の Compose UI テストは読み込みの完了を `waitForIdle` などで同期できず、`waitUntil` で実時間を待つしかない。yshrsmz/linkrelay-android の `ChangelogScreenTest` はこれが原因で flaky になり、linkrelay-android#186 で暫定的に待ち時間を延ばして対処している。

## What Changes

- 公開 `ChangelogContent` に `ioDispatcher: CoroutineDispatcher = Dispatchers.IO` を追加する。XML の読み込みと解析はこの dispatcher で行う
- 内部オーバーロードの `ChangelogContent` と `ChangelogViewModel.Factory` に `ioDispatcher` を通し、`ChangelogViewModel` に渡す
- 変更前のシグネチャの公開 `ChangelogContent` を `@Deprecated(level = DeprecationLevel.HIDDEN)` で残し、変更前のバージョンに対してコンパイルされたバイナリからの呼び出しを壊さない
- 公開 API の型として `CoroutineDispatcher` が出るため、`kotlinx-coroutines-core` の依存を `implementation` から `api` に変える
- KDoc と README に、引数の使い方と UI テストでの使い道を書く
- 既定値付きの引数の追加なので、既存の呼び出し元はソースもバイナリも変更不要

## Capabilities

### New Capabilities

なし

### Modified Capabilities

- `changelog-display`: 公開 `ChangelogContent` が読み込みに使う dispatcher を受け付ける要件を追加する

## Impact

- Affected specs: `changelog-display`
- Affected code:
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogContent.kt`
  - `changelog/src/main/java/com/codingfeline/changelog/internal/viewmodel/ChangelogViewModel.kt`
  - `changelog/src/test/java/com/codingfeline/changelog/ChangelogViewModelTest.kt`
  - `changelog/build.gradle.kts`（`kotlinx-coroutines-core` を `api` に変更）
  - `README.md`
- 公開 API への追加なので release-please では minor リリース（1.0.0 → 1.1.0）になる
