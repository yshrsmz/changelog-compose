# changelog-display Specification

## Purpose
Android アプリ内で変更履歴（changelog）を表示する Compose UI を提供する。
`res/raw` に置いた XML 形式の変更履歴を読み込み、リリースバージョンごとにまとめ、必要に応じて変更種別（FIX / NEW / BREAKING）を表示しつつ Material 3 のリストとして描画する。
公開 API である `ChangelogContent` composable は `contentPadding` で余白などの表示を調整でき、`Scaffold` の `innerPadding` を渡すことで edge-to-edge 構成でもコンテンツがシステムバーと干渉しないようにする。
また `labels`（`ChangelogLabels`）で、読み込みに失敗したときの再試行ボタンとエラー文の文言を差し替えられる。

## Requirements

### Requirement: Public contentPadding parameter

公開 `ChangelogContent` composable は `contentPadding: PaddingValues` パラメータを受け付け、変更履歴リスト（`LazyColumn`）の `contentPadding` として適用しなければならない（MUST）。デフォルト値は `PaddingValues(16.dp)` とし、従来の表示と同一でなければならない（MUST）。

#### Scenario: Scaffold の innerPadding を渡す（edge-to-edge）

- **WHEN** 利用者が `Scaffold` の `innerPadding` を `ChangelogContent` の `contentPadding` に渡す
- **THEN** リストのコンテンツはシステムバーの背後まで描画され、末尾までスクロールした際に最後の項目がナビゲーションバーと重ならない位置に収まる

#### Scenario: contentPadding を指定しない

- **WHEN** 利用者が `contentPadding` を指定せずに `ChangelogContent` を呼び出す
- **THEN** 従来どおり `PaddingValues(16.dp)` が適用され、表示は変更前と同一である

### Requirement: Customizable labels

公開 `ChangelogContent` composable は `labels: ChangelogLabels` パラメータを受け付け、エラー画面の文言（再試行ボタンとエラー文）に使わなければならない（MUST）。`ChangelogLabels` の各文言の既定値は変更前の英語の文言と同一でなければならない（MUST）。変更種別のバッジ（`NEW` / `FIX` / `BREAKING`）は差し替えの対象にしない。

#### Scenario: 文言を差し替える

- **WHEN** 利用者が `stringResource` で取得した文言を `ChangelogLabels` に設定して `ChangelogContent` に渡す
- **AND** 変更履歴の読み込みに失敗する
- **THEN** エラー画面の再試行ボタンとエラー文は渡された文言で表示される

#### Scenario: labels を指定しない

- **WHEN** 利用者が `labels` を指定せずに `ChangelogContent` を呼び出す
- **THEN** 再試行ボタンは `Retry`、エラー文は変更前と同じ英語の文言で表示される

#### Scenario: 例外メッセージを伴うエラー

- **WHEN** 変更履歴の XML の形式が不正、または読み込みに失敗し、例外がメッセージを持っている
- **THEN** エラー文は `"<該当する文言>: <例外メッセージ>"` の形で表示される
