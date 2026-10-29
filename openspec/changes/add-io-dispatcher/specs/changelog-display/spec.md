# changelog-display delta

## ADDED Requirements

### Requirement: Injectable io dispatcher

公開 `ChangelogContent` composable は `ioDispatcher: CoroutineDispatcher` パラメータを受け付け、変更履歴の XML の読み込みと解析をその dispatcher で行わなければならない（MUST）。デフォルト値は `Dispatchers.IO` とし、従来の動作と同一でなければならない（MUST）。引数を追加する前のシグネチャで `ChangelogContent` を呼ぶ既存のソースとバイナリは、変更なしで動作し続けなければならない（MUST）。

#### Scenario: ioDispatcher を指定しない

- **WHEN** 利用者が `ioDispatcher` を指定せずに `ChangelogContent` を呼び出す
- **THEN** 変更履歴は従来どおり `Dispatchers.IO` で読み込まれ、表示は変更前と同一である

#### Scenario: テストで TestDispatcher を渡す

- **WHEN** 利用者が Compose UI テストで `TestDispatcher` を `ChangelogContent` の `ioDispatcher` に渡す
- **THEN** 変更履歴の読み込みと解析はその dispatcher で行われ、テストはテストのスケジューラで読み込みの完了を同期できる

#### Scenario: 変更前のバージョンでコンパイルされたバイナリ

- **WHEN** `ioDispatcher` を追加する前のバージョンに対してコンパイルされたコードが `ChangelogContent` を呼び出す
- **THEN** `NoSuchMethodError` にならず、`Dispatchers.IO` で読み込んで表示する
