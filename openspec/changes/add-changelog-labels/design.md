# Design: customizable labels

## 文言を 1 つのクラスにまとめる

個別の引数（`retryLabel`, `errorResourceNotFoundLabel`, ...）を `ChangelogContent` に並べると、文言が増えるたびに公開関数のシグネチャが伸びる。`ChangelogLabels` にまとめておけば、文言を足すときもクラスにデフォルト付きのプロパティを足すだけで済む。

`ChangelogLabels` は `data class` にしない。`data class` は `copy` や `componentN` が公開 API に入り、プロパティを足したときにバイナリ互換を壊しやすい。通常のクラスに `equals` / `hashCode` / `toString` を手で実装し、`@Immutable` を付ける。

## 文言は String で受け取る

エラー文を `(ChangelogError) -> String` のようなラムダで受け取る案もあるが、ラムダの中からは `stringResource` を呼べない（`@Composable` ではない）。利用側が `stringResource(R.string.xxx)` の結果をそのまま渡せるよう、すべて `String` で受け取る。

## エラーの種類は内部に閉じる

Issue ではエラーの種類（`ResourceNotFound` / `InvalidFormat` / `Io`）を公開して利用側に文言を決めさせる案も挙がっている。ただしエラー画面の描画は `ChangelogContent` 自身が行っており、利用側がエラーの型を受け取る場面は無い。種類ごとの文言を `ChangelogLabels` で受け取れば目的は果たせるので、エラーの型は `internal` のままにする。利用側がエラー画面そのものを差し替えたい要望が出たら、そのときに型を公開する。

## 例外メッセージの扱い

形式不正と読み込み失敗は、これまで `"Invalid changelog format: ${e.message}"` のように例外メッセージを後ろに付けていた。同じ見た目を保つため、UI 層で `message` が `null` でなければ `"<label>: <message>"`、`null` なら `<label>` だけを表示する。

## 対象外

- 変更種別のバッジ（`NEW` / `FIX` / `BREAKING`）は固定とする
- バージョン名の前に付く `v` も今回は触らない
