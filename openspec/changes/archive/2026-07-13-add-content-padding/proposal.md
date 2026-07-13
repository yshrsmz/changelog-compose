# Add contentPadding to the public ChangelogContent API

Issue: https://github.com/yshrsmz/changelog-compose/issues/69

## Why

targetSdk 35+ では edge-to-edge が強制されるため、利用側は `Scaffold` の `innerPadding` をリスト側の `contentPadding` として渡し、コンテンツがシステムバーの背後までスクロールできるようにする必要がある。しかし公開 API の `ChangelogContent(changelogResId, modifier)` は `contentPadding` を受け取れず、`Modifier.padding(innerPadding)` で回避するとナビゲーションバー位置でコンテンツがクリップされてしまう。内部オーバーロードには既に `contentPadding: PaddingValues = PaddingValues(16.dp)` が存在するが、ライブラリ外から到達できない。

## What Changes

- 公開 `ChangelogContent` オーバーロードに `contentPadding: PaddingValues = PaddingValues(16.dp)` パラメータを追加し、内部オーバーロードへ転送する
- デフォルト値は現行の内部実装と同一のため、既存利用者にとってソース互換・挙動互換
- KDoc（パラメータ説明・edge-to-edge での利用例）を更新する
- サンプルアプリ（`:app`）で `Scaffold` の `innerPadding` を `contentPadding` として渡す例に更新する
- 注: Kotlin のデフォルト引数追加のためバイナリ互換は破壊される（0.x のためリリースノートに記載の上で許容）

## Impact

- Affected specs: `changelog-display`（新規作成）
- Affected code:
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogContent.kt`（公開オーバーロード）
  - `app/src/main/java/com/codingfeline/changelog/sample/MainActivity.kt`（サンプル更新）
  - `README.md`（利用例の更新）
