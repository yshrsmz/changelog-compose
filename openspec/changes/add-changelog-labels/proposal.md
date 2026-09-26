# Add customizable labels to the public ChangelogContent API

Issue: https://github.com/yshrsmz/changelog-compose/issues/114

## Why

公開 `ChangelogContent` からは、画面に出る文言（`Retry` ボタン、読み込み失敗時のエラー文）を差し替えられない。日本語のアプリに組み込むと、本体は日本語なのにこれらだけ英語で表示される。利用側は `stringResource` で持っている文言を渡したいが、渡す口が無い。README の Features には "Configurable padding and labels" とあるのに、実際に公開されている引数は padding だけになっている。

## What Changes

- 公開 `ChangelogLabels` クラスを追加する。フィールドはすべて `String` で、既定値は現在の英語の文言
  - `retry`（既定: `Retry`）
  - `errorResourceNotFound`（既定: `Changelog resource not found`）
  - `errorInvalidFormat`（既定: `Invalid changelog format`）
  - `errorReadFailed`（既定: `Failed to read changelog`）
- 公開 `ChangelogContent` に `labels: ChangelogLabels = ChangelogLabels()` を追加し、内部オーバーロードの `retryLabel` を置き換える
- `ChangelogViewModel` でのエラー文の組み立てをやめる。エラーの種類（リソースが無い / 形式が不正 / 読み込み失敗）と例外メッセージだけを状態に持たせ、UI 層で `labels` から文言を選ぶ
  - 例外メッセージがある場合は `"<label>: <message>"` の形で表示し、既定値のままなら従来と同じ文字列になる
- 変更種別のバッジ（`NEW` / `FIX` / `BREAKING`）は固定のままとし、差し替えの対象にしない
- KDoc、README、サンプルアプリを更新する
- 注: Kotlin のデフォルト引数追加のためバイナリ互換は破壊される（0.x のためリリースノートに記載の上で許容）

## Impact

- Affected specs: `changelog-display`
- Affected code:
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogContent.kt`
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogLabels.kt`（新規）
  - `changelog/src/main/java/com/codingfeline/changelog/internal/viewmodel/ChangelogViewModel.kt`
  - `changelog/src/main/java/com/codingfeline/changelog/internal/ui/ChangelogSupportingScreens.kt`
  - `changelog/src/test/java/com/codingfeline/changelog/ChangelogViewModelTest.kt`
  - `app/`（サンプル）、`README.md`
