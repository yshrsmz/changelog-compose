# changelog-display delta

## ADDED Requirements

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
