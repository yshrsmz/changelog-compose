# Design: ioDispatcher parameter

## Context

`ChangelogViewModel` のコンストラクタは既に `ioDispatcher` を受け取り、`withContext(ioDispatcher)` の中で `ChangelogParser.parseChangelog` を呼んでいる。ユニットテストはここに `TestDispatcher` を渡している。一方、`ChangelogViewModel.Factory` と `ChangelogContent` は dispatcher を受け取らず、本番の経路では常にコンストラクタの既定値 `Dispatchers.IO` が使われる。`ChangelogViewModel` と `Factory` は `internal` なので、利用側が触れるのは `ChangelogContent` だけである。

## Goals / Non-Goals

**Goals:**

- 利用側が `ChangelogContent` に dispatcher を渡し、UI テストで読み込みの完了を同期できるようにする
- 既存の呼び出し元を、ソースでもバイナリでも壊さない

**Non-Goals:**

- `ChangelogViewModel` や `ChangelogParser` を公開すること
- 読み込み以外（状態の更新や UI）の dispatcher を差し替えること

## Decisions

### 公開するのは `ChangelogContent` の引数だけにする

ViewModel やその `Factory` を公開して利用側に組み立てさせる案もあるが、利用側がしたいのは読み込みを行う dispatcher の差し替えだけである。`ChangelogContent` に `ioDispatcher` を足せば足りるので、ViewModel の層は `internal` のままにする。

### 既定値は公開関数にだけ持たせる

`Dispatchers.IO` という既定値は公開 `ChangelogContent` の引数にだけ書く。内部オーバーロード、`Factory`、`ChangelogViewModel` のコンストラクタでは必須の引数にする。`labels` を内部オーバーロードで必須にしているのと同じ扱いで、既定値が複数の層に重複しない。内部オーバーロードでは、`viewModel` の既定値から参照できるよう `ioDispatcher` を `viewModel` より前に置く。

### 変更前のシグネチャを HIDDEN の deprecated オーバーロードとして残す

Kotlin では既定値付きの引数を足しても、JVM 上のメソッドのシグネチャは変わる。1.0.0 に対してコンパイルされたライブラリやアプリが新しいバージョンと組み合わさると `NoSuchMethodError` になる。Compose の公式ライブラリと同じく、変更前の引数の公開 `ChangelogContent` を `@Deprecated(level = DeprecationLevel.HIDDEN)` で残し、新しい関数へ委譲する。HIDDEN なのでソースからは見えず、オーバーロードの解決に曖昧さは生じない。

### `kotlinx-coroutines-core` を `api` にする

公開関数の引数に `CoroutineDispatcher` が出るので、利用側のコンパイルにこの型が要る。CLAUDE.md の方針（公開 API に型を出す依存は `api`）に従い、`kotlinx-coroutines-core` を `api` にする。`kotlinx-coroutines-android` は `implementation` のままとする。

### テストでの使い道

利用側の Compose UI テストでは、`UnconfinedTestDispatcher()` を渡すと読み込みがコンポジションの中で同期的に終わり、`waitForIdle` の後には読み込み済みの状態になる。`StandardTestDispatcher` を渡せば、読み込み中の表示を確かめてからスケジューラを進めることもできる。

## Risks / Trade-offs

- `viewModel()` は ViewModelStoreOwner ごとに ViewModel を 1 度だけ作るので、作られた後に `ioDispatcher` を変えても反映されない。KDoc に書いておく
- `kotlinx-coroutines-core` が利用側のコンパイルクラスパスに入る。Compose を使うアプリでは既に依存しているので、実害は無い
