# Remove default values from the ChangelogLabels constructor

Issue: https://github.com/yshrsmz/changelog-compose/issues/133

## Why

`ChangelogLabels` の 4 つの引数はどれも英語の既定値を持っているため、利用者が自分で `ChangelogLabels(...)` を組むときに 1 つ書き忘れてもコンパイルが通り、書き忘れた文言だけが英語のまま画面に出る。ライブラリがラベルを足したときも、利用者は版を上げても新しい文言が英語で出ることに気づけない。エラー文は失敗したときにしか出ず、`ReadFailed` のように失敗の状況を作りにくいものもあるため、利用者側のテストでも拾いにくい。

## What Changes

- **BREAKING** `ChangelogLabels` のコンストラクタから既定値を外し、4 つの引数（`retry` / `errorResourceNotFound` / `errorInvalidFormat` / `errorReadFailed`）をすべて必須にする
- 英語の既定の文言を `ChangelogLabels.Default`（`companion object` の定数）に移す
- 公開 `ChangelogContent` の `labels` の既定値を `ChangelogLabels.Default` にする。`labels` を渡さない利用者の表示は変わらない
- 一部の文言だけを置き換える口として `copy(...)` を追加する。引数の既定値は呼び出し元の値とし、`ChangelogLabels.Default.copy(retry = ...)` のように使う
  - `ChangelogLabels` は data class にしない（`componentN` を公開しないため、#118 と同じく `equals` / `hashCode` / `toString` は手書きのまま）
  - トレードオフ: `Default.copy(...)` で組んだ利用者は、将来ラベルが増えたときに新しい文言が英語のまま出る。ただしそれは利用者が `Default` を土台にすると明示的に選んだ場合に限られ、コンストラクタで全部を組む利用者は書き忘れをコンパイルで検出できる。部分的な置き換えを提供しない案もあるが、既存の「置き換えたいものだけ渡す」使い方の移行先が無くなるため、`copy` を入れる案を推す
- KDoc と README の「置き換えたいものだけ渡せばよい」という説明を、全部を渡すか `Default.copy(...)` を使う説明に改める
- 注: 0.x のため、破壊的変更としてリリースノートに記載した上で許容する

## Impact

- Affected specs: `changelog-display`
- Affected code:
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogLabels.kt`
  - `changelog/src/main/java/com/codingfeline/changelog/ChangelogContent.kt`
  - `changelog/src/test/java/com/codingfeline/changelog/ChangelogErrorTextTest.kt`
  - `README.md`
- サンプルアプリ（`app/`）はすでに 4 つの引数をすべて渡しているため変更不要
- 移行: `ChangelogLabels()` は `ChangelogLabels.Default` に、一部だけを渡していた `ChangelogLabels(retry = ...)` は全部を渡すか `ChangelogLabels.Default.copy(retry = ...)` に置き換える
