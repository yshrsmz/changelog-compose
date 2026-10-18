# Tasks

## 1. Implementation

- [x] 1.1 `ChangelogLabels` のコンストラクタから既定値を外し、`companion object` に英語の文言を持つ `Default` を追加する
- [x] 1.2 `ChangelogLabels` に `copy(retry, errorResourceNotFound, errorInvalidFormat, errorReadFailed)` を追加する（各引数の既定値は呼び出し元の値）
- [x] 1.3 `ChangelogLabels` の KDoc を、全部を渡すか `Default.copy(...)` を使う説明に改める
- [x] 1.4 公開 `ChangelogContent` の `labels` の既定値を `ChangelogLabels.Default` にし、`@param labels` の KDoc を更新する
- [x] 1.5 `ChangelogErrorTextTest` を新しい API に合わせて更新し（`ChangelogLabels()` → `ChangelogLabels.Default`、文言を差し替えるテストで 4 つすべてを渡す）、`copy` で一部だけ置き換えるテストを追加する
- [x] 1.6 README の「Customizing Labels」を更新する

## 2. Verification

- [x] 2.1 `./gradlew :changelog:test` が通ることを確認する
- [x] 2.2 `./gradlew build` が通ることを確認する
