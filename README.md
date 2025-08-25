# 制作背景

サービスの概要は、投資用不動産を容易に管理するための「不動産管理アプリ」です。<br>
現在所有している不動産やこれから行う予定の投資プロジェクトを物件ごとに管理することができます。<br>
想定しているユーザーとしては今現在不動産投資を行っている方です。<br>
古い体質の界隈故にエクセルやテキストでデータを管理している方も多い中、SaaSとしてあまりPCに詳しくない方でも使えるようにしました。<br>
自身もエクセルを使い不動産を管理していましたが、セルを選択し間違えるといったヒューマンエラーが発生しやすく致命傷になり得ることを危惧し以前から「不動産を簡単に管理できるようなアプリがあれば」という思いがあり作成に至りました。<br>
<br>
プログラミングスクール、RaiseTechでJavaを学習し、バックエンド部分をAIと相談しながらも自力で作成しました。<br>
フロントエンド部分については主にAIを使用し作成しておりますことご了承ください。

# アプリ名

アプリ名は「Simple iSvest（シンプル・インベスト）」です。iSvestをインベストと読みます。<br>
Simple is vest と Simple investを掛けた名前で誤字ではありません。<br>
他のサイト名と被らない独自性のある名前になるように工夫しました。

# デプロイURL

https://simple-isvest.com/

# 使用技術

- Java 21（バックエンド）
- Springboot（バックエンド）
- MySQL（バックエンド）
- React（フロントエンド）
- AWS（デプロイ）

# 機能一覧

- ログイン機能
- ゲストユーザーログイン機能
- ログアウト機能
- ユーザー登録
- ユーザー情報変更
- ユーザー削除
- 管理者によるユーザー一覧取得
- 管理者専用機能（ユーザー無効化、アカウントロックリセット、ユーザーロール変更）
- 一定回数のログイン失敗によるアカウントロック機能（ブルートフォースアタック対策）
- 不動産の一覧取得
- 不動産の検索
- 不動産の登録
- 不動産の修正
- 不動産の削除
- 融資を考慮した収支表示

# インフラ構成図

<img width="998" height="697" alt="AWSデプロイ構成 drawio" src="https://github.com/user-attachments/assets/060076fb-acdd-4880-a018-1552f30982c6" />

# ER図

<img width="771" height="1291" alt="ER図" src="https://github.com/user-attachments/assets/b068b19f-d699-4730-8e42-f4e7c9866e64" />

# 操作画面

## トップページ

<img width="1382" height="939" alt="top" src="https://github.com/user-attachments/assets/bc729fe4-246a-451e-9ed3-83a836647d19" />

- 記載のURLからアプリ紹介のランディングページが表示されます。

## ユーザーログイン・ゲストユーザーログイン

![トップからゲストログイン](https://github.com/user-attachments/assets/8ef034fc-e692-4210-aa29-82300f6baa41)

- ユーザー名とパスワードを入力することでログインできます。
- ゲストログインボタンで簡単にログインできます。
- Jwt+SpringSecurityを使いトークン方式認証処理を行っています。ログインが成功した場合Json形式でBearerトークンが返りブラウザ側でトークン情報を保存します。
- 認証が必要な不動産登録などの処理はAuthorizationヘッダーのBearerトークンを都度確認し認証を行い実行します。

## ログアウト

![ログアウト](https://github.com/user-attachments/assets/0a36f668-521a-46a3-9c62-7bc08f5b4797)

- 画面右上のメニューからログアウトできます。
- ブラウザからはトークン情報を削除します。

## ユーザー登録

![ユーザー登録バリデーション](https://github.com/user-attachments/assets/03e02527-8b73-49cd-af3b-c525c3e57956)

- トップページ中央の無料で始める または 画面右上の新規登録から、ユーザー登録画面を表示します。
- 登録済みのユーザー名、EmailはバックエンドのAuthServiceでバリデーションチェックをし、登録できないようにしています。
- パスワードは6文字以上でない場合、6文字以上で入力するメッセージが表示され登録できないようになっています（フロントエンドでもバリデーションチェックをしていますが、バックエンドのRegisterRequest
DTOでも@Sizeアノテーションによるバリデーションチェックを行っています）。
- ユーザー情報はAWS RDS内のMySQL DBのusersテーブルに保存されます。パスワードはBCryptPasswordEncoderにてハッシュ化され保存されます。ユーザー情報はID単位で管理し、IDはMySQL DBのAUTO_INCREMENTにて自動採番されます。

## ユーザー情報変更

![ユーザー情報変更](https://github.com/user-attachments/assets/60be0ada-09e5-4b75-86b2-545c4cc4396e)

- 画面右上メニューからユーザー情報を変更できます。
- 既に登録されているユーザー名やEmailはバックエンドのAuthServiceでバリデーションチェックをし、変更できないようにしています。

## 管理者専用画面

![管理者専用画面](https://github.com/user-attachments/assets/63307319-0b6f-4f33-a28b-71f4eae56b50)

- 管理者専用機能として全登録ユーザーの表示ができます。
- ログイン失敗回数などの情報が表示でき、不審な動きを監視しユーザーを無効化したりすることができます。
- 管理者専用機能は、バックエンド側で管理者専用API用のコントローラーを作り、ADMINロールを持つユーザーのみ実行できるようにしています。

## アカウントロック（ブルートフォースアタック対策）

![アカウントロック](https://github.com/user-attachments/assets/2089d7d8-e892-4282-af27-f11f95fd5504)

- 一定回数ログインに連続失敗するとアカウントロックがかかります。
- ログイン失敗の度にMySQL DB、usersテーブルのlogin_failed_attemptsに値が加算され、一定回数（既定では5回）のログイン連続失敗でアカウントロック解除までの期間（既定では1800秒後）がaccount_locked_untilに設定されます。以後アカウントロック解除期間を過ぎるまでは正しいパスワードを入力したとしてもログインできなくなります。

## 不動産の一覧取得・検索

![一覧・検索](https://github.com/user-attachments/assets/9b46d51c-c064-4763-b2b2-e56400fbca49)

- 登録された不動産一覧が表示されます。
- 検索条件から特定の物件のみ取得し一覧を表示することができます。
- MySQL DBからProject（プロジェクト）、Parcel（土地）、Building（建物）、IncomeAndExpenses（収支）オブジェクトとしてそれぞれ情報を取得し、RealestateDetailでまとめてJson形式でフロントエンドに出力します。
- フロントエンドからの検索パラメーターは、バックエンド側のSearchParams
  DTOで受けます。パラメーターに値が入った項目のみMapper XMLにてwhere、ifで絞り込みをします。

## 不動産の登録

![不動産登録](https://github.com/user-attachments/assets/ff050c6f-1699-4a5c-a7cb-d901cecfb8cf)

- ログイン後の上部メニューから不動産登録をする画面に遷移します。
- フロントエンドからはJson形式でプロジェクト、土地、建物、収支情報が送られ、バックエンドでは各オブジェクトを格納するRealestateDetailで情報を受けます。各オブジェクトには負の価格が入らないようにするなどアノテーションを使ったバリデーションチェックを施しています。
- 不動産情報はプロジェクトID単位で管理し、初めにプロジェクトのIDがMySQL DBのAUTO_INCREMENTで自動採番された後、土地、建物、収支情報にプロジェクトIDを設定します。

## 不動産の情報更新

![不動産情報更新](https://github.com/user-attachments/assets/314d0b7d-5e06-4eee-8e93-b52c113ff898)

- 登録した不動産は一覧の緑色のペンとメモのボタンを押すことで情報更新用モーダルが開き修正することができます。
- 不動産の登録と同様にフロントエンドからはJson形式で、バックエンドではRealestateDetailで情報を受けます。更新処理は各オブジェクトが持つ同一のユーザーID、プロジェクトIDを元に更新を行います。ユーザーIDを偽装し別ユーザーの不動産情報が改竄されることの対策として、JwtトークンからログインユーザーのIDを取得しユーザーIDが一致しない場合は例外を発生させ更新処理を行わないようにしています。

## 不動産の詳細情報

<img width="1230" height="963" alt="不動産詳細情報" src="https://github.com/user-attachments/assets/f691533a-573e-406d-af83-683e6e9c1356" />

- 登録した不動産は一覧の青色の目のボタンを押すことで詳細表示することができます。
- 一覧取得した情報をフロントエンドで再表示しています。

## 不動産の削除

![不動産削除](https://github.com/user-attachments/assets/9d301482-17bc-44b3-a738-389d61ec46ff)

- 登録した不動産は一覧の赤色のゴミ箱ボタンを押すことで削除することができます。
- 物理削除を行います。各オブジェクトにはisDeletedフィールドがありますが、将来的にデータを復元できる機能を実装することになった時の為に冗長構成を取っています。

## 融資を考慮した収支表示

![融資考慮](https://github.com/user-attachments/assets/a92b4b8c-9e4c-4c3b-bdf7-d6610d6965e7)

- 不動産一覧右上の融資を考慮しないチェックボックスにチェックを入れることで経費に融資を含めないで一覧表示することができます。これにより融資有無による不動産の収益性の比較をしやすくしています。
- 一覧取得した情報をフロントエンド側で計算法を変更し再計算しています。

# 工夫した点①

他のユーザーの不動産情報を取得、変更、削除できないようにトークンからユーザーIDを取得し、不動産情報に設定されているユーザーIDが一致している時のみ不動産情報を取得、変更、削除できるようにしました。
セキュリティを意識して構成することができたかと思います。

```
RealestateService.javaから一部抜粋

  @Transactional
  public void updateRealestate(RealestateDetail request, HttpServletRequest requestToken) {

    if (!isProjectIdConsistent(request)) {
      throw new IllegalArgumentException("プロジェクトIDが一致していません。");
    }

    // トークンから取得したユーザーIDとrequest内の各オブジェクトのユーザーIDを照合
    String token = jwtUtil.extractTokenFromRequest(requestToken);
    int userId = jwtUtil.getUserIdFromToken(token);

    if (!isUserIdConsistent(userId, request)) {
      throw new IllegalArgumentException("ユーザーIDが一致していません。");
    }
  ～～～


  private boolean isUserIdConsistent(int userId, RealestateDetail request) {
    return userId == request.getProject().getUserId() &&
        userId == request.getParcel().getUserId() &&
        userId == request.getBuilding().getUserId() &&
        userId == request.getIncomeAndExpenses().getUserId();
  }
```

# 工夫した点②

ブルートフォースアタック対策として、DBのusersテーブルにログイン連続失敗回数を記録し、一定回数記録されたら一定期間アカウントロックがかかるようにしました。

```
AuthService.javaからauthenticateメソッド一部抜粋

  public LoginResponse authenticate(LoginRequest loginRequest) {
  ～～～
    } catch (BadCredentialsException e) {
      accountLockService.handleLoginFailure(loginRequest);
      throw new BadCredentialsException("ユーザー名またはパスワードが間違っています。");
  ～～～

AccountLockService.javaから一部抜粋

  @Transactional
  public void handleLoginFailure(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

    if (user != null) {
      // ログイン連続失敗回数を加算します。
      int loginFailedAttempts = user.getLoginFailedAttempts() + 1;
      LocalDateTime accountLockedUntil = user.getAccountLockedUntil();

      // maxLoginAttemptsで指定された回数以上のログイン連続失敗回数となった場合、
      // accountLockDurationMinutesで指定された期間のアカウントロックがかかります。
      // 一度アカウントロックがかかった場合はアカウントロックがリセットされない限り再度アカウントロックがかからない仕様です。
      if (loginFailedAttempts >= maxLoginAttempts && user.getAccountLockedUntil() == null) {
        accountLockedUntil = LocalDateTime.now().plusMinutes(accountLockDurationMinutes);
      }
      userRepository.updateLoginFailed(user.getId(), loginFailedAttempts, accountLockedUntil);
    }
  }

  @Transactional
  public void unlockIfAccountLockExpired(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
    if (user != null && user.getAccountLockedUntil() != null) {
      if (LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
        userRepository.updateLoginFailed(user.getId(), 0, null);
      }
    }
  }
```

# 工夫した点③

ゲストログイン機能を実装し、ボタン一つでユーザー登録をすることなく機能を試せるようにしたことです。
予めDBのusersテーブルにゲスト用アカウントを作成し、ゲストログインボタンを押した時に(/guest-login)
予め設定しているゲストユーザーのユーザー名、パスワードを/loginエンドポイントメソッドに送る仕様となっています。

```
AuthController.javaから一部抜粋

  @PostMapping("/guest-login")
  @Operation(summary = "ゲストユーザーログイン", description = "ゲストユーザーでログインします")
  public ResponseEntity<?> guestLogin() {

    LoginRequest loginRequest = new LoginRequest("guest", "guest123");

    return login(loginRequest);
  }
```

# 今後追加したい機能

現在は基本的な不動産情報の取得、登録、変更、削除しかできませんが、プロジェクトごとに年間の詳細な収支を管理できるようにしたり、融資を受ける際に金融機関に提出する為の収支表をエクセル等の形式で出力し簡単に作成できるようにするといった機能の追加を検討しています。

# 終わりに

以上がこのアプリの概要となります。<br>
稚拙な文章でお見苦しい箇所もあったかと思います。<br>
お忙しい中ここまで見ていただきありがとうございました。
