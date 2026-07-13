# Tasks

## 1. Implementation

- [x] 1.1 公開 `ChangelogContent` に `contentPadding: PaddingValues = PaddingValues(16.dp)` を追加し、内部オーバーロードへ転送する
- [x] 1.2 KDoc に `@param contentPadding` の説明と edge-to-edge（`Scaffold` の `innerPadding`）での利用例を追記する
- [x] 1.3 サンプルアプリで `Scaffold` の `innerPadding` を `contentPadding` に渡す形へ更新する
- [x] 1.4 README の利用例を更新する

## 2. Verification

- [x] 2.1 `./gradlew :changelog:build` が通ることを確認する
- [x] 2.2 サンプルアプリでリストがナビゲーションバーの背後までスクロールすることを確認する
