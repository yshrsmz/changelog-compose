# Tasks

## 1. Implementation

- [ ] 1.1 公開 `ChangelogLabels` クラスを追加する（`retry` / `errorResourceNotFound` / `errorInvalidFormat` / `errorReadFailed`、既定値は現在の英語）
- [ ] 1.2 `ChangelogViewModel` がエラー文を組み立てるのをやめ、内部のエラー型（種類 + 例外メッセージ）を状態に持たせる
- [ ] 1.3 エラー画面で `labels` から文言を選んで表示する
- [ ] 1.4 公開 `ChangelogContent` に `labels: ChangelogLabels = ChangelogLabels()` を追加し、内部オーバーロードの `retryLabel` を置き換える。KDoc に `@param labels` を追記する
- [ ] 1.5 `ChangelogViewModelTest` をエラー型に合わせて更新し、リソースが無い場合と読み込み失敗のテストを追加する
- [ ] 1.6 サンプルアプリで `strings.xml` の文言を `ChangelogLabels` に渡す
- [ ] 1.7 README に文言の差し替え方を追記する

## 2. Verification

- [ ] 2.1 `./gradlew :changelog:test` が通ることを確認する
- [ ] 2.2 `./gradlew build` が通ることを確認する
