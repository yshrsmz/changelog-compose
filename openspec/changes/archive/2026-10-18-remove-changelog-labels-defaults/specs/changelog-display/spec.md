## MODIFIED Requirements

### Requirement: Customizable labels

公開 `ChangelogContent` composable は `labels: ChangelogLabels` パラメータを受け付け、エラー画面の文言（再試行ボタンとエラー文）に使わなければならない（MUST）。`ChangelogLabels` のコンストラクタは既定値を持たず、すべての文言を必須の引数として受け付けなければならない（MUST）。英語の既定の文言は `ChangelogLabels.Default` として提供し、各文言は変更前の英語の文言と同一でなければならない（MUST）。`ChangelogContent` の `labels` の既定値は `ChangelogLabels.Default` でなければならない（MUST）。`ChangelogLabels` は、指定した文言だけを置き換え、それ以外は呼び出し元の値を引き継ぐ `copy` を提供しなければならない（MUST）。変更種別のバッジ（`NEW` / `FIX` / `BREAKING`）は差し替えの対象にしない。

#### Scenario: 文言を差し替える

- **WHEN** 利用者が `stringResource` で取得した文言を `ChangelogLabels` に設定して `ChangelogContent` に渡す
- **AND** 変更履歴の読み込みに失敗する
- **THEN** エラー画面の再試行ボタンとエラー文は渡された文言で表示される

#### Scenario: 文言の書き忘れ

- **WHEN** 利用者が `ChangelogLabels` のコンストラクタに一部の文言だけを渡す
- **THEN** コンパイルエラーになる

#### Scenario: 一部の文言だけを置き換える

- **WHEN** 利用者が `ChangelogLabels.Default.copy(...)` に一部の文言だけを渡して `ChangelogContent` に渡す
- **AND** 変更履歴の読み込みに失敗する
- **THEN** 渡した文言はその値で、渡さなかった文言は `ChangelogLabels.Default` の英語の文言で表示される

#### Scenario: labels を指定しない

- **WHEN** 利用者が `labels` を指定せずに `ChangelogContent` を呼び出す
- **THEN** 再試行ボタンは `Retry`、エラー文は変更前と同じ英語の文言で表示される

#### Scenario: 例外メッセージを伴うエラー

- **WHEN** 変更履歴の XML の形式が不正、または読み込みに失敗し、例外がメッセージを持っている
- **THEN** エラー文は `"<該当する文言>: <例外メッセージ>"` の形で表示される
